package clean_code.seminar_registration.controller;

import clean_code.seminar_registration.ApiResponse;
import clean_code.seminar_registration.service.enrollment.LectureEnrollmentService;
import clean_code.seminar_registration.service.enrollment.response.SearchedLectureEnrollmentResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/enrollment")
public class LectureEnrollmentController {

    private final LectureEnrollmentService lectureEnrollmentService;

    @GetMapping("/{userId}")
    public ApiResponse<SearchedLectureEnrollmentResponse> getUserEnrollments(@PathVariable Long userId) {
        return ApiResponse.ok(lectureEnrollmentService.getUserEnrollments(userId));
    }

}
