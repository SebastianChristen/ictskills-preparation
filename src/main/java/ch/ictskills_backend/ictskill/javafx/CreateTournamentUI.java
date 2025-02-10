package ch.ictskills_backend.ictskill.javafx;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.scene.paint.Color;
import javafx.scene.input.KeyCode;
import java.util.List;
import java.util.Arrays;
import java.net.HttpURLConnection;
import java.net.URL;
import java.io.OutputStream;

public class CreateTournamentUI extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Create Tournament");

        Label titleLabel = new Label("Title:");
        TextField titleField = new TextField();

        Label gameLabel = new Label("Game:");
        ComboBox<String> gameDropdown = new ComboBox<>();
        gameDropdown.getItems().addAll(fetchGamesFromDatabase()); // Load games dynamically

        Label sizeLabel = new Label("Size:");
        Spinner<Integer> sizeSpinner = new Spinner<>(2, 100, 16);

        Label errorLabel = new Label();
        errorLabel.setTextFill(Color.RED);

        Button createButton = new Button("Create");
        createButton.setOnAction(e -> handleCreateTournament(titleField, gameDropdown, sizeSpinner, errorLabel));

        // Handle Enter key press
        titleField.setOnKeyPressed(e -> { if (e.getCode() == KeyCode.ENTER) createButton.fire(); });
        gameDropdown.setOnKeyPressed(e -> { if (e.getCode() == KeyCode.ENTER) createButton.fire(); });
        sizeSpinner.setOnKeyPressed(e -> { if (e.getCode() == KeyCode.ENTER) createButton.fire(); });

        VBox layout = new VBox(10, titleLabel, titleField, gameLabel, gameDropdown, sizeLabel, sizeSpinner, errorLabel, createButton);
        layout.setPadding(new Insets(20));

        Scene scene = new Scene(layout, 350, 250);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private List<String> fetchGamesFromDatabase() {
        // Simulating games fetched from the database
        return Arrays.asList("Game A", "Game B", "Game C");
    }

    private void handleCreateTournament(TextField titleField, ComboBox<String> gameDropdown, Spinner<Integer> sizeSpinner, Label errorLabel) {
        String title = titleField.getText().trim();
        String game = gameDropdown.getValue();
        int size = sizeSpinner.getValue();

        if (title.isEmpty()) {
            errorLabel.setText("Title cannot be empty");
            return;
        }
        if (game == null) {
            errorLabel.setText("You must select a game");
            return;
        }
        if (size < 2 || size > 100) {
            errorLabel.setText("Size must be between 2 and 100");
            return;
        }

        errorLabel.setText(""); // Clear error
        sendTournamentToAPI(title, game, size);
    }

    private void sendTournamentToAPI(String title, String game, int size) {
        try {
            URL url = new URL("http://localhost:8080/tournaments"); // API Endpoint
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            // Convert data to JSON
            String jsonInputString = String.format(
                    "{\"title\":\"%s\", \"gameId\": 1, \"size\": %d, \"winnerParticipantId\": 1, \"tournamentState\": 1}",
                    title, size
            );

            // Send request
            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = jsonInputString.getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            int responseCode = conn.getResponseCode();
            if (responseCode == 201 || responseCode == 200) {
                System.out.println("Tournament Created Successfully!");
            } else {
                System.out.println("Failed to create tournament, Response Code: " + responseCode);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
