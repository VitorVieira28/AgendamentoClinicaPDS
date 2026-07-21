package com.mycompany.agendamentoclinica;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;
import java.util.List;
import java.util.Random; 

public class App extends Application {

    private static Scene scene;
    
    
    private static Paciente pacienteLogado;

    public static Paciente getPacienteLogado() {
        return pacienteLogado;
    }

    public static void setPacienteLogado(Paciente paciente) {
        pacienteLogado = paciente;
    }

    
    private static Medico medicoLogado;

    public static Medico getMedicoLogado() {
        return medicoLogado;
    }

    public static void setMedicoLogado(Medico medico) {
        medicoLogado = medico;
    }

    @Override
    public void start(Stage stage) throws IOException {
        
        ConsultaDAO consultaDao = new ConsultaDAO();
        consultaDao.limparConsultasAntigas();

        try (java.sql.Connection conn = java.sql.DriverManager.getConnection("jdbc:sqlite:clinica.db");
             java.sql.Statement stmt = conn.createStatement()) {
            stmt.execute("DROP TABLE IF EXISTS horarios_medico");
            System.out.println("Tabela de horários resetada com sucesso!");
        } catch (Exception e) {}
        
        MedicoDAO dao = new MedicoDAO();
        dao.excluirMedicoPorNome("vitor"); 
        gerarHorariosOcupadosTeste();      
        

        scene = new Scene(loadFXML("primary"), 640, 480);
        stage.setScene(scene);
        stage.setTitle("Agendamento de Clínica");
        stage.show();
    }

    static void setRoot(String fxml) throws IOException {
        scene.setRoot(loadFXML(fxml));
    }

    private static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(fxml + ".fxml"));
        return fxmlLoader.load();
    }

    public static void main(String[] args) {
        launch();
    }

    public static void gerarHorariosOcupadosTeste() {
        MedicoDAO medicoDao = new MedicoDAO();
        List<String> medicos = medicoDao.listarNomesMedicos();
        
        if (medicos.isEmpty()) return; 
        
        String[] todosHorarios = {"08:00", "09:00", "10:00", "11:00", "14:00", "15:00", "16:00", "17:00"};
        java.time.LocalDate hoje = java.time.LocalDate.now();
        Random random = new Random();
        
        
        for (int i = 0; i < 35; i++) {
            String medico = medicos.get(random.nextInt(medicos.size()));
            
            
            int diasParaFrente = Math.min(random.nextInt(7), random.nextInt(7)); 
            
            String data = hoje.plusDays(diasParaFrente).toString();
            String horario = todosHorarios[random.nextInt(todosHorarios.length)];
            
            medicoDao.alternarStatusHorario(medico, data, horario, 0);
        }
        
        System.out.println("Agenda populada com horários focados nos dias mais próximos!");
    }
}