package ch.ictskills_backend.ictskill.participant;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "participant")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Participant {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private boolean isTemporary;
    private String name;
    private boolean isTeam;
}
