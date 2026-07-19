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
        App.setRoot("login"); // Abre a tela de login do Paciente
    }

    @FXML
    private void irParaCadastroMedico() throws IOException {
        App.setRoot("cadastro_medico"); // Abre a tela de cadastro do Médico
    }

    @FXML
    private void irParaLoginMedico() throws IOException {
        App.setRoot("login_medico"); // CORREÇÃO: Abre a tela de login do Médico
    }
}