package com.mycompany.agendamentoclinica;

import java.io.IOException;
import java.util.Optional;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextInputDialog;

public class PrimaryController {

    @FXML
    private void irParaCadastroPaciente() throws IOException {
        App.setRoot("cadastro");
    }

    @FXML
    private void irParaLogin() throws IOException {
        App.setRoot("login"); 
    }

    @FXML
    private void irParaCadastroMedico() throws IOException {
        
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Segurança do Sistema");
        dialog.setHeaderText("Acesso Restrito para Profissionais");
        dialog.setContentText("Por favor, digite o Token de Registro da Clínica:");

        
        Optional<String> resultado = dialog.showAndWait();

     
        if (resultado.isPresent()) {
            String tokenDigitado = resultado.get();

           
            if (tokenDigitado.equals("123456")) {
                
                System.out.println("Token validado com sucesso! Abrindo cadastro...");
                App.setRoot("cadastro_medico"); // Abre a tela de cadastro do Médico

            } else {
                
                
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Acesso Negado");
                alert.setHeaderText("Token Inválido!");
                alert.setContentText("O token informado está incorreto. O registro de médicos é restrito à administração.");
                alert.showAndWait();
                
            }
        }
    }

    @FXML
    private void irParaLoginMedico() throws IOException {
        App.setRoot("login_medico"); 
    }
}