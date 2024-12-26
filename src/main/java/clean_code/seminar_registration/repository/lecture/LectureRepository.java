package clean_code.seminar_registration.repository.lecture;

import clean_code.seminar_registration.domain.lecture.Lecture;

import java.time.LocalDate;
import java.util.List;

public interface LectureRepository {
    List<Lecture> getLecturesByDate(LocalDate lectureTime);

    Lecture findById(Long lectureId);

    Lecture save(Lecture updateEnrollAnAvailable);

    Lecture findByIdWithPessimisticLock(Long lectureId);
}
