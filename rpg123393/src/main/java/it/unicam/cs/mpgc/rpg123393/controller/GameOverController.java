package it.unicam.cs.mpgc.rpg123393.controller;

import it.unicam.cs.mpgc.rpg123393.service.GameService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Controller della schermata di Game Over.
 * Riceve i dati dal HelloController tramite initData() dopo una sconfitta.
 */
public class GameOverController {

    @FXML private Label defeatLabel;
    @FXML private Label levelReachedLabel;
    @FXML private Label enemyLabel;

    private String playerName;
    private int    forza;
    private int    vitalita;
    private String imagePath;

    /**
     * Inizializza la schermata con i dati della sconfitta.
     *
     * @param gameService  servizio di gioco (per leggere livello e nome nemico)
     * @param playerName   nome del personaggio
     * @param forza        statistica forza
     * @param vitalita     statistica vitalità
     * @param imagePath    path immagine personaggio
     */
    public void initData(GameService gameService, String playerName,
                         int forza, int vitalita, String imagePath) {
        this.playerName = playerName;
        this.forza      = forza;
        this.vitalita   = vitalita;
        this.imagePath  = imagePath;

        defeatLabel.setText("Sei stato sconfitto da "
                + gameService.getEnemy().getName() + "...");
        levelReachedLabel.setText("Livello raggiunto: " + gameService.getPlayerLevel());
        enemyLabel.setText("Nemico: " + gameService.getEnemy().getName()
                + " · HP residui: " + gameService.getEnemy().getCurrentHp());
    }

    /** Ricomincia da capo (stesso personaggio, livello 1). */
    @FXML
    private void onRestart() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/it/unicam/cs/mpgc/rpg123393/view/hello-view.fxml"));
            Stage stage = (Stage) defeatLabel.getScene().getWindow();
            stage.setScene(new Scene(loader.load(), 800, 700));

            HelloController ctrl = loader.getController();
            // Nuovo GameService = partita azzerata dal livello 1
            ctrl.initData(playerName, forza, vitalita, imagePath, new GameService());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /** Torna al menu principale (schermata di creazione). */
    @FXML
    private void onMainMenu() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/it/unicam/cs/mpgc/rpg123393/view/creation-view.fxml"));
            Stage stage = (Stage) defeatLabel.getScene().getWindow();
            stage.setScene(new Scene(loader.load(), 800, 700));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
