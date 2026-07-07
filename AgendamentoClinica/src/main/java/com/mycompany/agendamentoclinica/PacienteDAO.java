package com.mycompany.agendamentoclinica;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class PacienteDAO {

    // Configurações do seu banco de dados local
    private static final String URL = "jdbc:mysql://localhost:3306/clinica_db";
    private static final String USER = "root"; // Mude para o seu usuário do MySQL
    private static final String PASSWORD = "root"; // Mude para a sua senha do MySQL

    // Método para obter conexão com o banco
    private Connection obterConexao() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    // Salva o paciente no banco de dados real (SCRUM-9)
    public boolean salvar(Paciente paciente) {
        String sql = "INSERT INTO pacientes (nome, cpf, email, telefone, senha_criptografada) VALUES (?, ?, ?, ?, ?)";
        
        try (Connection conn = obterConexao(); 
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, paciente.getNome());
            stmt.setString(2, paciente.getCpf());
            stmt.setString(3, paciente.getEmail());
            stmt.setString(4, paciente.getTelefone());
            stmt.setString(5, paciente.getSenhaCriptografada());
            
            stmt.executeUpdate();
            return true;
            
        } catch (SQLException e) {
            System.out.println("Erro ao salvar no banco: " + e.getMessage());
            return false;
        }
    }

    // Verifica se o e-mail já existe direto no banco de dados
    public boolean emailExiste(String email) {
        String sql = "SELECT id FROM pacientes WHERE email = ?";
        
        try (Connection conn = obterConexao(); 
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, email);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next(); // Retorna true se achar algum registro
            }
            
        } catch (SQLException e) {
            System.out.println("Erro ao verificar e-mail: " + e.getMessage());
            return false;
        }
    }
}
