package clean_code.seminar_registration.repository.lecture;

import clean_code.seminar_registration.domain.lecture.Lecture;
import clean_code.seminar_registration.exception.LectureDoesNotExistException;
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

    @Override
    public Lecture findById(final Long lectureId) {
        return lectureJpaRepository.findById(lectureId)
                .orElseThrow(() -> new LectureDoesNotExistException("존재하지 않는 강의입니다."));
    }

    @Override
    public Lecture save(final Lecture lecture) {
        lectureJpaRepository.save(lecture);
        return lecture;
    }

    @Override
    public Lecture findByIdWithPessimisticLock(final Long lectureId) {
        return lectureJpaRepository.findByIdWithPessimisticLock(lectureId);
    }


}
