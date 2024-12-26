package clean_code.seminar_registration;

import clean_code.seminar_registration.domain.lecture.Lecture;
import clean_code.seminar_registration.exception.DateFormatMalformedException;
import clean_code.seminar_registration.exception.InvalidLectureDateException;
import clean_code.seminar_registration.repository.lecture.LectureRepository;
import clean_code.seminar_registration.service.lecture.LectureService;
import clean_code.seminar_registration.service.lecture.LectureValidator;
import clean_code.seminar_registration.service.lecture.response.SearchLecturesResponse;
import clean_code.seminar_registration.service.lecture.response.SearchedLecture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.catchThrowable;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class LectureTest {
    private LectureValidator lectureValidator;

    private LectureRepository lectureRepository;

    @BeforeEach
    void setUp() {
        lectureValidator = new LectureValidator();
    }

    @DisplayName("일자로 강의 조회 시 일자가 yyyy-mm-dd 형식이 아닌 경우 DateFormatMalformedException 발생시킨다.")
    @Test
    void lectureDateFormatTest() {
        // given
        String time = "2024:12:25";

        // when
        Throwable throwable = catchThrowable(() -> lectureValidator.validateLectureDate(time));

        // then
        assertThat(throwable).isInstanceOf(DateFormatMalformedException.class);
    }

    @DisplayName("일자로 null이 들어온 경우 DateFormatMalformedException 발생시킨다.")
    @Test
    void lectureDateNullTest() {
        // given
        String time = null;

        // when
        Throwable throwable = catchThrowable(() -> lectureValidator.validateLectureDate(time));

        // then
        assertThat(throwable).isInstanceOf(DateFormatMalformedException.class);
    }

    @DisplayName("과거의 날짜를 요청하는 경우 InvalidLectureDateException을 발생시킨다.")
    @Test
    void validateLectureDateNotInPast() {
        // given
        String time = LocalDate.now().minusDays(1).toString();

        // when
        Throwable throwable = catchThrowable(() -> lectureValidator.validateLectureDate(time));

        // then
        assertThat(throwable).isInstanceOf(InvalidLectureDateException.class);
    }

    @DisplayName("강의 조회 시 강의를 수강가능한지를 판단할 수 있는 값을 함께 반환한다.")
    @Test
    void provideAvailableWithResponse() {
        //given
        LocalDate date = LocalDate.now();
        String dateString = date.toString();

        lectureValidator = mock(LectureValidator.class);
        lectureRepository = mock(LectureRepository.class);
        final LectureService lectureService = new LectureService(lectureValidator, lectureRepository);

        when(lectureValidator.validateLectureDate(dateString)).thenReturn(date);
        final Lecture lecture = Lecture.builder()
                .id(1L)
                .lectureName("How to master Java")
                .speakerName("MartinFowler")
                .lectureDate(LocalDateTime.now())
                .available(true)
                .build();

        when(lectureRepository.getLecturesByDate(date))
                .thenReturn(List.of(lecture));

        // When
        SearchLecturesResponse response = lectureService.getLecturesByDate(dateString);

        // Then
        assertThat(response).isNotNull();
        SearchedLecture searchedLecture = response.getLectures().get(0);
        assertThat(searchedLecture.isAvailable()).isTrue();
        verify(lectureRepository).getLecturesByDate(date);
    }

    @DisplayName("일자에 강의가 없는 경우 응답객체 안에 빈 리스트틀 반환한다.")
    @Test
    void returnsEmptyListWhenLectureNotRegistered() {
        //given
        LocalDate date = LocalDate.now();
        String dateString = date.toString();

        lectureValidator = mock(LectureValidator.class);
        lectureRepository = mock(LectureRepository.class);
        final LectureService lectureService = new LectureService(lectureValidator, lectureRepository);

        when(lectureValidator.validateLectureDate(dateString)).thenReturn(date);
        when(lectureRepository.getLecturesByDate(date))
                .thenReturn(Collections.emptyList());

        // When
        SearchLecturesResponse response = lectureService.getLecturesByDate(dateString);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getLectures()).isEqualTo(Collections.emptyList());
        verify(lectureRepository).getLecturesByDate(date);
    }

}
