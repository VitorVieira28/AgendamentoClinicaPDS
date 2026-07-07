package com.mycompany.agendamentoclinica;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class App extends Application {

    private static Scene scene;

    @Override
    public void start(Stage stage) throws IOException {
        // Carrega a tela inicial (primary.fxml) com o tamanho de 640x480
        scene = new Scene(loadFXML("primary"), 640, 480);
        stage.setScene(scene);
        stage.setTitle("Agendamento de Clínica"); // Título da janela
        stage.show();
    }

    // Método usado para trocar de tela (ex: ir da Primary para a Secondary)
    static void setRoot(String fxml) throws IOException {
        scene.setRoot(loadFXML(fxml));
    }

    // Método interno que busca o arquivo .fxml na pasta resources
    private static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(fxml + ".fxml"));
        return fxmlLoader.load();
    }

    // O VERDADEIRO MÉTODO MAIN QUE O NETBEANS ESTAVA PROCURANDO
    public static void main(String[] args) {
        launch(); // Dá o start na aplicação visual
    }
}