package ch.ictskills_backend.ictskill.tournament;


import ch.ictskills_backend.ictskill.participant.Participant;
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
    private Integer winnerParticipantId; // Direkte Speicherung der ID

    @ManyToOne
    @JoinColumn(name = "winnerParticipantId", referencedColumnName = "id", insertable = false, updatable = false)
    private Participant winnerParticipant;

    private Integer tournamentState;
}
