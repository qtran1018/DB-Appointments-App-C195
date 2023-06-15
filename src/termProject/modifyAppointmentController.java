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

public class modifyAppointmentController {
    /**
     * Variables for FXML controllers and those used to modify the information.
     */
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
    public String[] selectedArr;
    public ResultSet rs;
    //</editor-fold

    /**
     * Observable lists of the possible appointment times and contacts as defined by the database.
     */
    //<editor-fold desc="ComboBox lists folded">
    final ObservableList<String> times = FXCollections.observableArrayList("00:00", "01:00", "02:00","03:00","04:00","05:00","06:00","07:00","08:00","09:00","10:00","11:00","12:00","13:00","14:00","15:00","16:00","17:00","18:00","19:00","20:00","21:00","21:00","22:00","23:00");
    ObservableList<String> contacts = FXCollections.observableArrayList();
    //</editor-fold

    /**
     * Exits the appointment-add window without saving any changes.
     */
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
     * Then checks the selected appointment times against that customer's existing appointments.
     * Lastly gets all field data and inserts into INSERT SQL statement after formatting dates and times.
     */
    @FXML
    public void appointmentSave() throws SQLException {

        //TODO future: make time conversion methods, similar to AppointmentQuery.isOutOfHours()
        //DATE formatter.
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        //From user's input of datetime to UTC.
        String appointmentStart = LocalDateTime.parse(appointmentStartDate.getValue() + " " + appointmentStartTime.getValue() + ":00", formatter).atZone(ZoneId.systemDefault()).withZoneSameInstant(ZoneOffset.UTC).truncatedTo(ChronoUnit.MILLIS).toString().replaceAll("[TZ]", " ").trim() + ":00";
        String appointmentEnd = LocalDateTime.parse(appointmentEndDate.getValue() + " " + appointmentEndTime.getValue() + ":00", formatter).atZone(ZoneId.systemDefault()).withZoneSameInstant(ZoneOffset.UTC).truncatedTo(ChronoUnit.MILLIS).toString().replaceAll("[TZ]", " ").trim() + ":00";

        if (appointmentTitleField.getText().isEmpty() ||
                appointmentDescField.getText().isEmpty() ||
                appointmentLocField.getText().isEmpty() ||
                appointmentTypeField.getText().isEmpty() ||
                appointmentStartDate.getValue() == null ||
                appointmentEndDate.getValue() == null ||
                appointmentStartTime.getValue().isEmpty() ||
                appointmentEndTime.getValue().isEmpty() ||
                appointmentCustomerID.getText().isEmpty() ||
                appointmentContact.getValue().isEmpty())
        {
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
        } else if (AppointmentQuery.timesOverlap(Integer.parseInt(appointmentCustomerID.getText()), appointmentStart, appointmentEnd)) {
            if (login_screen.isEnglish()) {
                showMessageDialog(null, "Error: appointment times for this customer overlap.");
            }
            else {
                showMessageDialog(null, "Erreur: les heures de rendez-vous pour ce client se chevauchent.");
            }
        } else {
            String appointmentTitle = appointmentTitleField.getText();
            String appointmentDescription = appointmentDescField.getText();
            String appointmentLocation = appointmentLocField.getText();
            String appointmentType = appointmentTypeField.getText();
            int appointmentID = Integer.parseInt(appointmentIDField.getText());

            String appointmentUpdateDate = Instant.now().truncatedTo(ChronoUnit.SECONDS).toString().replaceAll("[TZ]", " ");
            String appointmentUpdatedBy = login_screen.getUsername();
            int customerID = Integer.parseInt(appointmentCustomerID.getText());
            int userID = AppointmentQuery.selectUser();
            int contactID = AppointmentQuery.selectContactID(appointmentContact.getValue());

            //Doubles up on create-date and update-date, created-by and updated-by, since they will be the same.
            AppointmentQuery.appointmentUpdate(
                    appointmentTitle,
                    appointmentDescription,
                    appointmentLocation,
                    appointmentType,
                    appointmentStart,
                    appointmentEnd,
                    appointmentUpdateDate,
                    appointmentUpdatedBy,
                    customerID,
                    userID,
                    contactID,
                    appointmentID);
            if (login_screen.isEnglish()) {
                showMessageDialog(null, "Appointment updated.");
            } else {
                showMessageDialog(null, "Rendez-vous mis à jour.");
            }

            Stage modifyAppointment = (Stage) btnCancel.getScene().getWindow();
            modifyAppointment.close();
        }
    }

