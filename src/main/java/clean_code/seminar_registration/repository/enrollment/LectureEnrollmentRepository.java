package clean_code.seminar_registration.repository.enrollment;

import clean_code.seminar_registration.domain.enrollment.LectureEnrollment;
import clean_code.seminar_registration.repository.enrollment.dto.UserLectureEnrollmentsDto;

import java.util.List;

public interface LectureEnrollmentRepository {

    Long countAllByLectureId(Long lectureId, Long userId);

    List<UserLectureEnrollmentsDto> getUserEnrollments(Long userId);

    boolean existByLectureIdAndUserId(Long lectureId, Long userId);

    LectureEnrollment enroll(LectureEnrollment lectureEnrollment);

}
