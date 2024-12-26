package clean_code.seminar_registration.repository.lecture;

import clean_code.seminar_registration.domain.lecture.Lecture;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface LectureJpaRepository extends JpaRepository<Lecture, Long> {
    @Query("SELECT l FROM Lecture l WHERE l.lectureDate BETWEEN :startOfDay AND :endOfDay")
    List<Lecture> findAllByLectureDate(
            @Param("startOfDay") LocalDateTime startOfDay,
            @Param("endOfDay") LocalDateTime endOfDay
    );

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT l FROM Lecture l WHERE l.id = :id")
    Lecture findByIdWithPessimisticLock(@Param("id") Long id);
}
