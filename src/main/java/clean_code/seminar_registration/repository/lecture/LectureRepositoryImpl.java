package clean_code.seminar_registration.repository.lecture;

import clean_code.seminar_registration.domain.lecture.Lecture;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class LectureRepositoryImpl implements LectureRepository {
    private final LectureJpaRepository lectureJpaRepository;

    @Override
    public List<Lecture> getLecturesByDate(final LocalDate lectureDate) {
        return lectureJpaRepository.findAllByLectureDate(lectureDate.atStartOfDay(),
                LocalDateTime.from(lectureDate.atTime(23, 59, 59)));
    }
}
