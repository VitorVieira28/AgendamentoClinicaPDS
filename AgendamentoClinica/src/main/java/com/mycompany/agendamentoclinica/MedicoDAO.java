package com.mycompany.agendamentoclinica;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class MedicoDAO {

    private static final String URL = "jdbc:sqlite:clinica.db";

    public MedicoDAO() {
        criarTabelaSeNaoExistir();
    }

    private Connection conectar() throws SQLException {
        return DriverManager.getConnection(URL);
    }

    private void criarTabelaSeNaoExistir() {
        // Tabela de médicos
        String sqlMedicos = "CREATE TABLE IF NOT EXISTS medicos ("
                   + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                   + "nome TEXT NOT NULL,"
                   + "crm TEXT NOT NULL UNIQUE,"
                   + "email TEXT NOT NULL UNIQUE,"
                   + "telefone TEXT,"
                   + "senha_criptografada TEXT NOT NULL"
                   + ");";
        
        // --- SCRUM-49: Nova Tabela Relacional para Horários ---
        String sqlHorarios = "CREATE TABLE IF NOT EXISTS horarios_medico ("
                   + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                   + "data TEXT NOT NULL,"
                   + "horario TEXT NOT NULL,"
                   + "disponivel INTEGER NOT NULL," // 1 para Vago, 0 para Indisponível
                   + "UNIQUE(data, horario)"
                   + ");";
        
        try (Connection conn = this.conectar();
             Statement stmt = conn.createStatement()) {
            stmt.execute(sqlMedicos);
            stmt.execute(sqlHorarios);
        } catch (SQLException e) {
            System.out.println("Erro ao inicializar tabelas SQLite de medicos: " + e.getMessage());
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

    public boolean validarLogin(String email, String senhaCriptografada) {
        String sql = "SELECT id FROM medicos WHERE email = ? AND senha_criptografada = ?";

        try (Connection conn = this.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, email);
            pstmt.setString(2, senhaCriptografada);
            ResultSet rs = pstmt.executeQuery();
            
            return rs.next();
        } catch (SQLException e) {
            System.out.println("Erro ao validar login do médico no SQLite: " + e.getMessage());
            return false;
        }
    }

    public List<String> listarNomesMedicos() {
        List<String> medicos = new ArrayList<>();
        String sql = "SELECT nome FROM medicos";
        
        try (Connection conn = this.conectar();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                medicos.add(rs.getString("nome"));
            }
        } catch (SQLException e) {
            System.out.println("Erro ao listar médicos: " + e.getMessage());
        }
        return medicos;
    }

    // --- SCRUM-50: Buscar se o horário está disponível (1) ou indisponível (0). Se não existir, assume disponível por padrão ---
    public int obterStatusHorario(String data, String horario) {
        String sql = "SELECT disponivel FROM horarios_medico WHERE data = ? AND horario = ?";
        try (Connection conn = this.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, data);
            pstmt.setString(2, horario);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("disponivel");
            }
        } catch (SQLException e) {
            System.out.println("Erro ao obter status do horário: " + e.getMessage());
        }
        return 1; // Padrão: Vago/Disponível
    }

    // --- SCRUM-50: Atualizar ou inserir a configuração de status do horário ---
    public void alternarStatusHorario(String data, String horario, int novoStatus) {
        String sql = "INSERT INTO horarios_medico(data, horario, disponivel) VALUES(?,?,?)"
                   + "ON CONFLICT(data, horario) DO UPDATE SET disponivel = excluded.disponivel";
        try (Connection conn = this.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, data);
            pstmt.setString(2, horario);
            pstmt.setInt(3, novoStatus);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Erro ao salvar alteração de horário: " + e.getMessage());
        }
    }
}