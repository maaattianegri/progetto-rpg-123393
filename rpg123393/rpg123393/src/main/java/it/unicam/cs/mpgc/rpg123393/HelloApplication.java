package it.unicam.cs.mpgc.rpg123393;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("view/creation-view.fxml"));
        // La risoluzione base qui diventa ininfluente perché forziamo il tutto schermo
        Scene scene = new Scene(fxmlLoader.load(), 800, 600);
        stage.setTitle("Arena Master - La Forgia");
        stage.setScene(scene);

        // --- LA MAGIA DEL TUTTO SCHERMO ---
        stage.setFullScreen(true);

        // (Se preferisci solo ingrandire la finestra mantenendo la X di chiusura,
        // cancella la riga sopra e decommenta la riga qui sotto)
        // stage.setMaximized(true);

        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}