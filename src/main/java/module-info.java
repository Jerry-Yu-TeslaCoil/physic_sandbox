module com.game.physicsandbox {
    requires javafx.controls;
    requires javafx.graphics;
    requires javafx.fxml;
    requires static lombok;
    requires java.desktop;
    requires org.slf4j;
    requires spring.context;
    requires spring.beans;

    opens com.game.physicsandbox;
    exports com.game.physicsandbox;
    opens com.game.physicsandbox.physics.frame;
    exports com.game.physicsandbox.physics.frame;
    opens com.game.physicsandbox.physics.event;
    exports com.game.physicsandbox.physics.event;
    opens com.game.physicsandbox.physics.mechanism;
    exports com.game.physicsandbox.physics.mechanism;
    opens com.game.physicsandbox.physics.control;
    exports com.game.physicsandbox.physics.control;
    opens com.game.physicsandbox.physics.util;
    exports com.game.physicsandbox.physics.util;
}