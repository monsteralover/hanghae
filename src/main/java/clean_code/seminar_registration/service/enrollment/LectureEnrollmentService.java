package clean_code.seminar_registration.service.enrollment;

import clean_code.seminar_registration.domain.enrollment.LectureEnrollment;
import clean_code.seminar_registration.domain.lecture.Lecture;
import clean_code.seminar_registration.exception.DuplicateUserRegistrationException;
import clean_code.seminar_registration.repository.enrollment.LectureEnrollmentRepository;
import clean_code.seminar_registration.repository.enrollment.dto.UserLectureEnrollmentsDto;
import clean_code.seminar_registration.repository.lecture.LectureRepository;
import clean_code.seminar_registration.service.enrollment.response.SearchedLectureEnrollment;
import clean_code.seminar_registration.service.enrollment.response.SearchedLectureEnrollmentResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LectureEnrollmentService {

    private final LectureEnrollmentRepository lectureEnrollmentRepository;
    private final LectureRepository lectureRepository;

    public SearchedLectureEnrollmentResponse getUserEnrollments(final Long userId) {
        final List<UserLectureEnrollmentsDto> userEnrollments = lectureEnrollmentRepository.getUserEnrollments(userId);
        final List<SearchedLectureEnrollment> searchedLectureEnrollments =
                userEnrollments.stream()
                        .map(enrollment -> SearchedLectureEnrollment.from(enrollment))
                        .toList();

        return SearchedLectureEnrollmentResponse.builder().userEnrollments(searchedLectureEnrollments).build();
    }

    @Transactional
    public Long enrollLecture(final Long userId, final Long lectureId) {

        final Lecture lecture = lectureRepository.findByIdWithPessimisticLock(lectureId);
        lecture.validateIfLectureIsAvailable(lecture.isAvailable());

        if (lectureEnrollmentRepository.existByLectureIdAndUserId(lectureId, userId)) {
            throw new DuplicateUserRegistrationException("이미 등록된 사용자입니다.");
        }

        //등록
        final LectureEnrollment savedEnrollment = lectureEnrollmentRepository.enroll(LectureEnrollment.create(userId,
                lectureId));

        final Long usersInLecture = lectureEnrollmentRepository.countAllByLectureId(lectureId);
        if (savedEnrollment.isMaxUserSizeForLecture(usersInLecture)) {
            lectureRepository.save(lecture.updateEnrollAnAvailable(lectureId));
        }

        return savedEnrollment.getId();
    }
}
