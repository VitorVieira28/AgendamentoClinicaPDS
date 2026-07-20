package com.mycompany.agendamentoclinica;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
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
    @FXML private ComboBox<String> comboHorarios; // NOVO: Campo de Horários
    @FXML private Label labelHorarios;
    @FXML private Label labelMensagem;

    private final String[] horariosPadrao = {"08:00", "09:00", "10:00", "11:00", "14:00", "15:00", "16:00", "17:00"};

    @FXML
    public void initialize() {
        MedicoDAO medicoDao = new MedicoDAO();
        List<String> nomesMedicos = medicoDao.listarNomesMedicos();
        ObservableList<String> medicosObservable = FXCollections.observableArrayList(nomesMedicos);
        comboMedicos.setItems(medicosObservable);
    }

    @FXML
    private void aoEscolherMedico() {
        if (comboMedicos.getValue() != null) {
            datePickerData.setDisable(false);
            labelHorarios.setText("Selecione uma data para ver os horários disponíveis.");
            labelHorarios.setStyle("-fx-text-fill: green;");
            
            // Limpa a data e horários caso o paciente troque de médico na metade do processo
            datePickerData.setValue(null);
            comboHorarios.setItems(null);
            comboHorarios.setDisable(true);
        }
    }

    // NOVO: Disparado ao escolher a data no calendário
    @FXML
    private void aoEscolherData() {
        LocalDate dataSelecionada = datePickerData.getValue();
        if (dataSelecionada == null || comboMedicos.getValue() == null) return;

        MedicoDAO medicoDao = new MedicoDAO();
        String dataStr = dataSelecionada.toString();
        List<String> horariosLivres = new ArrayList<>();

        // Verifica no banco de dados quais horários estão vagos (1) para aquela data
        for (String hora : horariosPadrao) {
            if (medicoDao.obterStatusHorario(dataStr, hora) == 1) {
                horariosLivres.add(hora);
            }
        }

        if (horariosLivres.isEmpty()) {
            labelHorarios.setText("Nenhum horário disponível para esta data.");
            labelHorarios.setStyle("-fx-text-fill: red;");
            comboHorarios.setDisable(true);
            comboHorarios.setItems(null);
        } else {
            comboHorarios.setItems(FXCollections.observableArrayList(horariosLivres));
            comboHorarios.setDisable(false); // Destrava a caixa de horários
            labelHorarios.setText("Horários carregados com sucesso.");
            labelHorarios.setStyle("-fx-text-fill: green;");
        }
    }

    @FXML
    private void clicouAgendar() {
        // Agora valida se o horário também foi preenchido
        if (comboMedicos.getValue() == null || datePickerData.getValue() == null || comboHorarios.getValue() == null) {
            labelMensagem.setText("Erro: Preencha o médico, a data e o horário.");
            labelMensagem.setStyle("-fx-text-fill: red;");
            return;
        }
        
        labelMensagem.setText("Sucesso! Consulta agendada com " + comboMedicos.getValue() + " às " + comboHorarios.getValue() + ".");
        labelMensagem.setStyle("-fx-text-fill: green; -fx-font-weight: bold;");
    }

    @FXML
    private void voltarAoInicio() throws IOException {
        App.setRoot("primary");
    }
}