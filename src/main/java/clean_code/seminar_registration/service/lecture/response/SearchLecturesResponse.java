package clean_code.seminar_registration.service.lecture.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class SearchLecturesResponse {
    public List<SearchedLecture> lectures;

    @Builder
    public SearchLecturesResponse(final List<SearchedLecture> lectures) {
        this.lectures = lectures;
    }
}
