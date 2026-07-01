module it.unicam.cs.mpgc.rpg123393 {
    requires javafx.controls;
    requires javafx.fxml;

    opens it.unicam.cs.mpgc.rpg123393 to javafx.fxml;
    opens it.unicam.cs.mpgc.rpg123393.controller to javafx.fxml;
    opens it.unicam.cs.mpgc.rpg123393.model to javafx.fxml;

    exports it.unicam.cs.mpgc.rpg123393;
    exports it.unicam.cs.mpgc.rpg123393.controller;
    exports it.unicam.cs.mpgc.rpg123393.model;
}
