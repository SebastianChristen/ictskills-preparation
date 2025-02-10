package ch.ictskills_backend.ictskill.tournament;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import java.util.List;

@RestController
@RequestMapping("/tournaments")
public class TournamentController {
    private final TournamentRepository repository;

    public TournamentController(TournamentRepository repository) {
        this.repository = repository;
    }

    // GET: Retrieve all tournaments
    @GetMapping
    public List<Tournament> getAllTournaments() {
        return repository.findAll();
    }

    // POST: Create a new tournament
    @PostMapping
    public Tournament createTournament(@RequestBody Tournament tournament) {
        return repository.save(tournament);
    }

    // PUT: Update an existing tournament
    @PutMapping("/{id}")
    public ResponseEntity<Tournament> updateTournament(@PathVariable Integer id, @RequestBody Tournament updatedTournament) {
        return repository.findById(id).map(tournament -> {
            tournament.setTitle(updatedTournament.getTitle());
            tournament.setGameId(updatedTournament.getGameId());
            tournament.setSize(updatedTournament.getSize());
            tournament.setWinnerParticipantId(updatedTournament.getWinnerParticipantId());
            tournament.setTournamentState(updatedTournament.getTournamentState());
            return ResponseEntity.ok(repository.save(tournament));
        }).orElse(ResponseEntity.notFound().build());
    }
}

