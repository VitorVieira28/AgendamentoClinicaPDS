package com.mycompany.agendamentoclinica;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
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

    private static List<Paciente> bancoDadosSimulado = new ArrayList<>();

    // 2. Método executado quando o usuário clica no botão "Cadastrar"
    @FXML
    private void clicouCadastrar() {
        // Pega o texto digitado na tela
        String nome = campoNome.getText();
        String cpf = campoCpf.getText();
        String email = campoEmail.getText();
        String telefone = campoTelefone.getText();
        String senha = campoSenha.getText();

        // Validações
        if (!ValidadorSeguranca.validarEmail(email) || !ValidadorSeguranca.validarSenhaForte(senha)) {
            labelMensagem.setText("Erro: E-mail inválido ou senha muito fraca.");
            labelMensagem.setStyle("-fx-text-fill: red;");
            return;
        }

        for (Paciente p : bancoDadosSimulado) {
            if (p.getEmail().equalsIgnoreCase(email)) {
                labelMensagem.setText("Erro: Este e-mail já está cadastrado.");
                labelMensagem.setStyle("-fx-text-fill: red;");
                return;
            }
        }

        // Criptografa e Salva
        String senhaCripto = ValidadorSeguranca.criptografarSenha(senha);
        Paciente novoPaciente = new Paciente(nome, cpf, email, telefone, senhaCripto);
        bancoDadosSimulado.add(novoPaciente);

        // Dá o feedback na tela para o usuário
        labelMensagem.setText("Sucesso: Paciente cadastrado com êxito!");
        labelMensagem.setStyle("-fx-text-fill: green;");
        System.out.println("Simulação Console: E-mail de confirmação enviado para " + email);
    }

    @FXML
    private void switchToSecondary() throws IOException {
        App.setRoot("secondary");
    }
}