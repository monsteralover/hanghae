package clean_code.seminar_registration.repository.enrollment;

import clean_code.seminar_registration.domain.enrollment.LectureEnrollment;
import clean_code.seminar_registration.repository.enrollment.dto.UserLectureEnrollmentsDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface LectureEnrollmentJpaRepository extends JpaRepository<LectureEnrollment, Long> {

    @Query("SELECT new clean_code.seminar_registration.repository.enrollment.dto.UserLectureEnrollmentsDto(" +
            "le.id, l.lectureName, l.speakerName) " +
            "FROM LectureEnrollment le " +
            "JOIN Lecture l ON le.lectureId = l.id " +
            "WHERE le.userId = :userId")
    List<UserLectureEnrollmentsDto> getEnrollmentsByUserId(Long userId);

    Optional<LectureEnrollment> findByLectureIdAndUserId(Long lectureId, Long userId);

    Long countAllByLectureId(Long lectureId);
}
