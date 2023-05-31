package termProject;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.sql.SQLException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

public class addCustomerController {

    @FXML
    private TextField customerIDField;
    @FXML
    private TextField customerNameField;
    @FXML
    private TextField customerAddressField;
    @FXML
    private TextField customerPostalField;
    @FXML
    private ComboBox<String> customerCountryField;
    @FXML
    private ComboBox<String> customerStateField;
    @FXML
    private TextField customerPhoneField;
    @FXML
    private Button saveCustomer;
    @FXML
    private Button btnCancel;

    @FXML
    public void customerCancel() {
        Stage customerAddStage = (Stage) btnCancel.getScene().getWindow();
        //Closes current window
        customerAddStage.close();
    }
    @FXML
    public void customerSave() throws SQLException {
        String customerName = customerNameField.getText();
        String customerAddress = customerAddressField.getText();
        String customerPostal = customerPostalField.getText();
        //TODO: set it when I make the combo boxes.
        String customerCountry = "U.S";//customerCountryField.getValue();
        String customerState = "Alabama";//customerStateField.getValue();
        int customerDivisionID = CustomerQuery.selectDivisionID(customerState);
        String customerPhone = customerPhoneField.getText();
        //Formats in UTC time like this : 2023-05-31 06:52:35
        String customerCreateDate = Instant.now().truncatedTo(ChronoUnit.SECONDS).toString().replaceAll("[TZ]"," ");
        //String customerLastUpdate = customerCreateDate;
        String customerCreatedBy = login_screen.getUsername();
        //String customerLastUpdatedBy = login_screen.getUsername();

        System.out.println(customerCountry + " " + customerState);
        System.out.println(customerDivisionID);
        System.out.println(customerCreateDate);

        //Doubles up on create-date and update-date, created-by and updated-by, since they will be the same.
        CustomerQuery.customerInsert(customerName, customerAddress, customerPostal, customerPhone, customerCreateDate, customerCreatedBy, customerCreateDate, customerCreatedBy, customerDivisionID);
        //TODO: somehow refresh the table.
        //CustomerQuery.refreshTable();
    }
    @FXML
    void initialize() {
        customerIDField.setDisable(true);
    }
}
