package com.mycompany.agendamentoclinica;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class MedicoDAO {

    private static final String URL = "jdbc:sqlite:clinica.db";

    public MedicoDAO() {
        criarTabelaSeNaoExistir();
    }

    private Connection conectar() throws SQLException {
        return DriverManager.getConnection(URL);
    }

    private void criarTabelaSeNaoExistir() {
        String sql = "CREATE TABLE IF NOT EXISTS medicos ("
                   + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                   + "nome TEXT NOT NULL,"
                   + "crm TEXT NOT NULL UNIQUE,"
                   + "email TEXT NOT NULL UNIQUE,"
                   + "telefone TEXT,"
                   + "senha_criptografada TEXT NOT NULL"
                   + ");";
        
        try (Connection conn = this.conectar();
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            System.out.println("Erro ao inicializar tabela SQLite de medicos: " + e.getMessage());
        }
    }

    public boolean salvar(Medico medico) {
        String sql = "INSERT INTO medicos(nome, crm, email, telefone, senha_criptografada) VALUES(?,?,?,?,?)";

        try (Connection conn = this.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, medico.getNome());
            pstmt.setString(2, medico.getCrm());
            pstmt.setString(3, medico.getEmail());
            pstmt.setString(4, medico.getTelefone());
            pstmt.setString(5, medico.getSenhaCriptografada());
            
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.out.println("Erro ao salvar medico no SQLite: " + e.getMessage());
            return false;
        }
    }

    public boolean emailExiste(String email) {
        String sql = "SELECT id FROM medicos WHERE email = ?";

        try (Connection conn = this.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, email);
            ResultSet rs = pstmt.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            return false;
        }
    }
}