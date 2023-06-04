package termProject;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static javax.swing.JOptionPane.showMessageDialog;

public class addAppointmentController {

    public Button saveAppointment;
    public TextField appointmentTitleField;
    public TextField appointmentDescField;
    public TextField appointmentLocField;
    public TextField appointmentCustomerID;
    public TextField appointmentIDField;
    public TextField appointmentTypeField;
    public DatePicker appointmentStartDate;
    public ComboBox appointmentStartTime;
    public ComboBox appointmentEndTime;
    public DatePicker appointmentEndDate;
    public TextField appointmentUserID;
    public TextField appointmentContactID;
    @FXML
    private Button btnCancel;

    //<editor-fold desc="ComboBox lists>
    final ObservableList<String> times = FXCollections.observableArrayList("00:00", "01:00", "02:00","03:00","04:00","05:00","06:00","07:00","08:00","09:00","10:00","11:00","12:00","13:00","14:00","15:00","16:00","17:00","18:00","19:00","20:00","21:00","21:00","22:00","23:00");
    //</editor-fold
    @FXML
    public void appointmentCancel() {
        Stage appointmentAddStage = (Stage) btnCancel.getScene().getWindow();
        //Closes current window
        appointmentAddStage.close();
    }
    @FXML
    public void appointmentSave() throws SQLException, IOException {
        String appointmentTitle = appointmentTitleField.getText();
        String appointmentDescription = appointmentDescField.getText();
        String appointmentLocation = appointmentLocField.getText();
        String appointmentType = appointmentTypeField.getText();

        //End Date
        String appointmentCreateDate = Instant.now().truncatedTo(ChronoUnit.SECONDS).toString().replaceAll("[TZ]"," ");
        String appointmentCreatedBy = login_screen.getUsername();
        int customerID = Integer.parseInt(appointmentCustomerID.getText());
        int userID = Integer.parseInt(appointmentUserID.getText());
        int contactID = Integer.parseInt(appointmentContactID.getText());

        //Doubles up on create-date and update-date, created-by and updated-by, since they will be the same.
        //AppointmentQuery.AppointmentInsert(AppointmentName, AppointmentAddress, AppointmentPostal, AppointmentPhone, AppointmentCreateDate, AppointmentCreatedBy, AppointmentCreateDate, AppointmentCreatedBy, AppointmentDivisionID);
        showMessageDialog(null,"Appointment added.");

        //TODO: somehow refresh the table.

        Stage addAppointment = (Stage) btnCancel.getScene().getWindow();
        addAppointment.close();
    }
    @FXML
    void initialize() {
        appointmentIDField.setDisable(true);
        appointmentStartTime.getItems().clear();
        appointmentEndTime.getItems().clear();
        appointmentStartTime.setItems(times);
        appointmentEndTime.setItems(times);
    }
}
