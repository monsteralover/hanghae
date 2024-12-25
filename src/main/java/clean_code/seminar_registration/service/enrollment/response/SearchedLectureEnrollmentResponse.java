package clean_code.seminar_registration.service.enrollment.response;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
public class SearchedLectureEnrollmentResponse {
    private List<SearchedLectureEnrollment> userEnrollments;

    @Builder
    public SearchedLectureEnrollmentResponse(final List<SearchedLectureEnrollment> userEnrollments) {
        this.userEnrollments = userEnrollments;
    }
}
