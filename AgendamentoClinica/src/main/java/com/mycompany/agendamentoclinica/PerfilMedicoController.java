package com.mycompany.agendamentoclinica;

import java.io.IOException;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class PerfilMedicoController {

    @FXML private TextField txtNome;
    @FXML private TextField txtCrm;
    @FXML private TextField txtEmail;
    @FXML private TextField txtTelefone;
    @FXML private PasswordField txtNovaSenha;
    @FXML private Label labelMensagem;

    private final MedicoDAO medicoDao = new MedicoDAO();

    @FXML
    public void initialize() {
        Medico medico = App.getMedicoLogado();
        
        if (medico != null) {
            
            txtNome.setText(medico.getNome());
            txtCrm.setText(medico.getCrm());
            txtEmail.setText(medico.getEmail());
            txtTelefone.setText(medico.getTelefone());
            
            
            txtCrm.setEditable(false);
            txtEmail.setEditable(false);
            txtCrm.setStyle("-fx-background-color: #e0e0e0;");
            txtEmail.setStyle("-fx-background-color: #e0e0e0;");
        }
    }

    @FXML
    private void salvarAlteracoes() {
        Medico medico = App.getMedicoLogado();
        if (medico == null) return;

        String novoNome = txtNome.getText();
        String novoTelefone = txtTelefone.getText();
        String novaSenha = txtNovaSenha.getText();

        if (novoNome.isEmpty()) {
            labelMensagem.setText("O campo Nome é obrigatório!");
            labelMensagem.setStyle("-fx-text-fill: red;");
            return;
        }

        medico.setNome(novoNome);
        medico.setTelefone(novoTelefone);

        boolean sucesso;
        
     
        if (novaSenha != null && !novaSenha.isEmpty()) {
            
            sucesso = medicoDao.atualizarPerfilComSenha(medico, novaSenha);
        } else {
            sucesso = medicoDao.atualizarPerfil(medico);
        }

        if (sucesso) {
            labelMensagem.setText("Perfil atualizado com sucesso!");
            labelMensagem.setStyle("-fx-text-fill: green;");
            txtNovaSenha.clear();
        } else {
            labelMensagem.setText("Erro ao atualizar o perfil. Tente novamente.");
            labelMensagem.setStyle("-fx-text-fill: red;");
        }
    }

    @FXML
    private void voltarParaAgenda() throws IOException {
        App.setRoot("agenda_medico");     }
}