package clean_code.seminar_registration.controller;

import clean_code.seminar_registration.ApiResponse;
import clean_code.seminar_registration.service.lecture.LectureService;
import clean_code.seminar_registration.service.lecture.response.SearchLecturesResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/lecture")
public class LectureController {
    private final LectureService lectureService;

    @GetMapping
    public ApiResponse<SearchLecturesResponse> getLecturesForTime(@RequestParam String date) {
        return ApiResponse.ok(lectureService.getLecturesByDate(date));
    }
}
