module com.game.physicsandbox {
    requires javafx.controls;
    requires javafx.graphics;
    requires javafx.fxml;
    requires static lombok;
    requires java.desktop;
    requires org.slf4j;
    requires spring.context;
    requires spring.beans;

    opens com.game.physicsandbox to javafx.fxml;
    exports com.game.physicsandbox;
}