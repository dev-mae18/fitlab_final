module pe.edu.upeu.fitlab_final {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.xerial.sqlitejdbc;
    requires java.sql;

    opens pe.edu.upeu.fitlab_final to javafx.fxml;
    opens pe.edu.upeu.fitlab_final.modelo to javafx.base, javafx.fxml;
    opens pe.edu.upeu.fitlab_final.controller to javafx.fxml;

    exports pe.edu.upeu.fitlab_final;
    exports pe.edu.upeu.fitlab_final.modelo;
    exports pe.edu.upeu.fitlab_final.utils;
    exports pe.edu.upeu.fitlab_final.controller;
    exports pe.edu.upeu.fitlab_final.implementacion;
    exports pe.edu.upeu.fitlab_final.interfaces;
    exports pe.edu.upeu.fitlab_final.test;
}
