package com.mycompany.agendamentoclinica;

public class SistemaPrincipal {
    public static void main(String[] args) {
        CadastroController controller = new CadastroController();

        System.out.println("--- TESTANDO FLUXO DE CADASTRO (SCRUM-1) --- \n");

        // Teste 1: Sucesso
        System.out.println("Teste 1: Criando conta válida...");
        controller.cadastrarPaciente("Francisco Brandão", "123.456.789-00", "francisco@email.com", "34999999999", "senha123");

        System.out.println("\n-------------------------------------------");

        // Teste 2: Erro de e-mail duplicado
        System.out.println("Teste 2: Tentando cadastrar o mesmo e-mail...");
        controller.cadastrarPaciente("Outro Nome", "000.000.000-11", "francisco@email.com", "34888888888", "outrasenha");
    }
}