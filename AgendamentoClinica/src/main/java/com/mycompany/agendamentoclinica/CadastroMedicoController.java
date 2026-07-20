package com.mycompany.agendamentoclinica;

import javafx.application.Platform; 
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class CadastroMedicoController {

    @FXML private TextField campoNome;
    @FXML private TextField campoCrm;
    @FXML private TextField campoEmail;
    @FXML private TextField campoTelefone;
    @FXML private PasswordField campoSenha;
    @FXML private Label labelMensagem;

    
    @FXML
    public void initialize() {
        Platform.runLater(() -> {
            if (campoNome != null && campoNome.getParent() != null) {
                campoNome.getParent().requestFocus();
            }
        });
    }

    @FXML
    private void clicouCadastrarMedico() {
        String nome = campoNome.getText();
        String crm = campoCrm.getText();
        String email = campoEmail.getText();
        String telefone = campoTelefone.getText();
        String senha = campoSenha.getText();

        if (!ValidadorSeguranca.validarEmail(email) || !ValidadorSeguranca.validarSenhaForte(senha)) {
            labelMensagem.setText("Erro: E-mail inválido ou senha muito fraca.");
            labelMensagem.setStyle("-fx-text-fill: red;");
            return;
        }

        MedicoDAO medicoDao = new MedicoDAO();

        if (medicoDao.emailExiste(email)) {
            labelMensagem.setText("Erro: Este e-mail já está cadastrado para outro médico.");
            labelMensagem.setStyle("-fx-text-fill: red;");
            return;
        }

        String senhaCripto = ValidadorSeguranca.criptografarSenha(senha);
        Medico novoMedico = new Medico(nome, crm, email, telefone, senhaCripto);

        if (medicoDao.salvar(novoMedico)) {
            labelMensagem.setText("Sucesso: Médico cadastrado no banco com êxito!");
            labelMensagem.setStyle("-fx-text-fill: green;");
            
        } else {
            labelMensagem.setText("Erro: Falha ao salvar no banco de dados.");
            labelMensagem.setStyle("-fx-text-fill: red;");
        }
    }
    
    @FXML
    private void voltarAoInicio() throws java.io.IOException {
        
        App.setRoot("primary"); 
    }
}