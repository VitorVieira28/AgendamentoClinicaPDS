package com.mycompany.agendamentoclinica;

import java.io.IOException;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;

public class AgendamentoController {

    @FXML private ComboBox<String> comboMedicos;
    @FXML private DatePicker datePickerData;
    @FXML private Label labelHorarios;
    @FXML private Label labelMensagem;

    // O método initialize roda sozinho assim que o JavaFX abre essa tela
    @FXML
    public void initialize() {
        // SCRUM-46: Buscar lista de médicos no banco
        MedicoDAO medicoDao = new MedicoDAO();
        List<String> nomesMedicos = medicoDao.listarNomesMedicos();

        // Converte a lista normal do Java para a lista que o JavaFX entende e joga na tela
        ObservableList<String> medicosObservable = FXCollections.observableArrayList(nomesMedicos);
        comboMedicos.setItems(medicosObservable);
    }

    // SCRUM-47: Ação disparada quando o paciente escolhe um médico na lista
    @FXML
    private void aoEscolherMedico() {
        String medicoEscolhido = comboMedicos.getValue();
        
        if (medicoEscolhido != null) {
            // Libera o calendário para clique
            datePickerData.setDisable(false);
            
            // Simula a liberação dos horários
            labelHorarios.setText("Calendário e horários liberados para Dr(a). " + medicoEscolhido + ".");
            labelHorarios.setStyle("-fx-text-fill: green; -fx-font-weight: bold;");
        }
    }

    @FXML
    private void clicouAgendar() {
        if (comboMedicos.getValue() == null || datePickerData.getValue() == null) {
            labelMensagem.setText("Erro: Por favor, selecione um médico e uma data.");
            labelMensagem.setStyle("-fx-text-fill: red;");
            return;
        }
        
        // Aqui no futuro entra o código para salvar a consulta no banco de dados
        labelMensagem.setText("Sucesso! Consulta agendada com " + comboMedicos.getValue() + ".");
        labelMensagem.setStyle("-fx-text-fill: green;");
    }

    @FXML
    private void voltarAoInicio() throws IOException {
        App.setRoot("primary");
    }
}