package clean_code.seminar_registration.domain.lecture;

import clean_code.seminar_registration.domain.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;


import java.time.LocalDateTime;

@Entity
@Table
public class Lecture extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Getter
    private Long id;

    @Getter
    private String lectureName;

    private String speakerName;

    @Getter
    private LocalDateTime lectureDate;

    @Getter
    private boolean available;

}
