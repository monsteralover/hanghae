package clean_code.seminar_registration.service.lecture;

import clean_code.seminar_registration.exception.DateFormatMalformedException;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Component
public class LectureValidator {
    public LocalDate validateLectureTime(final String time) {
        if (time == null || !time.matches("^\\d{4}-\\d{2}-\\d{2}$")) {
            throw new DateFormatMalformedException("날짜 형식은 'yyyy-MM-dd' 입니다.");
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        return LocalDate.parse(time, formatter);
    }
}
