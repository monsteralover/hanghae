package clean_code.seminar_registration.service.lecture;

import clean_code.seminar_registration.domain.lecture.Lecture;
import clean_code.seminar_registration.repository.lecture.LectureRepository;
import clean_code.seminar_registration.service.lecture.response.SearchLecturesResponse;
import clean_code.seminar_registration.service.lecture.response.SearchedLecture;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LectureService {

    private final LectureValidator lectureValidator;
    private final LectureRepository lectureRepository;

    public SearchLecturesResponse getLecturesByDate(final String date) {
        LocalDate lectureTime = lectureValidator.validateLectureDate(date);

        List<Lecture> lectures =
                lectureRepository.getLecturesByDate(lectureTime);
        return SearchLecturesResponse.builder()
                .lectures(lectures.stream().map(SearchedLecture::from).toList())
                .build();
    }
}
