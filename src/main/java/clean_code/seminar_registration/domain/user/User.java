package clean_code.seminar_registration.domain.user;

import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Table
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Getter
    private Long id;
}
