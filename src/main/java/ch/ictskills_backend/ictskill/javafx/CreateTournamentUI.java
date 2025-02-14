package ch.ictskills_backend.ictskill.javafx;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.scene.paint.Color;
import javafx.scene.input.KeyCode;
import java.util.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.io.*;
import org.json.JSONArray;
import org.json.JSONObject;

public class CreateTournamentUI extends Application {
    private final Map<String, Integer> gameMap = new HashMap<>(); // Speichert Game-Name -> ID

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
        loadGames(gameDropdown); // Lade Spiele aus API

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

    private void loadGames(ComboBox<String> gameDropdown) {
        new Thread(() -> {
            List<String> games = fetchGamesFromAPI();
            Platform.runLater(() -> gameDropdown.getItems().addAll(games)); // UI-Update auf JavaFX-Thread
        }).start();
    }

    private List<String> fetchGamesFromAPI() {
        List<String> gameNames = new ArrayList<>();
        try {
            URL url = new URL("http://localhost:8080/game"); // API für Spiele
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json");

            if (conn.getResponseCode() == 200) {
                InputStreamReader reader = new InputStreamReader(conn.getInputStream());
                BufferedReader br = new BufferedReader(reader);
                StringBuilder response = new StringBuilder();
                String line;

                while ((line = br.readLine()) != null) {
                    response.append(line);
                }

                JSONArray jsonArray = new JSONArray(response.toString());

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject game = jsonArray.getJSONObject(i);
                    int id = game.getInt("id");
                    String name = game.getString("name");
                    gameMap.put(name, id); // Speichere ID
                    gameNames.add(name);
                }
            } else {
                System.out.println("Failed to fetch games, Response Code: " + conn.getResponseCode());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return gameNames;
    }

    private void handleCreateTournament(TextField titleField, ComboBox<String> gameDropdown, Spinner<Integer> sizeSpinner, Label errorLabel) {
        String title = titleField.getText().trim();
        String gameName = gameDropdown.getValue();
        int size = sizeSpinner.getValue();

        if (title.isEmpty()) {
            errorLabel.setText("Title cannot be empty");
            return;
        }
        if (gameName == null) {
            errorLabel.setText("You must select a game");
            return;
        }
        if (size < 2 || size > 100) {
            errorLabel.setText("Size must be between 2 and 100");
            return;
        }

        errorLabel.setText(""); // Clear error
        int gameId = gameMap.get(gameName); // Hole gameId aus Map
        sendTournamentToAPI(title, gameId, size);
    }

    private void sendTournamentToAPI(String title, int gameId, int size) {
        try {
            URL url = new URL("http://localhost:8080/tournaments"); // API Endpoint
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            // Convert data to JSON
            String jsonInputString = String.format(
                    "{\"title\":\"%s\", \"gameId\": %d, \"size\": %d, \"winnerParticipantId\": 1, \"tournamentState\": 0}",
                    title, gameId, size
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
