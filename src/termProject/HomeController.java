package termProject;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import javax.swing.*;
import java.io.IOException;

public class HomeController {

    //<editor-fold desc="Variables folded">
    public Label labelNav;
    public Label labelTables;
    public Button btnCustomers;
    public Button btnAppointments;
    public Button btnLogout;
    public Label labelPage;
    /**
     * Variable declarations.
     */
    @FXML
    private Button btnHome;
    //</editor-fold

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
        int confirmBtn = JOptionPane.YES_NO_OPTION;
        int resultBtn;
        if (login_screen.isEnglish()){
            resultBtn = JOptionPane.showConfirmDialog(null, "Are you sure you want to logout?", "Warning", confirmBtn);
        }
        else {
            resultBtn = JOptionPane.showConfirmDialog(null, "Êtes-vous sûr de vouloir vous déconnecter?", "Avertissement", confirmBtn);
        }
        if (resultBtn == JOptionPane.YES_OPTION) {
            login_screen.unsetUsername();
            loadScreen("login_screen.fxml");
        }
    }
    public void customersClick() throws IOException {
        loadScreen("customers_screen.fxml");
    }
    public void appointmentsClick() throws IOException {
        loadScreen("appointments_screen.fxml");
    }
    public void initialize(){
        if (login_screen.isEnglish()) {/*Do nothing*/}
        else {
            //TODO: labelnav is too long in french, fix.
            labelNav.setText("La navigation");
            btnHome.setText("Maison");
            labelTables.setText("Les tables");
            btnCustomers.setText("Clients");
            btnAppointments.setText("Rendez-vous");
            btnLogout.setText("Se déconnecter");
            labelPage.setText("Page d'accueil");

        }
    }
}
