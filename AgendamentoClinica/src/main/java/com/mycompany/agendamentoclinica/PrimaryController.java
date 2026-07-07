package com.mycompany.agendamentoclinica;

import java.io.IOException;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class PrimaryController {

    // 1. Mapeamento dos campos da tela visual
    @FXML private TextField campoNome;
    @FXML private TextField campoCpf;
    @FXML private TextField campoEmail;
    @FXML private TextField campoTelefone;
    @FXML private PasswordField campoSenha;
    @FXML private Label labelMensagem;

    // 2. Método executado quando o usuário clica no botão "Cadastrar"
    @FXML
    private void clicouCadastrar() {
        // Pega o texto digitado na tela
        String nome = campoNome.getText();
        String cpf = campoCpf.getText();
        String email = campoEmail.getText();
        String telefone = campoTelefone.getText();
        String senha = campoSenha.getText();

        // Validações de formato básicas (E-mail e Senha)
        if (!ValidadorSeguranca.validarEmail(email) || !ValidadorSeguranca.validarSenhaForte(senha)) {
            labelMensagem.setText("Erro: E-mail inválido ou senha muito fraca.");
            labelMensagem.setStyle("-fx-text-fill: red;");
            return;
        }

        // Instancia o DAO para interagir com o Banco de Dados real
        PacienteDAO pacienteDao = new PacienteDAO();

        // Verificação de e-mail duplicado direto no Banco de Dados (SCRUM-10)
        if (pacienteDao.emailExiste(email)) {
            labelMensagem.setText("Erro: Este e-mail já está cadastrado.");
            labelMensagem.setStyle("-fx-text-fill: red;");
            return;
        }

        // Criptografa a senha com SHA-256 (SCRUM-11)
        String senhaCripto = ValidadorSeguranca.criptografarSenha(senha);
        Paciente novoPaciente = new Paciente(nome, cpf, email, telefone, senhaCripto);

        // Salva de verdade no Banco de Dados (SCRUM-9)
        if (pacienteDao.salvar(novoPaciente)) {
            labelMensagem.setText("Sucesso: Paciente cadastrado no banco com êxito!");
            labelMensagem.setStyle("-fx-text-fill: green;");
            
            // Dispara o e-mail simulado no console (SCRUM-12)
            ServicoEmail.enviarConfirmacao(email, nome);
        } else {
            labelMensagem.setText("Erro: Falha crítica ao salvar no banco de dados.");
            labelMensagem.setStyle("-fx-text-fill: red;");
        }
    }

    @FXML
    private void switchToSecondary() throws IOException {
        App.setRoot("secondary");
    }
}