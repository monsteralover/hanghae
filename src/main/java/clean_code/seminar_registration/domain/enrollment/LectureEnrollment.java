package clean_code.seminar_registration.domain.enrollment;

import clean_code.seminar_registration.domain.BaseEntity;
import clean_code.seminar_registration.exception.DuplicateUserRegistrationException;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Entity
@Table
public class LectureEnrollment extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Getter
    private Long id;

    @Getter
    private Long userId;

    @Getter
    private Long lectureId;

    @Builder
    public LectureEnrollment(final Long userId, final Long lectureId) {
        this.userId = userId;
        this.lectureId = lectureId;
    }

    public LectureEnrollment() {

    }

    public LectureEnrollment(final Long id, final Long userId, final Long lectureId) {
        this.id = id;
        this.userId = userId;
        this.lectureId = lectureId;
    }

    public static LectureEnrollment create(final Long userId, final Long lectureId) {
        return LectureEnrollment.builder()
                .userId(userId)
                .lectureId(lectureId)
                .build();
    }

    public static void validateUserEnrollDuplication(boolean existByLectureIdAndUserId) {
        if (existByLectureIdAndUserId) {
            throw new DuplicateUserRegistrationException("이미 등록된 사용자입니다.");
        }
    }

    public boolean isMaxUserSizeForLecture(Long usersInLecture) {
        final Long MAX_USER_SIZE = 30L;
        return MAX_USER_SIZE.equals(usersInLecture);
    }

}
