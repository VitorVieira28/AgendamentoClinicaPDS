package com.mycompany.agendamentoclinica;

import java.io.IOException;
import javafx.fxml.FXML;

public class PrimaryController {

    @FXML
    private void irParaCadastroPaciente() throws IOException {
        App.setRoot("cadastro");
    }

    @FXML
    private void irParaLogin() throws IOException {
        App.setRoot("login");
    }

    @FXML
    private void irParaCadastroMedico() throws IOException {
        App.setRoot("cadastro_medico");
    }
}