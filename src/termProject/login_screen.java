package termProject;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.PasswordField;

import javax.swing.*;
import java.net.URL;
import java.util.Locale;
import java.util.Objects;
import java.util.ResourceBundle;

import static javax.swing.JOptionPane.showMessageDialog;


public class login_screen extends Application implements Initializable {
    public Button forgot_button;
    public Button exitBtn;

    //<editor-fold desc="Variables>
    /**
     * Variable declarations.
     */
    @FXML
    private Button login_button;
    @FXML
    private Label login_location;
    @FXML
    private TextField username_field;
    @FXML
    private PasswordField password_field;
    /**
     *  Locale global variables
     */
    Locale currentLocale = Locale.getDefault();
    String myCountry = currentLocale.getCountry();
    String myLanguage = currentLocale.getDisplayLanguage();
    static String loggedUser;

    //</editor-fold

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("login_screen.fxml")));
        primaryStage.setTitle("Term Project Application");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }

    /**
     *Login Screen methods
     */
    public void forgotClick() {
        showMessageDialog(null,"Contact your system administrator for help.");
    }
    public void exitClick() {
        int confirmBtn = JOptionPane.YES_NO_OPTION;
        int resultBtn = JOptionPane.showConfirmDialog(null, "Are you sure you want to quit the program?", "Warning", confirmBtn);

        if (resultBtn == JOptionPane.YES_OPTION) {
            JDBC.closeConnection();
            System.out.println("exit clicked");
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
    public void loginClick() {
        try {
            String username = username_field.getText();
            String password = password_field.getText();

            if (loginCheck(username, password)) {
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
                showMessageDialog(null,"Username or password is incorrect.");
            }
        }
        catch (Exception e) {
            showMessageDialog(null,"Something went wrong. Contact your system administrator.\n" + "Error:" + e);
        }
    }
    public void setStuff() {
        System.out.println(myCountry);
        System.out.println(myLanguage);
        login_location.setText(myCountry);
    }
    public void initialize(URL location, ResourceBundle resources){
        //System.out.println("in login_screen initialize");
        setStuff();
    }

    public static void main(String[] args) {
        JDBC.openConnection();
        launch(args);
        JDBC.closeConnection();
    }
}