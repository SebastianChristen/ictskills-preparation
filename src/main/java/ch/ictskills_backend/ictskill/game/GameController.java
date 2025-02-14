package ch.ictskills_backend.ictskill.game;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/game")
public class GameController {
    private final GameRepository repository;

    public GameController(GameRepository repository) {
        this.repository = repository;
    }

    // GET: Retrieve all tournaments
    @GetMapping
    public List<Game> getAllGames() {
        return repository.findAll();
    }
}
