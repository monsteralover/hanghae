package clean_code.seminar_registration;

import clean_code.seminar_registration.domain.enrollment.LectureEnrollment;
import clean_code.seminar_registration.domain.lecture.Lecture;
import clean_code.seminar_registration.exception.DuplicateUserRegistrationException;
import clean_code.seminar_registration.exception.MaxEnrollmentExceededException;
import clean_code.seminar_registration.repository.enrollment.LectureEnrollmentRepository;
import clean_code.seminar_registration.repository.lecture.LectureRepository;
import clean_code.seminar_registration.service.enrollment.LectureEnrollmentService;
import clean_code.seminar_registration.service.enrollment.response.SearchedLectureEnrollmentResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Collections;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.catchThrowable;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LectureEnrollmentTest {
    @Mock
    private LectureEnrollmentRepository lectureEnrollmentRepository;
    @Mock
    private LectureRepository lectureRepository;

    @InjectMocks
    private LectureEnrollmentService lectureEnrollmentService;

    @DisplayName("사용자가 강의를 신청하지 않은 경우 신청 내역 조회 시 빈 리스트를 반환한다.")
    @Test
    void returnsEmptyListWhenUserNotEnrolled() {

        // Given
        Long userId = 1L;
        when(lectureEnrollmentRepository.getUserEnrollments(userId))
                .thenReturn(Collections.emptyList());

        // When
        SearchedLectureEnrollmentResponse response = lectureEnrollmentService.getUserEnrollments(userId);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getUserEnrollments()).isEqualTo(Collections.emptyList());
        verify(lectureEnrollmentRepository).getUserEnrollments(userId);
    }

    @DisplayName("강의가 매진된 경우 MaxEnrollmentExceededException을 발생시킨다.")
    @Test
    void throwExceptionLectureUnavailableTest() {
        // given
        final Lecture lecture = Lecture.builder()
                .id(1L)
                .lectureName("How to master Java")
                .speakerName("MartinFowler")
                .lectureDate(LocalDateTime.now())
                .available(false)
                .build();

        when(lectureRepository.findById(1L)).thenReturn(lecture);

        // when
        Throwable throwable = catchThrowable(() ->
                lectureEnrollmentService.enrollLecture(1L, 1L));

        // then
        assertThat(throwable)
                .isInstanceOf(MaxEnrollmentExceededException.class);
        verify(lectureRepository).findById(1L);
    }

    @DisplayName("같은 사용자는 같은 강의에 대해 1번 이상 등록하려고 하면 DuplicateUserRegistrationException이 발생한다.")
    @Test
    void userPerLectureNotDuplicatedTest() {
        // given
        Long userId = 2L;
        Long lectureId = 2L;

        Lecture lecture = Lecture.builder()
                .id(lectureId)
                .lectureName("How to master Java")
                .speakerName("MartinFowler")
                .lectureDate(LocalDateTime.now())
                .available(true)
                .build();

        when(lectureRepository.findById(lectureId)).thenReturn(lecture);
        when(lectureEnrollmentRepository.existByLectureIdAndUserId(lectureId, userId)).thenReturn(true);

        // when
        Throwable throwable = catchThrowable(() ->
                lectureEnrollmentService.enrollLecture(userId, lectureId));

        // then
        assertThat(throwable)
                .isInstanceOf(DuplicateUserRegistrationException.class);
        verify(lectureRepository).findById(userId);
        verify(lectureEnrollmentRepository).existByLectureIdAndUserId(lectureId, userId);

    }

    @DisplayName("최종적으로 등록하는 사람의 경우 (정원이 마감되는), 해당 강의를 마감 처리한다.")
    @Test
    void updateAvailabilityWhenMaxCapacityReached() {
        // given
        Long userId = 2L;
        Long lectureId = 2L;
        Long lectureEnrollmentId = 5L;

        Lecture lecture = Lecture.builder()
                .id(lectureId)
                .lectureName("How to master Java")
                .speakerName("MartinFowler")
                .lectureDate(LocalDateTime.now())
                .available(true)
                .build();

        LectureEnrollment savedEnrollment = new LectureEnrollment(lectureEnrollmentId, userId, lectureId);

        when(lectureRepository.findById(lectureId)).thenReturn(lecture);
        when(lectureEnrollmentRepository.existByLectureIdAndUserId(lectureId, userId)).thenReturn(false);
        when(lectureEnrollmentRepository.enroll(any(LectureEnrollment.class))).thenReturn(savedEnrollment);
        when(lectureEnrollmentRepository.countAllByLectureId(lectureId)).thenReturn(30L);

        // when
        lectureEnrollmentService.enrollLecture(userId, lectureId);

        // then
        assertThat(savedEnrollment.getId()).isEqualTo(lectureEnrollmentId);
        verify(lectureRepository).findById(lectureId);
        verify(lectureEnrollmentRepository).existByLectureIdAndUserId(lectureId, userId);
        verify(lectureEnrollmentRepository).enroll(any(LectureEnrollment.class));
        verify(lectureEnrollmentRepository).countAllByLectureId(lectureId);
        verify(lectureRepository).save(any(Lecture.class));
    }

    @DisplayName("아직 등록할 수 있는 경우 강의를 마감 처리하지 않고, 등록Id를 반환한다.")
    @Test
    void lectureEnrollmentIdReturnedAfterEnrollSuccess() {
        // given
        Long userId = 2L;
        Long lectureId = 2L;
        Long lectureEnrollmentId = 5L;

        Lecture lecture = Lecture.builder()
                .id(lectureId)
                .lectureName("How to master Java")
                .speakerName("MartinFowler")
                .lectureDate(LocalDateTime.now())
                .available(true)
                .build();

        LectureEnrollment savedEnrollment = new LectureEnrollment(lectureEnrollmentId, userId, lectureId);

        when(lectureRepository.findById(lectureId)).thenReturn(lecture);
        when(lectureEnrollmentRepository.existByLectureIdAndUserId(lectureId, userId)).thenReturn(false);
        when(lectureEnrollmentRepository.enroll(any(LectureEnrollment.class))).thenReturn(savedEnrollment);
        when(lectureEnrollmentRepository.countAllByLectureId(lectureId)).thenReturn(10L);

        // when
        lectureEnrollmentService.enrollLecture(userId, lectureId);

        // then
        assertThat(savedEnrollment.getId()).isEqualTo(lectureEnrollmentId);
        verify(lectureRepository).findById(lectureId);
        verify(lectureEnrollmentRepository).existByLectureIdAndUserId(lectureId, userId);
        verify(lectureEnrollmentRepository).enroll(any(LectureEnrollment.class));
        verify(lectureEnrollmentRepository).countAllByLectureId(lectureId);
        verify(lectureRepository, never()).save(any(Lecture.class));
    }

}
