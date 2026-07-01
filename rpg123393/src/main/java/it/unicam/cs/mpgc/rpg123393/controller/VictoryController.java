package it.unicam.cs.mpgc.rpg123393.controller;

import it.unicam.cs.mpgc.rpg123393.persistence.JsonSaveRepository;
import it.unicam.cs.mpgc.rpg123393.service.GameService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;

/**
 * Controller della schermata di vittoria.
 * Riceve i dati dal HelloController tramite initData() dopo una battaglia vinta.
 * Salva automaticamente lo stato della partita dopo ogni vittoria.
 */
public class VictoryController {

    @FXML private Label summaryLabel;
    @FXML private Label xpLabel;
    @FXML private Label levelLabel;
    @FXML private Label statsLabel;

    private GameService gameService;
    private String      playerName;
    private int         vigore;
    private int         arcano;
    private String      imagePath;

    /**
     * Inizializza la schermata con i dati della vittoria e salva la partita.
     *
     * @param gameService  servizio di gioco già inizializzato (con XP e livello aggiornati)
     * @param xpGained     XP guadagnata in questo scontro
     * @param levelUpMsgs  messaggi di level up (vuota se nessun level up)
     * @param playerName   nome del personaggio
     * @param vigore       statistica vigore
     * @param arcano       statistica arcano
     * @param imagePath    path immagine personaggio
     */
    public void initData(GameService gameService, int xpGained, List<String> levelUpMsgs,
                         String playerName, int vigore, int arcano, String imagePath) {
        this.gameService = gameService;
        this.playerName  = playerName;
        this.vigore      = vigore;
        this.arcano      = arcano;
        this.imagePath   = imagePath;

        // Aggiorna i metadati di classe nel GameService se non già presenti
        if (gameService.getImagePath() == null || gameService.getImagePath().isEmpty()) {
            gameService.setImagePath(imagePath);
        }

        summaryLabel.setText("Hai sconfitto " + gameService.getEnemy().getName() + "!");
        xpLabel.setText("+ " + xpGained + " XP");

        if (!levelUpMsgs.isEmpty()) {
            levelLabel.setText(String.join(" · ", levelUpMsgs));
        } else {
            levelLabel.setText("Livello: " + gameService.getPlayerLevel()
                    + "  (" + gameService.getPlayerXp() + "/" + gameService.getXpRequired() + " XP)");
        }

        var p = gameService.getPlayer();
        statsLabel.setText("HP: " + p.getCurrentHp() + "/" + p.getMaxHp()
                + "  |  MANA: " + p.getCurrentMana() + "/" + p.getMaxMana());

        // Salvataggio automatico dopo ogni vittoria
        saveGame();
    }

    /** Salva lo stato corrente tramite JsonSaveRepository. */
    private void saveGame() {
        try {
            new JsonSaveRepository().save(gameService.toGameState());
        } catch (IOException e) {
            // Il salvataggio è best-effort: un errore non blocca il gioco
            System.err.println("[WARN] Salvataggio fallito: " + e.getMessage());
        }
    }

    /** Passa al prossimo scontro tornando alla schermata di battaglia. */
    @FXML
    private void onNextBattle() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/it/unicam/cs/mpgc/rpg123393/view/hello-view.fxml"));
            Stage stage = (Stage) summaryLabel.getScene().getWindow();
            stage.setScene(new Scene(loader.load(), 800, 700));

            HelloController ctrl = loader.getController();
            ctrl.initData(playerName, vigore, arcano, imagePath, gameService);
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
            Stage stage = (Stage) summaryLabel.getScene().getWindow();
            stage.setScene(new Scene(loader.load(), 800, 700));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
