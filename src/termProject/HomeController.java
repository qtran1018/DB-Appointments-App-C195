package termProject;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import javax.swing.*;
import java.io.IOException;

public class HomeController {

    @FXML
    private Button btnHome;

    public void loadScreen(String screenName) throws IOException {
        FXMLLoader screenLoader = new FXMLLoader(login_screen.class.getResource(screenName));
        Parent screenRoot = screenLoader.load();
        Stage screenStage = new Stage();
        screenStage.setScene(new Scene(screenRoot));
        screenStage.setTitle("Term Project Application");
        screenStage.show();

        Stage closeStage = (Stage) btnHome.getScene().getWindow();
        closeStage.close();
    }
    public void logoutClick() throws IOException {
        //TODO: see if logClick and loginClick exception being different is a problem. One throws IOException, other does Try-Catch
        int confirmBtn = JOptionPane.YES_NO_OPTION;
        int resultBtn = JOptionPane.showConfirmDialog(null, "Are you sure you want to log out?", "Warning", confirmBtn);

        if (resultBtn == JOptionPane.YES_OPTION) {
            loadScreen("login_screen.fxml");
        }
    }
    public void customersClick() throws IOException {
        loadScreen("customers_screen.fxml");
    }
    public void appointmentsClick() throws IOException {
        loadScreen("appointments_screen.fxml");
    }
}
