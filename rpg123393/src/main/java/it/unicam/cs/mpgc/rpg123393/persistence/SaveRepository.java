package it.unicam.cs.mpgc.rpg123393.persistence;

import java.io.IOException;

public interface SaveRepository {
    void      save(GameState state) throws IOException;
    GameState load()                throws IOException;
    boolean   saveExists();
    /** Cancella il file di salvataggio. Nessun effetto se non esiste. */
    void      deleteSave();
}
