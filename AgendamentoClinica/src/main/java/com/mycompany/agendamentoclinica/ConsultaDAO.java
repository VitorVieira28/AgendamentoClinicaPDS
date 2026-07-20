package com.mycompany.agendamentoclinica;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class ConsultaDAO {

    private static final String URL = "jdbc:sqlite:clinica.db";

    public ConsultaDAO() {
        criarTabelaSeNaoExistir();
    }

    private Connection conectar() throws SQLException {
        return DriverManager.getConnection(URL);
    }

    private void criarTabelaSeNaoExistir() {
        String sql = "CREATE TABLE IF NOT EXISTS consultas ("
                   + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                   + "paciente_cpf TEXT NOT NULL,"
                   + "medico_nome TEXT NOT NULL,"
                   + "data TEXT NOT NULL,"
                   + "horario TEXT NOT NULL"
                   + ");";
        
        try (Connection conn = this.conectar();
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            System.out.println("Erro ao criar tabela de consultas: " + e.getMessage());
        }
    }

 
    public boolean agendarConsulta(String cpfPaciente, String nomeMedico, String data, String horario) {
        String sql = "INSERT INTO consultas(paciente_cpf, medico_nome, data, horario) VALUES(?,?,?,?)";

        try (Connection conn = this.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, cpfPaciente);
            pstmt.setString(2, nomeMedico);
            pstmt.setString(3, data);
            pstmt.setString(4, horario);
            
            pstmt.executeUpdate();
            return true;
            
        } catch (SQLException e) {
            System.out.println("Erro ao salvar consulta no SQLite: " + e.getMessage());
            return false;
        }
    }

   
    public String[] buscarConsultaAtiva(String cpfPaciente) {
        String sql = "SELECT medico_nome, data, horario FROM consultas WHERE paciente_cpf = ?";
        try (Connection conn = this.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, cpfPaciente);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                
                return new String[]{
                    rs.getString("medico_nome"), 
                    rs.getString("data"), 
                    rs.getString("horario")
                };
            }
        } catch (SQLException e) {
            System.out.println("Erro ao buscar consulta: " + e.getMessage());
        }
        return null; 
    }

    
    public boolean cancelarConsulta(String cpfPaciente) {
        String sql = "DELETE FROM consultas WHERE paciente_cpf = ?";
        try (Connection conn = this.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, cpfPaciente);
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.out.println("Erro ao cancelar consulta: " + e.getMessage());
            return false;
        }
    }

    
    public String buscarNomePaciente(String nomeMedico, String data, String horario) {
        String sqlCpf = "SELECT paciente_cpf FROM consultas WHERE medico_nome = ? AND data = ? AND horario = ?";
        
        try (java.sql.Connection conn = this.conectar();
             java.sql.PreparedStatement pstmt1 = conn.prepareStatement(sqlCpf)) {
            
            pstmt1.setString(1, nomeMedico);
            pstmt1.setString(2, data);
            pstmt1.setString(3, horario);
            
            java.sql.ResultSet rs1 = pstmt1.executeQuery();
            
            
            if (rs1.next()) {
                String cpf = rs1.getString("paciente_cpf");
                
                
                String sqlNome = "SELECT nome FROM pacientes WHERE cpf = ?";
                try (java.sql.PreparedStatement pstmt2 = conn.prepareStatement(sqlNome)) {
                    pstmt2.setString(1, cpf);
                    java.sql.ResultSet rs2 = pstmt2.executeQuery();
                    
                    if (rs2.next()) {
                        return rs2.getString("nome");
                    }
                }
            }
        } catch (java.sql.SQLException e) {
            System.out.println("Erro ao buscar nome do paciente: " + e.getMessage());
        }
        return null; 
    }

   
    public void cancelarConsultaPorMedico(String nomeMedico, String data, String horario) {
        String sql = "DELETE FROM consultas WHERE medico_nome = ? AND data = ? AND horario = ?";
        try (Connection conn = this.conectar();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, nomeMedico);
            pstmt.setString(2, data);
            pstmt.setString(3, horario);
            pstmt.executeUpdate();
            
        } catch (SQLException e) {
            System.out.println("Erro ao cancelar consulta pelo médico: " + e.getMessage());
        }
    }

    public void limparConsultasAntigas() {
        String sql = "DELETE FROM consultas WHERE data < ?";
        
        try (java.sql.Connection conn = this.conectar();
             java.sql.PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            String dataDeHoje = java.time.LocalDate.now().toString();
            
            pstmt.setString(1, dataDeHoje);
            int apagadas = pstmt.executeUpdate();
            
            if (apagadas > 0) {
                System.out.println(apagadas + " consulta(s) antiga(s) foi(ram) limpa(s) do sistema.");
            }
            
        } catch (java.sql.SQLException e) {
            System.out.println("Erro ao limpar consultas antigas: " + e.getMessage());
        }
    }
}