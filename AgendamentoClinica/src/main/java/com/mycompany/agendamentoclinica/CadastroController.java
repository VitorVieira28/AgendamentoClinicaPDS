package com.mycompany.agendamentoclinica;

import java.util.ArrayList;
import java.util.List;

public class CadastroController {
    // Lista na memória simulando o banco de dados por enquanto
    private List<Paciente> bancoDadosSimulado = new ArrayList<>();

    public boolean cadastrarPaciente(String nome, String cpf, String email, String telefone, String senha) {
        
        // 1. Validações de Campos
        if (!ValidadorSeguranca.validarEmail(email) || !ValidadorSeguranca.validarSenhaForte(senha)) {
            System.out.println("Erro: Dados inválidos (E-mail incorreto ou senha fraca).");
            return false;
        }

        // 2. Verificação de E-mail duplicado (SCRUM-10)
        for (Paciente p : bancoDadosSimulado) {
            if (p.getEmail().equalsIgnoreCase(email)) {
                System.out.println("Erro: Este e-mail já está cadastrado.");
                return false;
            }
        }

        // 3. Criptografar Senha (SCRUM-11)
        String senhaCripto = ValidadorSeguranca.criptografarSenha(senha);

        // 4. Salvar no "Banco" (SCRUM-9)
        Paciente novoPaciente = new Paciente(nome, cpf, email, telefone, senhaCripto);
        bancoDadosSimulado.add(novoPaciente);
        System.out.println("Sucesso: Paciente " + nome + " cadastrado com êxito!");

        // 5. Simular envio de e-mail (SCRUM-12)
        enviarEmailConfirmacao(email);
        
        return true;
    }

    private void enviarEmailConfirmacao(String email) {
        System.out.println("Simulação: E-mail de confirmação enviado para " + email);
    }
}