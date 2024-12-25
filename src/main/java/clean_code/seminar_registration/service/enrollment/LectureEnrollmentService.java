package clean_code.seminar_registration.service.enrollment;

import clean_code.seminar_registration.repository.enrollment.LectureEnrollmentRepository;
import clean_code.seminar_registration.repository.enrollment.dto.UserLectureEnrollmentsDto;
import clean_code.seminar_registration.service.enrollment.response.SearchedLectureEnrollment;
import clean_code.seminar_registration.service.enrollment.response.SearchedLectureEnrollmentResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LectureEnrollmentService {

    private final LectureEnrollmentRepository lectureEnrollmentRepository;

    public SearchedLectureEnrollmentResponse getUserEnrollments(final Long userId) {
        final List<UserLectureEnrollmentsDto> userEnrollments = lectureEnrollmentRepository.getUserEnrollments(userId);
        final List<SearchedLectureEnrollment> searchedLectureEnrollments =
                userEnrollments.stream()
                        .map(enrollment -> SearchedLectureEnrollment.from(enrollment))
                        .toList();

        return SearchedLectureEnrollmentResponse.builder().userEnrollments(searchedLectureEnrollments).build();
    }
}
