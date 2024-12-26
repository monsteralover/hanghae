package clean_code.seminar_registration.repository.enrollment.dto;

import lombok.Getter;

@Getter
public class UserLectureEnrollmentsDto {
    private Long lectureEnrollmentId;
    private String lectureName;
    private String speakerName;

    public UserLectureEnrollmentsDto(final Long lectureEnrollmentId, final String lectureName,
                                     final String speakerName) {
        this.lectureEnrollmentId = lectureEnrollmentId;
        this.lectureName = lectureName;
        this.speakerName = speakerName;
    }
}
