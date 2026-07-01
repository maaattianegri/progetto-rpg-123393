package it.unicam.cs.mpgc.rpg123393.persistence;

import java.io.IOException;

/**
 * Interfaccia per il salvataggio e caricamento della partita.
 *
 * Dipendere da questa interfaccia (e non da JsonSaveRepository)
 * permette di cambiare il meccanismo di persistenza in futuro
 * (es. database, cloud) senza modificare GameService.
 * Questo rispetta il Dependency Inversion Principle (SOLID).
 */
public interface SaveRepository {

    /**
     * Salva lo stato della partita.
     *
     * @param state lo stato da salvare
     * @throws IOException se si verifica un errore di I/O
     */
    void save(GameState state) throws IOException;

    /**
     * Carica lo stato della partita.
     *
     * @return il GameState salvato, o null se non esiste nessun salvataggio
     * @throws IOException se si verifica un errore di I/O
     */
    GameState load() throws IOException;

    /**
     * Controlla se esiste un file di salvataggio.
     *
     * @return true se esiste un salvataggio, false altrimenti
     */
    boolean saveExists();
}
