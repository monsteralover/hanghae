package clean_code.seminar_registration.domain.enrollment;

import clean_code.seminar_registration.domain.BaseEntity;
import jakarta.persistence.*;
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

}
