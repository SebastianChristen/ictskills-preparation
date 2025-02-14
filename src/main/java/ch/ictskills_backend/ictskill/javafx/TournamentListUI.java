package ch.ictskills_backend.ictskill.javafx;

import ch.ictskills_backend.ictskill.game.Game;
import ch.ictskills_backend.ictskill.tournament.Tournament;
import javafx.application.Application;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class TournamentListUI extends Application {

    private TableView<Tournament> tableView = new TableView<>();
    private ObservableList<Tournament> tournamentList = FXCollections.observableArrayList();
    private Button viewButton;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Tournament List");

        // Header Image
        ImageView headerImage = new ImageView(new Image("file:header.png"));
        headerImage.setFitWidth(600);
        headerImage.setPreserveRatio(true);

        // Table Columns
        TableColumn<Tournament, String> titleColumn = new TableColumn<>("Title");
        titleColumn.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getTitle()));

        TableColumn<Tournament, String> gameColumn = new TableColumn<>("Game");
        gameColumn.setCellValueFactory(cellData -> {
            Game game = cellData.getValue().getGame();
            String gameName = (game != null) ? game.getName() : "Unknown";
            return new javafx.beans.property.SimpleStringProperty(gameName);
        });

        TableColumn<Tournament, String> winnerColumn = new TableColumn<>("Winner");
        winnerColumn.setCellValueFactory(cellData -> {
            String winnerName = (cellData.getValue().getWinnerParticipant() != null) ? cellData.getValue().getWinnerParticipant().getName() : "undecided";
            return new javafx.beans.property.SimpleStringProperty(winnerName);
        });

        tableView.getColumns().addAll(titleColumn, gameColumn, winnerColumn);
        tableView.setItems(tournamentList);

        // Buttons
        viewButton = new Button("View");
        viewButton.setDisable(true);
        viewButton.setOnAction(e -> openTournamentOverview());

        Button addButton = new Button("+ Tournament");
        addButton.setOnAction(e -> openCreateTournamentUI());

        tableView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            viewButton.setDisable(newSelection == null);
        });

        HBox buttonBox = new HBox(10, viewButton, addButton);
        buttonBox.setPadding(new Insets(10));

        VBox layout = new VBox(10, headerImage, tableView, buttonBox);
        layout.setPadding(new Insets(20));

        Scene scene = new Scene(layout, 600, 400);
        primaryStage.setScene(scene);
        primaryStage.show();

        fetchTournaments();
        startAutoRefresh();
    }

    private void fetchTournaments() {
        try {
            URL url = new URL("http://localhost:8080/tournaments");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json");

            if (conn.getResponseCode() == 200) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();

                Type listType = new TypeToken<List<Tournament>>() {}.getType();
                List<Tournament> tournaments = new Gson().fromJson(response.toString(), listType);
                tournamentList.setAll(tournaments);
            } else {
                System.out.println("Fehler beim Abrufen der Turniere: " + conn.getResponseCode());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void openCreateTournamentUI() {
        CreateTournamentUI createTournamentUI = new CreateTournamentUI();
        Stage createStage = new Stage();
        try {
            createTournamentUI.start(createStage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void startAutoRefresh() {
        Timer timer = new Timer(true);
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                fetchTournaments();
            }
        }, 0, 5000); // Alle 5 Sekunden aktualisieren
    }

    // TODO: FUNKTION EINBAUEN
    private void openTournamentOverview() {
        Tournament selectedTournament = tableView.getSelectionModel().getSelectedItem();
        if (selectedTournament != null) {
            //TournamentOverviewUI overviewUI = new TournamentOverviewUI(selectedTournament);
            Stage overviewStage = new Stage();
            try {
                //overviewUI.start(overviewStage);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
