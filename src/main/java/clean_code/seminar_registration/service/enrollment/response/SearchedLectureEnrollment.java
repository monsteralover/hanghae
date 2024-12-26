package clean_code.seminar_registration.service.enrollment.response;

import clean_code.seminar_registration.repository.enrollment.dto.UserLectureEnrollmentsDto;
import lombok.Builder;
import lombok.Getter;

@Getter
public class SearchedLectureEnrollment {
    private Long lectureEnrollmentId;
    private String lectureName;
    private String speakerName;

    @Builder
    public SearchedLectureEnrollment(final Long lectureEnrollmentId, final String lectureName,
                                     final String speakerName) {
        this.lectureEnrollmentId = lectureEnrollmentId;
        this.lectureName = lectureName;
        this.speakerName = speakerName;
    }

    public static SearchedLectureEnrollment from(final UserLectureEnrollmentsDto userEnrollment) {
        return SearchedLectureEnrollment.builder()
                .lectureEnrollmentId(userEnrollment.getLectureEnrollmentId())
                .lectureName(userEnrollment.getLectureName())
                .speakerName(userEnrollment.getSpeakerName())
                .build();
    }
}
