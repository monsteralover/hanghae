package clean_code.seminar_registration.controller;

import clean_code.seminar_registration.ApiResponse;
import clean_code.seminar_registration.service.enrollment.LectureEnrollmentService;
import clean_code.seminar_registration.service.enrollment.response.SearchedLectureEnrollmentResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/enrollment")
public class LectureEnrollmentController {

    private final LectureEnrollmentService lectureEnrollmentService;

    @GetMapping("/{userId}")
    public ApiResponse<SearchedLectureEnrollmentResponse> getUserEnrollments(@PathVariable Long userId) {
        return ApiResponse.ok(lectureEnrollmentService.getUserEnrollments(userId));
    }

    @PostMapping("/{userId}")
    public ApiResponse<Long> enrollLecture(@PathVariable Long userId, @RequestParam Long lectureId) {
        Long id = lectureEnrollmentService.enrollLecture(userId, lectureId);
        return ApiResponse.ok(id);
    }

}
