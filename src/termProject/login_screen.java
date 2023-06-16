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
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Locale;
import java.util.Objects;
import java.util.ResourceBundle;

import static javax.swing.JOptionPane.showMessageDialog;
public class login_screen extends Application implements Initializable {
    /**
     * Contains all the fields, labels, and variables needed to log in.
     * All the labels are buttons are needed so they can be translated into French.
     * Locale and language is based off of the system's locale.
     * The default language is set to English, since all the FXML files are made with English names, later to be translated to French when needed.
     */
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
    String getLanguage = currentLocale.getLanguage();
    public static boolean languageIsEnglish = true;
    static String loggedUser;
    int appointmentID;
    String appointmentUserTime;

    //</editor-fold

    /**
     * Starts off the program by loading the first screen, login_screen.
     * @param primaryStage the Stage that will be loaded in the window.
     * @throws Exception exception thrown in event of error.
     */
    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("login_screen.fxml")));
        primaryStage.setTitle("Term Project Application");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }

    /**
     * Simple forgot-password button.
     * Simply tells user to contact their administrator for support.
     * Can later be implemented with a real find-password function.
     */
    public void forgotClick() {
        if (login_screen.isEnglish()){
            showMessageDialog(null,"Contact your system administrator for help.");
        }
        else {
            showMessageDialog(null,"Contactez votre administrateur système pour obtenir de l'aide.");
        }
    }

    /**
     * Quits the program.
     * Prompts user with a YES-NO choice.
     * If YES, closes the DB connection and then exits.
     */
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

    /**
     *
     * @param username Takes in a username from the username field (implemented more in loginClick().
     * @param password Takes in a password from the password field (implemented more in loginClick().
     * @return Returns true if there is a result in the ResultSet, indicating there is a match on username and password.
     * @throws SQLException throws an SQL exception if met.
     */
    public static boolean loginCheck (String username, String password) throws SQLException {
        String sql = "SELECT User_Name FROM users WHERE User_Name = ? AND Password = ?";
        PreparedStatement ps = JDBC.connection.prepareStatement(sql);
        ps.setString(1,username);
        ps.setString(2,password);
        ResultSet rs = ps.executeQuery();
        return rs.isBeforeFirst();
    }

    /**
     * Used as part of getting the user for created-by details and users as part of appointments.
     * @return returns the variable loggedUser.
     */
    public static String getUsername(){
        return loggedUser;
    }

    /**
     * Used to set the username on successful login, so the right user can be accessed by getUsername().
     * @param userName takes in a successful login username and assigns to the class variable loggedUser.
     */
    public void setUserName(String userName){
        loggedUser = userName;
    }

    /**
     * Sets loggedUser to null when we use the LOGOUT function in the Home, Customer, and Appointment screens.
     */
    public static void unsetUsername(){
        //TODO: Maybe this will cause a problem, check back later.
        loggedUser = null;
    }
    //-----------------------------------------------------------------------

    //TODO future: Don't think I need these setters and getters with the class-wide default variables.
    /**
     * Setters and Getters for the message on login, checking if there is an appointment within 15min of login.
     * @param appointmentID gets an ID to set the class variable to.
     */
    public void setAppointmentIdMessage(int appointmentID){
        this.appointmentID = appointmentID;
    }
    public int getAppointmentIdMessage(){
        return appointmentID;
    }
    /**
     * Setters and Getters for the message on login, checking if there is an appointment within 15min of login.
     * @param appointmentUserTime gets a time to set the class variable to.
     */
    public void setAppointmentUserTimeMessage(String appointmentUserTime){
        this.appointmentUserTime = appointmentUserTime;
    }
    public String getAppointmentUserTime(){
        return appointmentUserTime;
    }
    //-----------------------------------------------------------------------
    /**
     * Returns the boolean languageIsEnglish. That boolean variable value may be changed in setStuff().
     */
    public static boolean isEnglish(){
        return languageIsEnglish;
    }

    /**
     * Performs the checks for a login when the login button is clicked.
     * Takes in the username field and password field and passes them to loginCheck to look for a match.
     * On successful match, creates a text file login_activity.txt if it doesn't exist.
     * Writes the current UTC times, username, and whether it was a successful or failed login to the text file.
     * On success, runs checkAppointmentsSoon, to see if an appointment exists within 15min of login.
     * On failure, shows a message saying the username or password is incorrect and wrties the result to the text file.
     */
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

                //Get 15-min or less appointment notice
                ResultSet rs = HomeController.checkAppointmentsSoon();

                if (rs.isBeforeFirst()) {
                    while (rs.next()) {
                        //Set and Get ID
                        int appointmentID = rs.getInt("Appointment_ID");
                        setAppointmentIdMessage(appointmentID);

                        //Set and Get converted time
                        String appointmentTimeUTC = rs.getString("Start");

                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                        LocalDateTime localDateTimeStart = LocalDateTime.parse(appointmentTimeUTC, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                        ZonedDateTime zonedDateTimeStart = localDateTimeStart.atZone(ZoneId.of("UTC")).withZoneSameInstant(ZoneOffset.systemDefault());

                        String truncatedTimeStart = zonedDateTimeStart.toString().replaceAll("T", " ").substring(0, 16);
                        String appointmentUserTime = LocalDate.parse(truncatedTimeStart.substring(0,10), formatter) + " " + truncatedTimeStart.replaceAll("T"," ").substring(11,16);
                        setAppointmentUserTimeMessage(appointmentUserTime);
                    }
                    if(isEnglish()){
                        showMessageDialog(null,"There is an appointment within 15 minutes.\n" + "Appointment_ID: " + getAppointmentIdMessage() + "\n" + "Date and Time: " + getAppointmentUserTime());
                    }
                    else {
                        showMessageDialog(null,"Il y a un rendez-vous dans les 15 minutes.\n" + "Rendez-vous_ID: " + getAppointmentIdMessage() + "\n" + "Date et l'heure: " + getAppointmentUserTime());
                    }
                }
                else {
                    if(isEnglish()){
                        showMessageDialog(null,"There are no upcoming appointments.");
                    }
                    else {
                        showMessageDialog(null,"Il n'y a pas de rendez-vous à venir.");
                    }
                }

                setUserName(username);
                //TODO future: use the loadScreen method.
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

    /**
     * These could be in the initialize function, but I've separated them this time.
     * When the program loads, check the locale and get the Display Language.\
     * Sets the languageIsEnglish boolean according to the result of a check on the display language equaling "English".
     */
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

    /**
     * Executes setStuff when the login screen is loaded.
     */
    public void initialize(URL location, ResourceBundle resources){
        setStuff();
    }

    /**
     * Starts the program by connecting to the DB.
     * Launches the window, which loads login_screen.
     * Closes the connection when the program is exited via X button on the window.
     */
    public static void main(String[] args) {
        JDBC.openConnection();
        launch(args);
        JDBC.closeConnection();
    }
}