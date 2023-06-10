package termProject;

import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import javax.swing.*;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Locale;
import java.util.Objects;
import java.util.ResourceBundle;

import static javax.swing.JOptionPane.showMessageDialog;


public class login_screen extends Application implements Initializable {

    //<editor-fold desc="Variables folded">
    public Button forgot_button;
    public Button exitBtn;
    public Label labelLocation;
    public Label labelBigLogin;
    @FXML
    private Button login_button;
    @FXML
    private Label login_location;
    @FXML
    private TextField username_field;
    @FXML
    private PasswordField password_field;
    Locale currentLocale = Locale.getDefault();
    String myCountry = currentLocale.getCountry();
    String getLanguage = currentLocale.getDisplayLanguage();
    public static boolean languageIsEnglish = true;
    static String loggedUser;

    //</editor-fold
    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("login_screen.fxml")));
        primaryStage.setTitle("Term Project Application");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }
    public void forgotClick() {
        if (login_screen.isEnglish()){
            showMessageDialog(null,"Contact your system administrator for help.");
        }
        else {
            showMessageDialog(null,"Contactez votre administrateur système pour obtenir de l'aide.");
        }
    }
    public void exitClick() {
        int confirmBtn = JOptionPane.YES_NO_OPTION;
        int resultBtn;
        if (login_screen.isEnglish()){
            resultBtn = JOptionPane.showConfirmDialog(null, "Are you sure you want to quit the program?", "Warning", confirmBtn);
        }
        else {
            resultBtn = JOptionPane.showConfirmDialog(null, "Voulez-vous vraiment quitter le programme?", "Avertissement", confirmBtn);
        }
        if (resultBtn == JOptionPane.YES_OPTION) {
            JDBC.closeConnection();
            System.exit(0);
        }
    }
    public static boolean loginCheck (String username, String password) throws SQLException {
        String sql = "SELECT User_Name FROM users WHERE User_Name = ? AND Password = ?";
        PreparedStatement ps = JDBC.connection.prepareStatement(sql);
        ps.setString(1,username);
        ps.setString(2,password);
        ResultSet rs = ps.executeQuery();

        return rs.isBeforeFirst();
    }
    public static String getUsername(){
        return loggedUser;
    }
    public void setUserName(String userName){
        loggedUser = userName;
    }
    public static void unsetUsername(){
        //TODO: Maybe this will cause a problem, check back later.
        loggedUser = null;
    }
    public static boolean isEnglish(){
        return languageIsEnglish;
    }
    public void loginClick() {
        try {
            String username = username_field.getText();
            String password = password_field.getText();

            if (loginCheck(username, password)) {
                try (FileWriter fw = new FileWriter("login_activity.txt", true);
                     BufferedWriter bw = new BufferedWriter(fw))
                {
                    bw.write(Instant.now().truncatedTo(ChronoUnit.SECONDS).toString().replaceAll("[TZ]"," ") + "|" + username + "|" + "Success\n");
                    bw.flush();
                } catch (IOException e) {
                    System.out.println("An error occurred.");
                    e.printStackTrace();
                }

                setUserName(username);
                //Future improvement: use the loadScreen method.
                FXMLLoader screenLoader = new FXMLLoader(getClass().getResource("home_screen.fxml"));
                Parent screenRoot = screenLoader.load();
                Stage screenStage = new Stage();
                screenStage.setScene(new Scene(screenRoot));
                screenStage.setTitle("Term Project Application");
                screenStage.show();

                Stage closeStage = (Stage) login_button.getScene().getWindow();
                closeStage.close();
            }
            else {
                if (login_screen.isEnglish()){
                    showMessageDialog(null,"Username or password is incorrect.");
                    if (username.isEmpty()){
                        username = "(blank)";
                    }
                }
                else {
                    if (username.isEmpty()){
                        username = "(blank)";
                    }
                    showMessageDialog(null,"L'identifiant ou le mot de passe est incorrect.");
                }
                try (FileWriter fw = new FileWriter("login_activity.txt", true);
                     BufferedWriter bw = new BufferedWriter(fw))
                {
                    bw.write(Instant.now().truncatedTo(ChronoUnit.SECONDS).toString().replaceAll("[TZ]"," ") + "|" + username + "|" + "Failed\n");
                    bw.flush();
                } catch (IOException e) {
                    System.out.println("An error occurred.");
                    e.printStackTrace();
                }
            }
        }
        catch (Exception e) {
            if (login_screen.isEnglish()){
                showMessageDialog(null,"Something went wrong. Contact your system administrator.\n" + "Error:" + e);
            }
            else {
                showMessageDialog(null,"Quelque chose s'est mal passé. Contactez votre administrateur système.\n" + "Erreur: " + e);
            }
        }
    }
    public void setStuff() {
        login_location.setText(myCountry);
        languageIsEnglish = getLanguage.equals("English");
        if (!login_screen.isEnglish()){
            labelBigLogin.setText("Bienvenu!");
            username_field.setPromptText("Nom d'utilisateur");
            password_field.setPromptText("Mot de passe");
            login_button.setText("Connexion");
            forgot_button.setText("Oublié?");
            labelLocation.setText("Emplacement:");
            exitBtn.setText("Arrêter");
        }
    }
    public void initialize(URL location, ResourceBundle resources){
        setStuff();
    }
    public static void main(String[] args) {
        JDBC.openConnection();
        launch(args);
        JDBC.closeConnection();
    }
}