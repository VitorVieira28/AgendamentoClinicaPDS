package com.mycompany.agendamentoclinica;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DateCell;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ListCell; 

public class AgendaMedicoController {

    @FXML private DatePicker datePickerAgenda;
    @FXML private ListView<String> listaHorarios;
    @FXML private Label labelAviso;

    private final String[] horariosPadrao = {"08:00", "09:00", "10:00", "11:00", "14:00", "15:00", "16:00", "17:00"};
    private final MedicoDAO medicoDao = new MedicoDAO();
    private final ConsultaDAO consultaDao = new ConsultaDAO();

    @FXML
    public void initialize() {
        bloquearDatasPassadas();
        datePickerAgenda.setEditable(false);
        
        // NOVO: Abre o calendário clicando em qualquer lugar da caixa de texto!
        datePickerAgenda.getEditor().setOnMouseClicked(e -> datePickerAgenda.show());

        listaHorarios.setCellFactory(lv -> new ListCell<String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item);
                    if (item.contains("Ocupado")) {
                        setStyle("-fx-text-fill: #888888; -fx-font-style: italic;"); 
                    } else {
                        // Negrito removido! Ficará com a fonte padrão preta.
                        setStyle("-fx-text-fill: #000000;"); 
                    }
                }
            }
        });
    }

    private void bloquearDatasPassadas() {
        datePickerAgenda.setDayCellFactory(picker -> new DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                LocalDate hoje = LocalDate.now();
                setDisable(empty || date.compareTo(hoje) < 0);
            }
        });
    }

    @FXML
    private void carregarHorariosDaData() {
        LocalDate data = datePickerAgenda.getValue();
        if (data == null) return;
        
        Medico medico = App.getMedicoLogado();
        if (medico == null) return;
        
        String nomeMedico = medico.getNome();
        List<String> exibicao = new ArrayList<>();
        
        for (String hora : horariosPadrao) {
            int status = medicoDao.obterStatusHorario(nomeMedico, data.toString(), hora);
            
            if (status == 1) {
                exibicao.add(hora + " - Vago");
            } else {
                String nomePaciente = consultaDao.buscarNomePaciente(nomeMedico, data.toString(), hora);
                
                if (nomePaciente != null) {
                    exibicao.add(hora + " - Ocupado: " + nomePaciente);
                } else {
                    exibicao.add(hora + " - Ocupado"); 
                }
            }
        }
        listaHorarios.setItems(FXCollections.observableArrayList(exibicao));
    }

    @FXML
    private void alterarStatusSelecionado() {
        String itemSelecionado = listaHorarios.getSelectionModel().getSelectedItem();
        LocalDate data = datePickerAgenda.getValue();
        Medico medico = App.getMedicoLogado();
        
        if (itemSelecionado == null || data == null || medico == null) return;
        
        String hora = itemSelecionado.substring(0, 5);
        boolean estaOcupado = itemSelecionado.contains("Ocupado");
        
        Alert alerta = new Alert(Alert.AlertType.CONFIRMATION);
        alerta.setTitle("Confirmar Alteração");
        
        if (estaOcupado) {
            alerta.setHeaderText("Deseja deixar esse horario vago e cancelar com o paciente?");
            alerta.setContentText("");
        } else {
            alerta.setHeaderText("Deseja deixar esse horario ocupado para um paciente?");
            alerta.setContentText("");
        }

        Optional<ButtonType> resultado = alerta.showAndWait();
        
        if (resultado.isPresent() && resultado.get() == ButtonType.OK) {
            String nomeMedico = medico.getNome();
            int novoStatus = estaOcupado ? 1 : 0;
            
            if (estaOcupado && itemSelecionado.contains(":")) {
                consultaDao.cancelarConsultaPorMedico(nomeMedico, data.toString(), hora);
            }

            if (medicoDao.alternarStatusHorario(nomeMedico, data.toString(), hora, novoStatus)) {
                labelAviso.setText("Status alterado com sucesso!");
                labelAviso.setStyle("-fx-text-fill: green;");
                carregarHorariosDaData(); 
            }
        }
    }
@FXML
    private void abrirPerfilMedico() throws IOException {
        App.setRoot("perfil_medico"); 
    }
    @FXML
    private void fazerLogout() throws IOException {
        App.setMedicoLogado(null); 
        App.setRoot("primary");
    }
    
}