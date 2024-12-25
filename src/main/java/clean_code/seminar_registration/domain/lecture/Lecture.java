package clean_code.seminar_registration.domain.lecture;

import clean_code.seminar_registration.domain.BaseEntity;
import clean_code.seminar_registration.exception.MaxEnrollmentExceededException;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Entity
@Table
public class Lecture extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Getter
    private Long id;

    @Getter
    private String lectureName;

    private String speakerName;

    @Getter
    private LocalDateTime lectureDate;

    @Getter
    private boolean available;

    @Builder
    public Lecture(final Long id, final String lectureName, final String speakerName, final LocalDateTime lectureDate
            , final boolean available) {
        this.id = id;
        this.lectureName = lectureName;
        this.speakerName = speakerName;
        this.lectureDate = lectureDate;
        this.available = available;
    }

    public Lecture() {

    }

    public Lecture updateEnrollAnAvailable(long id) {
        return Lecture.builder()
                .id(id)
                .lectureName(this.lectureName)
                .speakerName(this.speakerName)
                .lectureDate(this.getLectureDate())
                .available(false)
                .build();
    }

    public void validateIfLectureIsAvailable(boolean isLectureAvailable) {
        if (!isLectureAvailable) {
            throw new MaxEnrollmentExceededException("강의 정원이 모두 찼습니다.");
        }

    }
}