    /**
     * Function that allows data from the tableview in AppointmentQuery to be accessed by the modify-class.
     * Takes in an array and then assigns variables the values of its corresponding location in the array.
     * @param selectedArr the Array that is taken from the tableview, which contains the data of the appointment being modified.
     */
    public void initAppointmentData(String[] selectedArr) throws SQLException {
        this.selectedArr = selectedArr;

        String appointmentID = selectedArr[0];
        String title = selectedArr[1];
        String description = selectedArr[2];
        String location = selectedArr[3];
        String type = selectedArr[4];
        String customerID = selectedArr[11];
        String userID = selectedArr[12];

        //DATE formatter.
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String startDateTime = selectedArr[5];
        String endDateTime = selectedArr[6];
        //Makes LocalDate type, so it can be set to the DatePicker boxes. Substring is to pick only the date, leave the time.
        LocalDate startDate;
        LocalDate endDate;

        LocalDateTime localDateTimeStart = LocalDateTime.parse(startDateTime, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        ZonedDateTime zonedDateTimeStart = localDateTimeStart.atZone(ZoneId.of("UTC")).withZoneSameInstant(ZoneOffset.systemDefault());

        LocalDateTime localDateTimeEnd = LocalDateTime.parse(endDateTime, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        ZonedDateTime zonedDateTimeEnd = localDateTimeEnd.atZone(ZoneId.of("UTC")).withZoneSameInstant(ZoneOffset.systemDefault());

        //Should be 2023-06-15 04:00
        //          2023-06-15 06:00
        //String replacedStart = zonedDateTimeStart.toString().replaceAll("[T]"," ").substring(0,16);
        //String replacedEnd = zonedDateTimeEnd.toString().replaceAll("[T]"," ").substring(0,16);

        //SENT TO DATEPICKER
        String truncatedTimeStart = zonedDateTimeStart.toString().replaceAll("T", " ").substring(0, 16);
        startDate = LocalDate.parse(truncatedTimeStart.substring(0,10), formatter);
        String truncatedTimeEnd = zonedDateTimeEnd.toString().replaceAll("T", " ").substring(0, 16);
        endDate = LocalDate.parse(truncatedTimeEnd.substring(0,10), formatter);

        //Test
        //System.out.println(zonedDateTimeStart.toString().replaceAll("T"," ").substring(17,22));
        //System.out.println(zonedDateTimeEnd.toString().replaceAll("T"," ").substring(17,22));

        int contactID = Integer.parseInt(selectedArr[13]);
        String contactUserName = AppointmentQuery.selectContactName(contactID);

        appointmentIDField.setText(appointmentID);
        appointmentTitleField.setText(title);
        appointmentDescField.setText(description);
        appointmentLocField.setText(location);
        appointmentTypeField.setText(type);
        appointmentCustomerID.setText(customerID);
        appointmentUserID.setText(userID);
        appointmentContact.setValue(contactUserName);

        appointmentStartDate.setValue(startDate);
        appointmentEndDate.setValue(endDate);
        appointmentStartTime.setValue(truncatedTimeStart.replaceAll("T"," ").substring(11,16));
        appointmentEndTime.setValue(truncatedTimeEnd.replaceAll("T"," ").substring(11,16));


    }

    /**
     * At the start of loading the modify screen, queries for a result list of contacts in the database, then adds each result in the set to the contact list.
     * Sets the observable lists to the combo boxes.
     */
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

        if (!login_screen.isEnglish()) {
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
