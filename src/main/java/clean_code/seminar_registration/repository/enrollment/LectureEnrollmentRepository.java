package clean_code.seminar_registration.repository.enrollment;

import clean_code.seminar_registration.repository.enrollment.dto.UserLectureEnrollmentsDto;

import java.util.List;

public interface LectureEnrollmentRepository {
    List<UserLectureEnrollmentsDto> getUserEnrollments(Long userId);
}
