package ch.ictskills_backend.ictskill.tournament;


import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "tournament")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Tournament {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String title;
    private Integer gameId;
    private Integer size;
    private Integer winnerParticipantId;
    private Integer tournamentState;
}
