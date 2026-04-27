package aplikacija;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;

public class MainApp extends Application {

    private Database db = new Database();
    private ListView<String> tableList = new ListView<>();
    private TableView<Record> tableView = new TableView<>();
    private TextArea queryInput = new TextArea();
    private TableView<Record> queryResultView = new TableView<>();
    private Label statusLabel = new Label("Spreman za rad...");

    @Override
    public void start(Stage primaryStage) {

        // Levo: lista tabela
        VBox leftBox = new VBox(new Label("Tabele:"), tableList);
        leftBox.setSpacing(5);
        leftBox.setPadding(new Insets(10));

        // Centar: prikaz selektovane tabele
        VBox centerBox = new VBox(new Label("Podaci u tabeli:"), tableView);
        centerBox.setSpacing(5);
        centerBox.setPadding(new Insets(10));

        // Dugmad
        Button executeBtn = new Button("Izvrši");
        Button saveBtn = new Button("Sačuvaj bazu");
        Button loadBtn = new Button("Učitaj bazu");

        HBox fileButtons = new HBox(10, saveBtn, loadBtn);
        fileButtons.setPadding(new Insets(5));

        // Dole: upit i rezultati
        VBox bottomBox = new VBox(
                new Label("Upit:"),
                queryInput,
                executeBtn,
                new Label("Rezultat:"),
                queryResultView,
                statusLabel,
                fileButtons
        );
        bottomBox.setSpacing(5);
        bottomBox.setPadding(new Insets(10));
        
        //VBox.setVgrow(queryInput, Priority.ALWAYS);

        // Horizontalni split: lista tabela i tabela podataka
        SplitPane horizontalSplit = new SplitPane();
        horizontalSplit.getItems().addAll(leftBox, centerBox);
        horizontalSplit.setDividerPositions(0.25);

        // Vertikalni split: gore horizontalni split, dole query
        SplitPane verticalSplit = new SplitPane();
        verticalSplit.setOrientation(javafx.geometry.Orientation.VERTICAL);
        verticalSplit.getItems().addAll(horizontalSplit, bottomBox);
        verticalSplit.setDividerPositions(0.6);

        BorderPane root = new BorderPane();
        root.setCenter(verticalSplit);

        // Listener za selekciju tabele
        tableList.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                showTable(newVal);
            }
        });

        // Cozy stil
        root.setStyle("-fx-background-color: #3B5323; -fx-font-family: 'Segoe UI'; -fx-font-size: 14px; -fx-text-fill: #F5F5DC;");
        tableList.setStyle("-fx-background-color: #556B2F; -fx-control-inner-background: #556B2F; -fx-text-fill: #F5F5DC;");
        tableView.setStyle("-fx-background-color: #6B8E23; -fx-control-inner-background: #6B8E23; -fx-text-fill: #F5F5DC;");
        queryResultView.setStyle("-fx-background-color: #6B8E23; -fx-control-inner-background: #6B8E23; -fx-text-fill: #F5F5DC;");
        queryInput.setStyle("-fx-background-color: #8F9779; -fx-text-fill: #000000;");
        statusLabel.setStyle("-fx-text-fill: #F5F5DC; -fx-font-weight: bold;");

        Scene scene = new Scene(root, 900, 600);
        primaryStage.setTitle("Mini SQL Editor");
        primaryStage.setScene(scene);
        primaryStage.show();

        // Dugme Izvrši
        executeBtn.setOnAction(e -> executeQuery());

        // Dugme Sačuvaj bazu (SQLFormat)
        saveBtn.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Sačuvaj bazu");
            File file = fileChooser.showSaveDialog(primaryStage);
            if (file != null) {
                try {
                    SQLFormat sqlFormat = new SQLFormat(db);
                    sqlFormat.save(file);
                    statusLabel.setText("Baza sačuvana: " + file.getName());
                } catch (Exception ex) {
                    statusLabel.setText("Greška pri čuvanju: " + ex.getMessage());
                }
            }
        });

        // Dugme Učitaj bazu (SQLFormat)
        loadBtn.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Učitaj bazu");
            File file = fileChooser.showOpenDialog(primaryStage);
            if (file != null) {
                try {
                    SQLFormat sqlFormat = new SQLFormat(db);
                    sqlFormat.load(file);
                    statusLabel.setText("Baza učitana: " + file.getName());
                    refreshTableList();
                    tableView.getItems().clear();
                    tableView.getColumns().clear();
                    queryResultView.getItems().clear();
                    queryResultView.getColumns().clear();
                } catch (Exception ex) {
                    statusLabel.setText("Greška pri učitavanju: " + ex.getMessage());
                }
            }
        });
    }

    private void refreshTableList() {
        tableList.getItems().clear();
        for (Table t : db.getTables()) {
            tableList.getItems().add(t.getImeTabele());
        }
    }

    private void showTable(String imee) {
        Table t = db.getTable(imee);
        if (t == null) return;
        fillTableView(tableView, t);
    }

    private void executeQuery() {
        String sql = queryInput.getText().trim();
        if (sql.isEmpty()) return;

        // Očisti prethodni rezultat
        queryResultView.getItems().clear();
        queryResultView.getColumns().clear();

        // Podeli upit na više naredbi po ";"
        String[] queries = sql.split(";");
        Result lastResult = null;

        for (String q : queries) {
            q = q.trim();
            if (q.isEmpty()) continue;

            Statement stmt = new Statement(q);
            lastResult = stmt.execute(db);
        }

        // Prikaži samo poslednji rezultat
        if (lastResult != null) {
            if (lastResult instanceof Data) {
                Data data = (Data) lastResult;
                fillTableView(queryResultView, data);
                statusLabel.setText("Izvršeno: " + lastResult.getMessage());
            } else {
                // Poruku izdvajamo u lokalnu promenljivu (final/efektivno final)
                String message = lastResult.getMessage();

                TableColumn<Record, String> col = new TableColumn<>("Poruka");
                // Vrednost kolone čitamo iz samog reda (Record), bez hvatanja outer promenljivih
                col.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(
                        d.getValue().getValues().get(0)
                ));
                queryResultView.getColumns().add(col);

                // Dodaj jedan red sa porukom
                queryResultView.getItems().add(new Record(java.util.Collections.singletonList(message)));

                statusLabel.setText(message);
            }
        }

        refreshTableList();
    }

    private void fillTableView(TableView<Record> tv, Table t) {
        tv.getItems().clear();
        tv.getColumns().clear();
        for (int i = 0; i < t.getKolone().size(); i++) {
            final int colIndex = i;
            TableColumn<Record, String> col = new TableColumn<>(t.getKolone().get(i));
            col.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(
                    data.getValue().getValues().get(colIndex)
            ));
            tv.getColumns().add(col);
        }
        tv.getItems().addAll(t.getRecords());
    }

    private void fillTableView(TableView<Record> tv, Data data) {
        tv.getItems().clear();
        tv.getColumns().clear();
        for (int i = 0; i < data.getKolone().size(); i++) {
            final int colIndex = i;
            TableColumn<Record, String> col = new TableColumn<>(data.getKolone().get(i));
            col.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(
                    d.getValue().getValues().get(colIndex)
            ));
            tv.getColumns().add(col);
        }
        tv.getItems().addAll(data.getRedovi());
    }

    public static void main(String[] args) {
        launch(args);
    }
}
