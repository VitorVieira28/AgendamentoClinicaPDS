package com.mycompany.agendamentoclinica;

import java.io.IOException;
import javafx.application.Platform; 
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class LoginMedicoController {

    @FXML private TextField campoEmail;
    @FXML private PasswordField campoSenha;
    @FXML private Label labelMensagem;

    
    @FXML
    public void initialize() {
        Platform.runLater(() -> {
            if (campoEmail != null && campoEmail.getParent() != null) {
                campoEmail.getParent().requestFocus();
            }
        });
    }

    @FXML
    private void clicouEntrar() throws IOException {
        String email = campoEmail.getText();
        String senha = campoSenha.getText();

        
        String senhaCripto = ValidadorSeguranca.criptografarSenha(senha);

        MedicoDAO medicoDao = new MedicoDAO();

        
        if (medicoDao.validarLogin(email, senhaCripto)) {
            
            
            App.setMedicoLogado(medicoDao.buscarPorEmail(email));
            
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