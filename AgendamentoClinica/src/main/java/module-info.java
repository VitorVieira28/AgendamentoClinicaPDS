module com.mycompany.agendamentoclinica {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql; 

    opens com.mycompany.agendamentoclinica to javafx.fxml;
    exports com.mycompany.agendamentoclinica;
}