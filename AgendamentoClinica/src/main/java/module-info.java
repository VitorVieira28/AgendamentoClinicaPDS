module com.mycompany.agendamentoclinica {
    requires javafx.controls;
    requires javafx.fxml;

    opens com.mycompany.agendamentoclinica to javafx.fxml;
    exports com.mycompany.agendamentoclinica;
}
