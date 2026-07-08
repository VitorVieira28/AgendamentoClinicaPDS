package com.mycompany.agendamentoclinica;

import java.io.IOException;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.layout.VBox;

public class PrimaryController {

    @FXML private VBox painelPrincipal; // Puxa o fundo da tela

    // Esse método roda automaticamente quando a tela abre
    @FXML
    public void initialize() {
        // Joga o foco para o fundo da tela, tirando a marcação do botão
        Platform.runLater(() -> painelPrincipal.requestFocus());
    }

    @FXML
    private void irParaCadastro() throws IOException {
        App.setRoot("cadastro"); 
    }

    @FXML
    private void irParaLogin() throws IOException {
        App.setRoot("login"); 
    }
}