package com.mycompany.agendamentoclinica;

import java.io.IOException;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class PerfilPacienteController {

    @FXML private TextField campoNome;
    @FXML private TextField campoCpf;
    @FXML private TextField campoEmail;
    @FXML private TextField campoTelefone;
    @FXML private PasswordField campoNovaSenha;
    @FXML private Label labelMensagem;

    @FXML
    public void initialize() {
        Platform.runLater(() -> {
            if (campoNome != null && campoNome.getParent() != null) {
                campoNome.getParent().requestFocus();
            }
        });

       
        Paciente logado = App.getPacienteLogado();
        if (logado != null) {
            campoNome.setText(logado.getNome());
            campoCpf.setText(logado.getCpf());
            campoEmail.setText(logado.getEmail());
            campoTelefone.setText(logado.getTelefone());
            
            
            campoCpf.setDisable(true);
            campoEmail.setDisable(true);
        }
    }

    @FXML
    private void clicouSalvar() {
        String nome = campoNome.getText();
        String telefone = campoTelefone.getText();
        String novaSenha = campoNovaSenha.getText();
        String cpf = campoCpf.getText(); 

        PacienteDAO dao = new PacienteDAO();
        String senhaCripto = null;

       
        if (novaSenha != null && !novaSenha.isEmpty()) {
            senhaCripto = ValidadorSeguranca.criptografarSenha(novaSenha);
        }

      
        if (dao.atualizarDados(cpf, nome, telefone, senhaCripto)) {
            labelMensagem.setText("Sucesso! Seus dados foram atualizados.");
            labelMensagem.setStyle("-fx-text-fill: green; -fx-font-weight: bold;");
            
         
            Paciente logado = App.getPacienteLogado();
            logado.setNome(nome);
            logado.setTelefone(telefone);
        } else {
            labelMensagem.setText("Erro ao atualizar no banco de dados.");
            labelMensagem.setStyle("-fx-text-fill: red;");
        }
    }

    @FXML
    private void voltar() throws IOException {
        App.setRoot("agendamento"); 
    }
}