package clean_code.seminar_registration.service.lecture.response;

import clean_code.seminar_registration.domain.lecture.Lecture;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class SearchedLecture {
    private Long lectureId;
    private String lectureName;
    private LocalDateTime lectureDate;
    private boolean available;

    @Builder
    public SearchedLecture(final Long lectureId, final String lectureName, final LocalDateTime lectureDate,
                           final boolean available) {
        this.lectureId = lectureId;
        this.lectureName = lectureName;
        this.lectureDate = lectureDate;
        this.available = available;
    }

    public static SearchedLecture of(Lecture lecture) {
        return SearchedLecture.builder()
                .lectureId(lecture.getId())
                .lectureName(lecture.getLectureName())
                .lectureDate(lecture.getLectureDate())
                .available(lecture.isAvailable())
                .build();

    }
}
