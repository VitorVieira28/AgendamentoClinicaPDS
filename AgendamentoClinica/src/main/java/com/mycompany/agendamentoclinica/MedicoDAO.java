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
        
        // Tabela de horários do médico
        String sqlHorarios = "CREATE TABLE IF NOT EXISTS horarios_medico ("
                   + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                   + "nome_medico TEXT NOT NULL,"
                   + "data TEXT NOT NULL,"
                   + "horario TEXT NOT NULL,"
                   + "disponivel INTEGER NOT NULL," 
                   + "UNIQUE(nome_medico, data, horario)"
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

    public Medico buscarPorEmail(String email) {
        String sql = "SELECT nome, crm, email, telefone FROM medicos WHERE email = ?";
        try (Connection conn = this.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, email);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return new Medico(
                    rs.getString("nome"),
                    rs.getString("crm"),
                    rs.getString("email"),
                    rs.getString("telefone"),
                    "" 
                );
            }
        } catch (SQLException e) {
            System.out.println("Erro ao buscar médico por email: " + e.getMessage());
        }
        return null;
    }

    public void excluirMedicoPorNome(String nome) {
        String sql = "DELETE FROM medicos WHERE nome = ?";
        try (Connection conn = this.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, nome);
            int linhasAfetadas = pstmt.executeUpdate();
            
            if (linhasAfetadas > 0) {
                System.out.println("Médico '" + nome + "' excluído com sucesso!");
            }
        } catch (SQLException e) {
            System.out.println("Erro ao excluir médico: " + e.getMessage());
        }
    }

   

    public int obterStatusHorario(String nomeMedico, String data, String horario) {
        String sql = "SELECT disponivel FROM horarios_medico WHERE nome_medico = ? AND data = ? AND horario = ?";
        try (Connection conn = this.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, nomeMedico);
            pstmt.setString(2, data);
            pstmt.setString(3, horario);
            
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("disponivel");
            }
        } catch (SQLException e) {
            System.out.println("Erro ao obter status da agenda: " + e.getMessage());
        }
        return 1; 
    }

    public boolean alternarStatusHorario(String nomeMedico, String data, String horario, int novoStatus) {
        String sql = "INSERT INTO horarios_medico(nome_medico, data, horario, disponivel) VALUES(?,?,?,?) "
                   + "ON CONFLICT(nome_medico, data, horario) DO UPDATE SET disponivel = excluded.disponivel";
        try (Connection conn = this.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, nomeMedico);
            pstmt.setString(2, data);
            pstmt.setString(3, horario);
            pstmt.setInt(4, novoStatus); 
            
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.out.println("Erro ao salvar alteração de horário: " + e.getMessage());
            return false;
        }
    }
    
    public boolean atualizarPerfil(Medico medico) {
        String sql = "UPDATE medicos SET nome = ?, telefone = ? WHERE email = ?";
        try (Connection conn = this.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, medico.getNome());
            pstmt.setString(2, medico.getTelefone());
            pstmt.setString(3, medico.getEmail()); 
            
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.out.println("Erro ao atualizar perfil do médico: " + e.getMessage());
            return false;
        }
    }

   
    public boolean atualizarPerfilComSenha(Medico medico, String novaSenhaCriptografada) {
        String sql = "UPDATE medicos SET nome = ?, telefone = ?, senha_criptografada = ? WHERE email = ?";
        try (Connection conn = this.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, medico.getNome());
            pstmt.setString(2, medico.getTelefone());
            pstmt.setString(3, novaSenhaCriptografada);
            pstmt.setString(4, medico.getEmail());
            
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.out.println("Erro ao atualizar perfil e senha do médico: " + e.getMessage());
            return false;
        }
    }
}