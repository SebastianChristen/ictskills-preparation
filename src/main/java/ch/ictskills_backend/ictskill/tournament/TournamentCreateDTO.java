package ch.ictskills_backend.ictskill.tournament;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TournamentCreateDTO {
    private String title;
    private Integer gameId;
    private Integer size;
    private Integer tournamentState;
    private Integer winnerParticipantId; // Nur die ID des Gewinners senden
}