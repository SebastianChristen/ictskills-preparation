package ch.ictskills_backend.ictskill.javafx;

import ch.ictskills_backend.ictskill.tournament.Tournament;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.List;

public class TournamentListUI extends Application {

    private TableView<Tournament> tableView = new TableView<>();
    private ObservableList<Tournament> tournamentList = FXCollections.observableArrayList();

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Tournament List");

        // Tabelle für Turniere
        TableColumn<Tournament, Integer> idColumn = new TableColumn<>("ID");
        idColumn.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleIntegerProperty(cellData.getValue().getId()).asObject());

        TableColumn<Tournament, String> titleColumn = new TableColumn<>("Title");
        titleColumn.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleStringProperty(cellData.getValue().getTitle()));

        TableColumn<Tournament, Integer> sizeColumn = new TableColumn<>("Size");
        sizeColumn.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleIntegerProperty(cellData.getValue().getSize()).asObject());

        TableColumn<Tournament, Integer> stateColumn = new TableColumn<>("State");
        stateColumn.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleIntegerProperty(cellData.getValue().getTournamentState()).asObject());

        tableView.getColumns().addAll(idColumn, titleColumn, sizeColumn, stateColumn);
        tableView.setItems(tournamentList);

        // Buttons
        Button refreshButton = new Button("Refresh");
        refreshButton.setOnAction(e -> fetchTournaments());

        Button addButton = new Button("Hinzufügen");
        addButton.setOnAction(e -> openCreateTournamentUI());

        HBox buttonBox = new HBox(10, refreshButton, addButton);

        VBox layout = new VBox(10, tableView, buttonBox);
        layout.setPadding(new Insets(20));

        Scene scene = new Scene(layout, 600, 400);
        primaryStage.setScene(scene);
        primaryStage.show();

        // Initial Turniere laden
        fetchTournaments();
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

                // JSON in Liste von Turnieren umwandeln
                Type listType = new TypeToken<List<Tournament>>() {}.getType();
                List<Tournament> tournaments = new Gson().fromJson(response.toString(), listType);

                // JavaFX TableView aktualisieren
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
}
