package clean_code.seminar_registration.repository.enrollment;

import clean_code.seminar_registration.repository.enrollment.dto.UserLectureEnrollmentsDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class LectureEnrollmentRepositoryImpl implements LectureEnrollmentRepository {
    private final LectureEnrollmentJpaRepository lectureEnrollmentJpaRepository;

    @Override
    public List<UserLectureEnrollmentsDto> getUserEnrollments(final Long userId) {
        return lectureEnrollmentJpaRepository.getEnrollmentsByUserId(userId);
    }
}
