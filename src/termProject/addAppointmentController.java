package termProject;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import static javax.swing.JOptionPane.showMessageDialog;

public class addAppointmentController {

    //<editor-fold desc="Variables folded">
    public Button saveAppointment;
    public TextField appointmentTitleField;
    public TextField appointmentDescField;
    public TextField appointmentLocField;
    public TextField appointmentCustomerID;
    public TextField appointmentIDField;
    public TextField appointmentTypeField;
    public DatePicker appointmentStartDate;
    public ComboBox<String> appointmentStartTime;
    public ComboBox<String> appointmentEndTime;
    public DatePicker appointmentEndDate;
    public TextField appointmentUserID;
    public ComboBox<String> appointmentContact;
    public ResultSet rs;
    public Label labelTitle;
    public Label labelType;
    public Label labelDescription;
    public Label labelLocation;
    public Label labelStartDate;
    public Label labelCustomerID;
    public Label labelAppointment;
    public Label labelAppointmentID;
    public Label labelUserID;
    public Label labelContact;
    public Label labelEndDate;
    public Label labelStartTime;
    public Label labelEndTime;
    @FXML
    private Button btnCancel;
    //</editor-fold

    //<editor-fold desc="ComboBox lists folded">
    final ObservableList<String> times = FXCollections.observableArrayList("00:00", "01:00", "02:00","03:00","04:00","05:00","06:00","07:00","08:00","09:00","10:00","11:00","12:00","13:00","14:00","15:00","16:00","17:00","18:00","19:00","20:00","21:00","21:00","22:00","23:00");
    ObservableList<String> contacts = FXCollections.observableArrayList();

    //</editor-fold
    @FXML
    public void appointmentCancel() {
        Stage appointmentAddStage = (Stage) btnCancel.getScene().getWindow();
        //Closes current window
        appointmentAddStage.close();
    }

    /**
     * Appointment Save function.
     * First checks for DISQUALIFYING information.
     * Checks if data fields are empty.
     * Then checks against business hours, if it is OUT of business hours, with a concatenation of date and time fields as parameters for the starting and ending date-times.
     * Lastly gets all field data and inserts into INSERT SQL statement after formatting dates and times.
     * @throws SQLException
     */
    @FXML
    public void appointmentSave() throws SQLException {
        if (appointmentTitleField.getText().isEmpty() ||
                appointmentDescField.getText().isEmpty() ||
                appointmentLocField.getText().isEmpty() ||
                appointmentTypeField.getText().isEmpty() ||
                appointmentStartDate.getValue() == null ||
                appointmentEndDate.getValue() == null ||
                appointmentStartTime.getValue().isEmpty() ||
                appointmentEndTime.getValue().isEmpty() ||
                appointmentCustomerID.getText().isEmpty() ||
                appointmentContact.getValue().isEmpty()) {
            if (login_screen.isEnglish()) {
                showMessageDialog(null, "Error: All fields are required.");
            }
            else {
                showMessageDialog(null, "Erreur: Tous les champs sont obligatoires.");
            }
        }
        else if (AppointmentQuery.isOutOfHours(appointmentStartDate.getValue().toString() + " " + appointmentStartTime.getValue(), appointmentEndDate.getValue().toString() + " " + appointmentEndTime.getValue())) {
            if (login_screen.isEnglish()) {
                showMessageDialog(null, "Error: Outside business hours.");
            }
            else {
                showMessageDialog(null, "Erreur: En dehors des heures d'ouverture.");
            }
        }
        else {
            String appointmentTitle = appointmentTitleField.getText();
            String appointmentDescription = appointmentDescField.getText();
            String appointmentLocation = appointmentLocField.getText();
            String appointmentType = appointmentTypeField.getText();
            String appointmentCreateDate = Instant.now().truncatedTo(ChronoUnit.SECONDS).toString().replaceAll("[TZ]", " ");
            String appointmentCreatedBy = login_screen.getUsername();

            int customerID = Integer.parseInt(appointmentCustomerID.getText());
            int userID = AppointmentQuery.selectUser();
            int contactID = AppointmentQuery.selectContactID(appointmentContact.getValue());

            //TODO future: make time conversion methods, similar to AppointmentQuery.isOutOfHours()
            //DATE formatter.
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            //From user's input of datetime to UTC.
            String appointmentStart = LocalDateTime.parse(appointmentStartDate.getValue() + " " + appointmentStartTime.getValue() + ":00", formatter).atZone(ZoneId.systemDefault()).withZoneSameInstant(ZoneOffset.UTC).truncatedTo(ChronoUnit.MILLIS).toString().replaceAll("[TZ]", " ").trim() + ":00";
            String appointmentEnd = LocalDateTime.parse(appointmentEndDate.getValue() + " " + appointmentEndTime.getValue() + ":00", formatter).atZone(ZoneId.systemDefault()).withZoneSameInstant(ZoneOffset.UTC).truncatedTo(ChronoUnit.MILLIS).toString().replaceAll("[TZ]", " ").trim() + ":00";

            //Doubles up on create-date and update-date, created-by and updated-by, since they will be the same.
            AppointmentQuery.appointmentInsert(
                    appointmentTitle,
                    appointmentDescription,
                    appointmentLocation,
                    appointmentType,
                    appointmentStart,
                    appointmentEnd,
                    appointmentCreateDate,
                    appointmentCreatedBy,
                    appointmentCreateDate,
                    appointmentCreatedBy,
                    customerID,
                    userID,
                    contactID);
            if (login_screen.isEnglish()) {
                showMessageDialog(null, "Appointment added.");
            } else {
                showMessageDialog(null, "Rendez-vous ajouté.");
            }
            Stage addAppointment = (Stage) btnCancel.getScene().getWindow();
            addAppointment.close();
        }
    }
    @FXML
    void initialize() throws SQLException {
        appointmentIDField.setDisable(true);
        appointmentUserID.setDisable(true);
        appointmentStartTime.getItems().clear();
        appointmentEndTime.getItems().clear();
        appointmentContact.getItems().clear();
        appointmentStartTime.setItems(times);
        appointmentEndTime.setItems(times);

        //Could have the ObservableList get pass through instead, then set to the combo box.
        contacts.clear();
        rs = AppointmentQuery.getContactList();
        while(rs.next()){
            contacts.add(rs.getString("Contact_Name"));
        }
        appointmentContact.setItems(contacts);

        if (!login_screen.isEnglish()){
            //TODO: labelnav is too long in french, fix.
            labelAppointment.setText("Rendez-vous");
            labelAppointmentID.setText("ID de rendez-vous");
            labelTitle.setText("Titre");
            labelDescription.setText("Description");
            labelLocation.setText("Emplacement");
            labelType.setText("Taper");
            labelStartDate.setText("Date de début");
            labelEndDate.setText("Date de fin");
            labelStartTime.setText("Heure de début");
            labelEndTime.setText("Heure de fin");
            labelCustomerID.setText("N ° de client");
            labelUserID.setText("ID de l'utilisateur");
            labelContact.setText("Contact");
            saveAppointment.setText("Sauvegarder");
            btnCancel.setText("Annuler");
        }
    }

}
