package clean_code.seminar_registration.service.lecture;

import clean_code.seminar_registration.exception.DateFormatMalformedException;
import clean_code.seminar_registration.exception.InvalidLectureDateException;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Component
public class LectureValidator {
    public LocalDate validateLectureDate(final String date) {
        if (date == null || !date.matches("^\\d{4}-\\d{2}-\\d{2}$")) {
            throw new DateFormatMalformedException("날짜 형식은 'yyyy-MM-dd' 입니다.");
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        final LocalDate localDate = LocalDate.parse(date, formatter);

        if (localDate.isBefore(LocalDate.now())) {
            throw new InvalidLectureDateException("과거의 일자는 수강이 불가합니다.");
        }

        return localDate;
    }
}
