package com.mycompany.agendamentoclinica;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class PacienteDAO {

    
    private static final String URL = "jdbc:sqlite:clinica.db";

   
    public PacienteDAO() {
        criarTabelaSeNaoExistir();
    }

    private Connection conectar() throws SQLException {
        return DriverManager.getConnection(URL);
    }

   
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

    
    public boolean emailExiste(String email) {
        String sql = "SELECT id FROM pacientes WHERE email = ?";

        try (Connection conn = this.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, email);
            ResultSet rs = pstmt.executeQuery();
            
            return rs.next();
        } catch (SQLException e) {
            System.out.println("Erro ao verificar email no SQLite: " + e.getMessage());
            return false;
        }
    }
    
    public boolean validarLogin(String email, String senhaCriptografada) {
        String sql = "SELECT id FROM pacientes WHERE email = ? AND senha_criptografada = ?";

        try (Connection conn = this.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, email);
            pstmt.setString(2, senhaCriptografada);
            
            ResultSet rs = pstmt.executeQuery();
            
            
            return rs.next(); 
            
        } catch (SQLException e) {
            System.out.println("Erro ao validar login no SQLite: " + e.getMessage());
            return false;
        }
    }
   
    public Paciente buscarPorEmail(String email) {
        String sql = "SELECT nome, cpf, email, telefone FROM pacientes WHERE email = ?";

        try (Connection conn = this.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, email);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return new Paciente(
                    rs.getString("nome"),
                    rs.getString("cpf"),
                    rs.getString("email"),
                    rs.getString("telefone"),
                    "" 
                );
            }
        } catch (SQLException e) {
            System.out.println("Erro ao buscar paciente por email: " + e.getMessage());
        }
        return null;
    }

    public boolean atualizarDados(String cpf, String nome, String telefone, String senhaCriptografada) {
        boolean atualizarSenha = (senhaCriptografada != null && !senhaCriptografada.isEmpty());
        String sql = atualizarSenha 
            ? "UPDATE pacientes SET nome = ?, telefone = ?, senha_criptografada = ? WHERE cpf = ?"
            : "UPDATE pacientes SET nome = ?, telefone = ? WHERE cpf = ?";

        try (java.sql.Connection conn = this.conectar();
             java.sql.PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, nome);
            pstmt.setString(2, telefone);
            
            if (atualizarSenha) {
                pstmt.setString(3, senhaCriptografada);
                pstmt.setString(4, cpf);
            } else {
                pstmt.setString(3, cpf);
            }
            
            pstmt.executeUpdate();
            return true;
        } catch (java.sql.SQLException e) {
            System.out.println("Erro ao atualizar paciente no SQLite: " + e.getMessage());
            return false;
        }
    }
}
