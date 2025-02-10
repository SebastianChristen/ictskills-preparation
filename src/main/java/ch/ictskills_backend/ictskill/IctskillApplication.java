package ch.ictskills_backend.ictskill;

import ch.ictskills_backend.ictskill.javafx.TournamentListUI;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javafx.application.Application;

@SpringBootApplication
public class IctskillApplication {

	public static void main(String[] args) {
		// Start Spring Boot in a separate thread
		new Thread(() -> SpringApplication.run(IctskillApplication.class, args)).start();

		// Start JavaFX Application
		Application.launch(TournamentListUI.class, args);
	}
}
