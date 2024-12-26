package clean_code.seminar_registration;

import clean_code.seminar_registration.domain.lecture.Lecture;
import clean_code.seminar_registration.repository.enrollment.LectureEnrollmentRepository;
import clean_code.seminar_registration.repository.lecture.LectureRepository;
import clean_code.seminar_registration.service.enrollment.LectureEnrollmentService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@ActiveProfiles("test")
public class LectureEnrollmentIntegrationTest {

    @Autowired
    private LectureEnrollmentService lectureEnrollmentService;

    @Autowired
    private LectureRepository lectureRepository;

    @Autowired
    private LectureEnrollmentRepository lectureEnrollmentRepository;

    @Test
    @DisplayName("동시에 40명의 사용자가 강의를 신청할 때 30명에 대해서만 신청이 완료된다.")
    void enrollLectureConcurrently() throws InterruptedException {
        // Given
        Lecture lecture = lectureRepository.save(
                Lecture.builder()
                        .lectureName("How to master Java")
                        .speakerName("Martin Fowler")
                        .lectureDate(LocalDateTime.now())
                        .available(true)
                        .build());

        int numberOfThreads = 40;
        ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);
        CountDownLatch latch = new CountDownLatch(numberOfThreads);
        AtomicInteger successCount = new AtomicInteger(0);

        // When
        for (int i = 0; i < numberOfThreads; i++) {
            Long userId = (long) i;
            executorService.submit(() -> {
                try {
                    lectureEnrollmentService.enrollLecture(userId, lecture.getId());
                    successCount.incrementAndGet();

                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await(10, TimeUnit.SECONDS);
        executorService.shutdown();

        if (!executorService.awaitTermination(5, TimeUnit.SECONDS)) {
            executorService.shutdownNow();
        }

        // Then
        assertAll(
                () -> assertEquals(30, lectureEnrollmentRepository.countAllByLectureId(lecture.getId())),
                () -> assertEquals(30, successCount.get()));
    }

    @Test
    @DisplayName("동일한 유저 정보로 같은 특강을 5번 신청했을 때, 1번만 성공하는 것을 검증")
    void duplicatedUserInLectureTest() throws InterruptedException {
        // Given
        Lecture lecture = lectureRepository.save(
                Lecture.builder()
                        .lectureName("How to master JavaScript")
                        .speakerName("Martin Fowler")
                        .lectureDate(LocalDateTime.now())
                        .available(true)
                        .build());

        int numberOfThreads = 5;
        ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);
        CountDownLatch latch = new CountDownLatch(numberOfThreads);
        AtomicInteger successCount = new AtomicInteger(0);

        // When
        Long userId = 1L;
        for (int i = 0; i < numberOfThreads; i++) {
            executorService.submit(() -> {
                try {
                    lectureEnrollmentService.enrollLecture(userId, lecture.getId());
                    successCount.incrementAndGet();

                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await(10, TimeUnit.SECONDS);
        executorService.shutdown();

        if (!executorService.awaitTermination(5, TimeUnit.SECONDS)) {
            executorService.shutdownNow();
        }

        // Then
        assertAll(
                () -> assertEquals(1, lectureEnrollmentRepository.countAllByLectureId(lecture.getId())),
                () -> assertEquals(true, lectureEnrollmentRepository.existByLectureIdAndUserId(lecture.getId(),
                        userId)),
                () -> assertEquals(1, successCount.get()));
    }

}
