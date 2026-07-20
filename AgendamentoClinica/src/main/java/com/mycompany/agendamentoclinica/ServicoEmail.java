package com.mycompany.agendamentoclinica;

public class ServicoEmail {
    
    
    public static void enviarConfirmacao(String emailDestino, String nomePaciente) {
        System.out.println("\n================= SISTEMA DE E-MAIL =================");
        System.out.println("Enviando e-mail para : " + emailDestino);
        System.out.println("Assunto              : Confirmação de Cadastro - Clínica");
        System.out.println("Mensagem             : Olá, " + nomePaciente + "! Seu cadastro foi realizado com sucesso. Bem-vindo(a) à nossa clínica!");
        System.out.println("=====================================================\n");
    }
}