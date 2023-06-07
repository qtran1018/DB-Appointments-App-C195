package termProject;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import static javax.swing.JOptionPane.showMessageDialog;

public class modifyAppointmentController {

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
    @FXML
    public void appointmentSave() throws SQLException {
        String appointmentTitle = appointmentTitleField.getText();
        String appointmentDescription = appointmentDescField.getText();
        String appointmentLocation = appointmentLocField.getText();
        String appointmentType = appointmentTypeField.getText();
        int appointmentID = Integer.parseInt(appointmentIDField.getText());

        //TODO: conversions maybe. MAYBE use PreparedStatement ps.setTimestamp stmt.setTimestamp(1, t, Calendar.getInstance(TimeZone.getTimeZone("UTC")))
        //Start Date
        String appointmentStart = appointmentStartDate.getValue() + " " + appointmentStartTime.getValue() + ":00";
        //End Date
        String appointmentEnd = appointmentEndDate.getValue() + " " + appointmentEndTime.getValue() + ":00";

        String appointmentCreateDate = Instant.now().truncatedTo(ChronoUnit.SECONDS).toString().replaceAll("[TZ]"," ");
        String appointmentCreatedBy = login_screen.getUsername();
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
                appointmentCreateDate,
                appointmentCreatedBy,
                customerID,
                userID,
                contactID,
                appointmentID);
        if (login_screen.isEnglish()){
            showMessageDialog(null,"Appointment updated.");
        }
        else {
            showMessageDialog(null,"Rendez-vous mis à jour.");
        }

        Stage modifyAppointment = (Stage) btnCancel.getScene().getWindow();
        modifyAppointment.close();
    }

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
        LocalDate startDate = LocalDate.parse(startDateTime.substring(0,10), formatter);
        LocalDate endDate = LocalDate.parse(endDateTime.substring(0,10), formatter);

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

        if (login_screen.isEnglish()) {/*Do nothing*/}
        else {
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
