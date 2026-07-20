package com.mycompany.agendamentoclinica;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DateCell;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell; 
import javafx.scene.layout.VBox;

public class AgendamentoController {

    @FXML private ComboBox<String> comboMedicos;
    @FXML private DatePicker datePickerData;
    @FXML private ComboBox<String> comboHorarios; 
    @FXML private Label labelHorarios;
    @FXML private Label labelMensagem;
    
    @FXML private VBox painelConsultaAtiva;
    @FXML private VBox painelNovoAgendamento;
    @FXML private Label labelDetalhesConsulta;

    private final String[] horariosPadrao = {"08:00", "09:00", "10:00", "11:00", "14:00", "15:00", "16:00", "17:00"};
    private final MedicoDAO medicoDao = new MedicoDAO();
    private final ConsultaDAO consultaDao = new ConsultaDAO();

    @FXML
    public void initialize() {
        bloquearDatasPassadas();
        verificarConsultaAtiva();
        
        datePickerData.setEditable(false);
        
        // NOVO: Abre o calendário clicando em qualquer lugar da caixa de texto!
        datePickerData.getEditor().setOnMouseClicked(e -> datePickerData.show());
        
        List<String> nomesMedicos = medicoDao.listarNomesMedicos();
        comboMedicos.setItems(FXCollections.observableArrayList(nomesMedicos));

        comboHorarios.setCellFactory(lv -> new ListCell<String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setDisable(false);
                    setStyle("");
                } else {
                    setText(item);
                    if (item.contains("Ocupado")) {
                        setDisable(true); 
                        setStyle("-fx-text-fill: #b0b0b0;"); 
                    } else {
                        setDisable(false); 
                        setStyle("-fx-text-fill: #000000;"); 
                    }
                }
            }
        });
    }

    private void bloquearDatasPassadas() {
        datePickerData.setDayCellFactory(picker -> new DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                LocalDate hoje = LocalDate.now();
                setDisable(empty || date.compareTo(hoje) < 0);
            }
        });
    }

    private void verificarConsultaAtiva() {
        String cpf = App.getPacienteLogado().getCpf();
        String[] consulta = consultaDao.buscarConsultaAtiva(cpf);

        if (consulta != null) {
            painelConsultaAtiva.setVisible(true);
            painelConsultaAtiva.setManaged(true);
            painelNovoAgendamento.setVisible(false);
            painelNovoAgendamento.setManaged(false);
            
            labelDetalhesConsulta.setText("Médico: " + consulta[0] + " | Data: " + consulta[1] + " | Horário: " + consulta[2]);
        } else {
            painelConsultaAtiva.setVisible(false);
            painelConsultaAtiva.setManaged(false);
            painelNovoAgendamento.setVisible(true);
            painelNovoAgendamento.setManaged(true);
        }
    }

    @FXML
    private void aoEscolherMedico() {
        if (comboMedicos.getValue() != null) {
            datePickerData.setDisable(false);
            datePickerData.setValue(null);
            comboHorarios.setItems(null);
            comboHorarios.setDisable(true);
        }
    }

    @FXML
    private void aoEscolherData() {
        LocalDate data = datePickerData.getValue();
        String medico = comboMedicos.getValue();
        if (data == null || medico == null) return;

        List<String> horariosExibicao = new ArrayList<>();
        
        for (String hora : horariosPadrao) {
            int status = medicoDao.obterStatusHorario(medico, data.toString(), hora);
            if (status == 1) {
                horariosExibicao.add(hora);
            } else {
                horariosExibicao.add(hora + " - Ocupado");
            }
        }

        comboHorarios.setItems(FXCollections.observableArrayList(horariosExibicao));
        comboHorarios.setDisable(false); 
        labelHorarios.setText("Horários carregados com sucesso.");
    }

    @FXML
    private void clicouAgendar() {
        if (comboMedicos.getValue() == null || datePickerData.getValue() == null || comboHorarios.getValue() == null) return;
        
        String horaSelecionada = comboHorarios.getValue();
        
        if (horaSelecionada.contains("Ocupado")) {
            labelMensagem.setText("Este horário já está ocupado. Escolha outro!");
            labelMensagem.setStyle("-fx-text-fill: red;");
            return;
        }
        
        String cpf = App.getPacienteLogado().getCpf();
        String medico = comboMedicos.getValue();
        String data = datePickerData.getValue().toString();
        
        String horaLimpa = horaSelecionada.substring(0, 5);
        
        if (consultaDao.agendarConsulta(cpf, medico, data, horaLimpa)) {
            medicoDao.alternarStatusHorario(medico, data, horaLimpa, 0);
            
            labelMensagem.setText("Consulta agendada!");
            labelMensagem.setStyle("-fx-text-fill: green;");
            verificarConsultaAtiva(); 
        }
    }

    @FXML
    private void clicouCancelar() {
        String cpf = App.getPacienteLogado().getCpf();
        String[] consulta = consultaDao.buscarConsultaAtiva(cpf);
        
        if (consulta != null && consultaDao.cancelarConsulta(cpf)) {
            medicoDao.alternarStatusHorario(consulta[0], consulta[1], consulta[2], 1);
            
            labelMensagem.setText("Consulta cancelada com sucesso!");
            labelMensagem.setStyle("-fx-text-fill: orange;");
            
            comboMedicos.setValue(null);
            datePickerData.setValue(null);
            comboHorarios.setValue(null);
            verificarConsultaAtiva();
        }
    }

    @FXML
    private void voltarAoInicio() throws IOException {
        App.setRoot("primary");
    }
    @FXML
    private void abrirPerfilPaciente() throws java.io.IOException {
        App.setRoot("perfil_paciente"); 
    }
}