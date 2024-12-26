package clean_code.seminar_registration.repository.enrollment;

import clean_code.seminar_registration.domain.enrollment.LectureEnrollment;
import clean_code.seminar_registration.repository.enrollment.dto.UserLectureEnrollmentsDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class LectureEnrollmentRepositoryImpl implements LectureEnrollmentRepository {
    private final LectureEnrollmentJpaRepository lectureEnrollmentJpaRepository;


    @Override
    public List<UserLectureEnrollmentsDto> getUserEnrollments(final Long userId) {
        return lectureEnrollmentJpaRepository.getEnrollmentsByUserId(userId);
    }

    @Override
    public boolean existByLectureIdAndUserId(final Long lectureId, final Long userId) {
        final Optional<LectureEnrollment> byLectureIdAndUserId =
                lectureEnrollmentJpaRepository.findByLectureIdAndUserId(lectureId, userId);

        return byLectureIdAndUserId.isPresent();
    }

    @Override
    public LectureEnrollment enroll(final LectureEnrollment lectureEnrollment) {
        return lectureEnrollmentJpaRepository.save(lectureEnrollment);
    }

    @Override
    public Long countAllByLectureId(final Long lectureId) {
        return lectureEnrollmentJpaRepository.countAllByLectureId(lectureId);
    }
}
