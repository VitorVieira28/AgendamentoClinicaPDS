package com.mycompany.agendamentoclinica;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DateCell;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.VBox;

public class AgendamentoController {

    @FXML private VBox painelPrincipal;
    @FXML private Label labelDadosPaciente;
    @FXML private DatePicker calendarioConsulta;
    @FXML private ComboBox<String> comboHorarios;
    @FXML private Label labelMensagem;

    private final List<LocalDate> diasTotalmenteOcupados = new ArrayList<>();
    private final Map<LocalDate, List<String>> horariosOcupadosPorDia = new HashMap<>();
    
    // Lista mestre com todos os horários da clínica
    private final List<String> todosHorarios = Arrays.asList("08:00", "09:00", "10:00", "14:00", "15:00", "16:00");

    public AgendamentoController() {
        Random random = new Random();

        // Faz uma simulação super realista para os próximos 30 dias
        for (int i = 0; i <= 30; i++) {
            LocalDate dataAvaliada = LocalDate.now().plusDays(i);

            // ZONA 1: HOJE (Sempre só 1 vaga livre para gerar urgência)
            if (i == 0) {
                int indiceSorteado = random.nextInt(todosHorarios.size());
                String horarioQueFicouLivre = todosHorarios.get(indiceSorteado);
                List<String> ocupadosDeHoje = new ArrayList<>();
                for (String hora : todosHorarios) {
                    if (!hora.equals(horarioQueFicouLivre)) {
                        ocupadosDeHoje.add(hora);
                    }
                }
                horariosOcupadosPorDia.put(dataAvaliada, ocupadosDeHoje);
            } 
            
            // ZONA 2: PRÓXIMOS 7 DIAS (Muito concorrido, agenda lotando)
            else if (i <= 7) {
                int tipoDia = random.nextInt(3); // Sorteia 0, 1 ou 2

                if (tipoDia == 0) {
                    // 33% de chance do dia estar LOTADO (Fica Vermelho)
                    diasTotalmenteOcupados.add(dataAvaliada);
                } else {
                    // 66% de chance de ter horários misturados, mas a maioria riscada
                    List<String> horasOcupadas = new ArrayList<>();
                    for (String hora : todosHorarios) {
                        if (random.nextInt(100) < 75) { // 75% de chance daquele horário já estar pego
                            horasOcupadas.add(hora);
                        }
                    }
                    horariosOcupadosPorDia.put(dataAvaliada, horasOcupadas);
                }
            } 
            
            // ZONA 3: DIAS DISTANTES (+ de 1 semana) (Agenda livre, apenas riscos pontuais)
            else {
                List<String> horasOcupadas = new ArrayList<>();
                for (String hora : todosHorarios) {
                    // Apenas 20% de chance de alguém já ter marcado esse horário lá pro futuro
                    if (random.nextInt(100) < 20) { 
                        horasOcupadas.add(hora);
                    }
                }
                
                // Se sorteou algum horário ocupado, guarda no mapa
                if (!horasOcupadas.isEmpty()) {
                    horariosOcupadosPorDia.put(dataAvaliada, horasOcupadas);
                }
            }
        }
    }

    @FXML
    public void initialize() {
        Platform.runLater(() -> painelPrincipal.requestFocus());
        
        if (App.getPacienteLogado() != null) {
            String nome = App.getPacienteLogado().getNome();
            String cpf = App.getPacienteLogado().getCpf();
            labelDadosPaciente.setText("Paciente: " + nome + "   |   CPF: " + cpf);
        }

        comboHorarios.getItems().addAll(todosHorarios);
        comboHorarios.setDisable(true); 

        calendarioConsulta.setDayCellFactory(picker -> new DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                
                if (date.isBefore(LocalDate.now())) {
                    setDisable(true);
                    setStyle("-fx-background-color: #e6e6e6;");
                }
                else if (!empty && diasTotalmenteOcupados.contains(date)) {
                    setDisable(true);
                    setStyle("-fx-background-color: #ffcccc; -fx-text-fill: #b30000; -fx-font-weight: bold;");
                }
            }
        });

        calendarioConsulta.valueProperty().addListener((observable, antigoValor, dataEscolhida) -> {
            if (dataEscolhida != null) {
                comboHorarios.setDisable(false); 
                comboHorarios.setValue(null); 
                
                labelMensagem.setText("Escolha o horário para a data selecionada.");
                labelMensagem.setStyle("-fx-text-fill: #007bff;");
                
                atualizarVisualizacaoHorarios(dataEscolhida);
            }
        });
    }

    private void atualizarVisualizacaoHorarios(LocalDate dataEscolhida) {
        comboHorarios.setCellFactory(lv -> new ListCell<String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                    setDisable(false);
                } else {
                    if (horariosOcupadosPorDia.containsKey(dataEscolhida) && horariosOcupadosPorDia.get(dataEscolhida).contains(item)) {
                        setDisable(true); 
                        
                        javafx.scene.text.Text textoRiscado = new javafx.scene.text.Text(item + " (Ocupado)");
                        textoRiscado.setStrikethrough(true); 
                        textoRiscado.setFill(javafx.scene.paint.Color.GRAY); 
                        
                        setText(null); 
                        setGraphic(textoRiscado); 
                    } else {
                        setDisable(false); 
                        setText(item); 
                        setGraphic(null); 
                    }
                }
            }
        });
    }

    @FXML
    private void clicouMarcarConsulta() {
        if (calendarioConsulta.getValue() == null || comboHorarios.getValue() == null) {
            labelMensagem.setText("Erro: Por favor, selecione uma data e um horário.");
            labelMensagem.setStyle("-fx-text-fill: red;");
            return;
        }

        String dataEscolhida = calendarioConsulta.getValue().toString();
        String horaEscolhida = comboHorarios.getValue();
        
        if (horariosOcupadosPorDia.containsKey(calendarioConsulta.getValue()) && horariosOcupadosPorDia.get(calendarioConsulta.getValue()).contains(horaEscolhida)) {
            labelMensagem.setText("Erro: Este horário específico já está reservado.");
            labelMensagem.setStyle("-fx-text-fill: red;");
            return;
        }

        labelMensagem.setText("Sucesso! Consulta marcada para " + dataEscolhida + " às " + horaEscolhida);
        labelMensagem.setStyle("-fx-text-fill: green; -fx-font-weight: bold;");
    }

    @FXML
    private void voltarInicio() throws IOException {
        App.setRoot("primary");
    }
}