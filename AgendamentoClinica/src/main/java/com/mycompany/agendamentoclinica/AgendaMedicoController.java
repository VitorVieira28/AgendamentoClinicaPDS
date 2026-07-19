package com.mycompany.agendamentoclinica;

import java.io.IOException;
import java.time.LocalDate;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;

public class AgendaMedicoController {

    @FXML private DatePicker datePickerAgenda;
    @FXML private ListView<String> listaHorarios;
    @FXML private Label labelAviso;

    private final MedicoDAO medicoDao = new MedicoDAO();
    private final String[] horariosPadrao = {"08:00", "09:00", "10:00", "11:00", "14:00", "15:00", "16:00", "17:00"};

    @FXML
    public void initialize() {
        // Inicializa com a data de hoje selecionada
        datePickerAgenda.setValue(LocalDate.now());
        carregarHorariosDaData();
    }

    // SCRUM-50: Disparado ao trocar a data ou clicar para recarregar
    @FXML
    private void carregarHorariosDaData() {
        LocalDate dataSelecionada = datePickerAgenda.getValue();
        if (dataSelecionada == null) return;

        String dataStr = dataSelecionada.toString();
        ObservableList<String> itensLista = FXCollections.observableArrayList();

        for (String hora : horariosPadrao) {
            int status = medicoDao.obterStatusHorario(dataStr, hora);
            String textoStatus = (status == 1) ? " [VAGO]" : " [INDISPONÍVEL]";
            itensLista.add(hora + textoStatus);
        }

        listaHorarios.setItems(itensLista);
    }

    // SCRUM-50: Lógica para clicar em um horário da lista e mudar o status dele
    @FXML
    private void alterarStatusSelecionado() {
        String itemSelecionado = listaHorarios.getSelectionModel().getSelectedItem();
        LocalDate dataSelecionada = datePickerAgenda.getValue();

        if (itemSelecionado == null || dataSelecionada == null) {
            labelAviso.setText("Selecione um horário na lista para alterar.");
            labelAviso.setStyle("-fx-text-fill: red;");
            return;
        }

        String dataStr = dataSelecionada.toString();
        String horario = itemSelecionado.substring(0, 5); // Pega apenas o "HH:MM"

        // Descobre o status atual com base no texto
        int statusAtual = itemSelecionado.contains("[VAGO]") ? 1 : 0;
        int novoStatus = (statusAtual == 1) ? 0 : 1; // Inverte o valor

        // Atualiza no SQLite
        medicoDao.alternarStatusHorario(dataStr, horario, novoStatus);
        
        // Recarrega a lista visual
        carregarHorariosDaData();
        
        labelAviso.setText("Horário das " + horario + " atualizado com sucesso!");
        labelAviso.setStyle("-fx-text-fill: green;");
    }

    @FXML
    private void fazerLogout() throws IOException {
        App.setRoot("primary");
    }
}