module it.unicam.cs.mpgc.rpg123393 {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires javafx.base;

    opens it.unicam.cs.mpgc.rpg123393 to javafx.fxml;
    opens it.unicam.cs.mpgc.rpg123393.controller to javafx.fxml;
    opens it.unicam.cs.mpgc.rpg123393.model to javafx.fxml;
    opens it.unicam.cs.mpgc.rpg123393.service to javafx.fxml;
    opens it.unicam.cs.mpgc.rpg123393.persistence to javafx.fxml;

    exports it.unicam.cs.mpgc.rpg123393;
    exports it.unicam.cs.mpgc.rpg123393.controller;
    exports it.unicam.cs.mpgc.rpg123393.model;
    exports it.unicam.cs.mpgc.rpg123393.service;
    exports it.unicam.cs.mpgc.rpg123393.persistence;
}
