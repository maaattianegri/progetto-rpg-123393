module it.unicam.cs.mpgc.rpg123393 {
    requires javafx.controls;
    requires javafx.fxml;

    // Apre i package a JavaFX per permettere l'iniezione nei controller FXML
    opens it.unicam.cs.mpgc.rpg123393 to javafx.fxml;
    opens it.unicam.cs.mpgc.rpg123393.controller to javafx.fxml;
    opens it.unicam.cs.mpgc.rpg123393.model to javafx.fxml;
    opens it.unicam.cs.mpgc.rpg123393.service to javafx.fxml;
    opens it.unicam.cs.mpgc.rpg123393.persistence to javafx.fxml;

    // Esporta tutti i package in modo che siano visibili tra i moduli
    exports it.unicam.cs.mpgc.rpg123393;
    exports it.unicam.cs.mpgc.rpg123393.controller;
    exports it.unicam.cs.mpgc.rpg123393.model;
    exports it.unicam.cs.mpgc.rpg123393.service;
    exports it.unicam.cs.mpgc.rpg123393.persistence;
}
