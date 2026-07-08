package com.mycompany.agendamentoclinica;

import java.io.IOException;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

public class LoginController {

    @FXML private VBox painelPrincipal;
    @FXML private TextField campoEmailLogin;
    @FXML private PasswordField campoSenhaLogin;
    @FXML private Label labelMensagemLogin;

    @FXML
    public void initialize() {
        Platform.runLater(() -> painelPrincipal.requestFocus());
    }

    @FXML
    private void clicarEntrar() {
        String email = campoEmailLogin.getText();
        String senha = campoSenhaLogin.getText();
        
        // 1. Verifica se os campos estão em branco
        if (email.isEmpty() || senha.isEmpty()) {
            labelMensagemLogin.setText("Erro: Preencha seu e-mail e senha.");
            labelMensagemLogin.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
            return;
        }

        String senhaCripto = ValidadorSeguranca.criptografarSenha(senha);
        PacienteDAO pacienteDao = new PacienteDAO();
        
        // 2. Tenta validar no banco de dados
        if (pacienteDao.validarLogin(email, senhaCripto)) {
            Paciente p = pacienteDao.buscarPorEmail(email);
            App.setPacienteLogado(p);
            
            try {
                App.setRoot("agendamento");
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            // 3. SE CAIR AQUI, A SENHA OU EMAIL ESTÃO ERRADOS!
            labelMensagemLogin.setText("Acesso Negado: E-mail ou senha incorretos.");
            labelMensagemLogin.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
            
            // Limpa o campo de senha para o usuário tentar de novo
            campoSenhaLogin.clear();
        }
    }

    @FXML
    private void voltarInicio() throws IOException {
        App.setRoot("primary");
    }
}