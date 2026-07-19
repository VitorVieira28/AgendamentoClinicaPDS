package com.mycompany.agendamentoclinica;

import java.io.IOException;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class LoginMedicoController {

    @FXML private TextField campoEmail;
    @FXML private PasswordField campoSenha;
    @FXML private Label labelMensagem;

    @FXML
    private void clicouEntrar() throws IOException {
        String email = campoEmail.getText();
        String senha = campoSenha.getText();

        // Criptografa a senha digitada para poder comparar com a do banco
        String senhaCripto = ValidadorSeguranca.criptografarSenha(senha);

        MedicoDAO medicoDao = new MedicoDAO();

        // SCRUM-44: Verifica no banco e redireciona para a tela de agenda
        if (medicoDao.validarLogin(email, senhaCripto)) {
            App.setRoot("agenda_medico");
        } else {
            labelMensagem.setText("Erro: E-mail ou senha incorretos.");
            labelMensagem.setStyle("-fx-text-fill: red;");
        }
    }

    @FXML
    private void voltarAoInicio() throws IOException {
        App.setRoot("primary");
    }
}