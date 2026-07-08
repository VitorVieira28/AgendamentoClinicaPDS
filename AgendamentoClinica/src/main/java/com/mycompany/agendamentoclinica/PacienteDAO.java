package com.mycompany.agendamentoclinica;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class PacienteDAO {

    // URL de conexão apontando para um arquivo local na pasta do projeto
    private static final String URL = "jdbc:sqlite:clinica.db";

    // Construtor: Toda vez que o DAO for usado, ele garante que a tabela existe
    public PacienteDAO() {
        criarTabelaSeNaoExistir();
    }

    private Connection conectar() throws SQLException {
        return DriverManager.getConnection(URL);
    }

    // Cria a tabela automaticamente na primeira execução do programa
    private void criarTabelaSeNaoExistir() {
        String sql = "CREATE TABLE IF NOT EXISTS pacientes ("
                   + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                   + "nome TEXT NOT NULL,"
                   + "cpf TEXT NOT NULL,"
                   + "email TEXT NOT NULL UNIQUE,"
                   + "telefone TEXT,"
                   + "senha_criptografada TEXT NOT NULL"
                   + ");";
        
        try (Connection conn = this.conectar();
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            System.out.println("Erro ao inicializar tabela SQLite: " + e.getMessage());
        }
    }

    // Método para salvar o paciente no arquivo SQLite
    public boolean salvar(Paciente paciente) {
        String sql = "INSERT INTO pacientes(nome, cpf, email, telefone, senha_criptografada) VALUES(?,?,?,?,?)";

        try (Connection conn = this.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, paciente.getNome());
            pstmt.setString(2, paciente.getCpf());
            pstmt.setString(3, paciente.getEmail());
            pstmt.setString(4, paciente.getTelefone());
            pstmt.setString(5, paciente.getSenhaCriptografada());
            
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.out.println("Erro ao salvar no SQLite: " + e.getMessage());
            return false;
        }
    }

    // Método para verificar e-mail duplicado
    public boolean emailExiste(String email) {
        String sql = "SELECT id FROM pacientes WHERE email = ?";

        try (Connection conn = this.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, email);
            ResultSet rs = pstmt.executeQuery();
            
            return rs.next(); // Retorna true se achar algum registro
        } catch (SQLException e) {
            System.out.println("Erro ao verificar email no SQLite: " + e.getMessage());
            return false;
        }
    }
}