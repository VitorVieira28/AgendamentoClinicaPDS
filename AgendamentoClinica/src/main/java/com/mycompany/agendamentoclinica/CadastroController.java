package com.mycompany.agendamentoclinica;

import java.io.IOException;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

public class CadastroController {

    @FXML private VBox painelPrincipal;
    @FXML private TextField campoNome;
    @FXML private TextField campoCpf;
    @FXML private TextField campoEmail;
    @FXML private TextField campoTelefone;
    @FXML private PasswordField campoSenha;
    @FXML private Label labelMensagem;

    @FXML
    public void initialize() {
        Platform.runLater(() -> painelPrincipal.requestFocus());
    }

    @FXML
    private void clicouCadastrar() {
        String nome = campoNome.getText();
        String cpf = campoCpf.getText();
        String email = campoEmail.getText();
        String telefone = campoTelefone.getText();
        String senha = campoSenha.getText();

        if (!ValidadorSeguranca.validarEmail(email) || !ValidadorSeguranca.validarSenhaForte(senha)) {
            labelMensagem.setText("Erro: E-mail inválido ou senha muito fraca.");
            labelMensagem.setStyle("-fx-text-fill: red;");
            return;
        }

        PacienteDAO pacienteDao = new PacienteDAO();

        if (pacienteDao.emailExiste(email)) {
            labelMensagem.setText("Erro: Este e-mail já está cadastrado.");
            labelMensagem.setStyle("-fx-text-fill: red;");
            return;
        }

        String senhaCripto = ValidadorSeguranca.criptografarSenha(senha);
        Paciente novoPaciente = new Paciente(nome, cpf, email, telefone, senhaCripto);

        if (pacienteDao.salvar(novoPaciente)) {
            System.out.println("Cadastro feito com sucesso no banco!");
            
            // SALVA O PACIENTE RECÉM-CRIADO NA SESSÃO
            App.setPacienteLogado(novoPaciente);
            
            try {
                App.setRoot("agendamento");
            } catch (IOException e) {
                e.printStackTrace();
            }
            ServicoEmail.enviarConfirmacao(email, nome);
        }
        else {
            labelMensagem.setText("Erro: Falha ao salvar no banco.");
            labelMensagem.setStyle("-fx-text-fill: red;");
        }
    }

    @FXML
    private void voltarInicio() throws IOException {
        App.setRoot("primary");
    }
}