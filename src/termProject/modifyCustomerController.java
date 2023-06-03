package termProject;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import static javax.swing.JOptionPane.showMessageDialog;

public class modifyCustomerController {

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
    public String[] selectedArr;
    private String customerID;
    private String customerName;
    private String customerAddress;;
    private String customerPostal;
    private String customerCountry;
    private String customerState;
    private String customerPhone;

    //<editor-fold desc="Country State lists>
    final ObservableList<String> countries = FXCollections.observableArrayList("U.S", "UK", "Canada");
    final ObservableList<String> usStates = FXCollections.observableArrayList(
            "Alabama",
            "Alaska",
            "Arizona",
            "Arkansas",
            "California",
            "Colorado",
            "Connecticut",
            "Delaware",
            "Florida",
            "Georgia",
            "Hawaii",
            "Idaho",
            "Illinois",
            "Indiana",
            "Iowa",
            "Kansas",
            "Kentucky",
            "Louisiana",
            "Maine",
            "Maryland",
            "Massachusetts",
            "Michigan",
            "Minnesota",
            "Mississippi",
            "Missouri",
            "Montana",
            "Nebraska",
            "Nevada",
            "New Hampshire",
            "New Jersey",
            "New Mexico",
            "New York",
            "North Carolina",
            "North Dakota",
            "Ohio",
            "Oklahoma",
            "Oregon",
            "Pennsylvania",
            "Rhode Island",
            "South Carolina",
            "South Dakota",
            "Tennessee",
            "Texas",
            "Utah",
            "Vermont",
            "Virginia",
            "Washington",
            "West Virginia",
            "Wisconsin",
            "Wyoming" );
    final ObservableList<String> ukStates = FXCollections.observableArrayList("England", "Scotland", "Wales", "Northern Ireland");
    final ObservableList<String> caStates = FXCollections.observableArrayList("Northwestern Territories","Alberta","British Columbia","Manitoba","New Brunswick","Nova Scotia","Prince Edward Island","Ontario","Québec","Saskatchewan","Nunavut","Yukon","Newfoundland and Labrador");
    //</editor-fold

    @FXML
    public void customerCancel() {
        Stage customerAddStage = (Stage) btnCancel.getScene().getWindow();
        //Closes current window
        customerAddStage.close();
    }

    @FXML
    public void customerSave() throws SQLException, IOException {
        int customerID = Integer.parseInt(customerIDField.getText());
        String customerName = customerNameField.getText();
        String customerAddress = customerAddressField.getText();
        String customerPostal = customerPostalField.getText();
        String customerState = customerStateField.getValue();
        int customerDivisionID = CustomerQuery.selectDivisionID(customerState);
        String customerPhone = customerPhoneField.getText();
        //Formats in UTC time like this : 2023-05-31 06:52:35
        String customerLastUpdate = Instant.now().truncatedTo(ChronoUnit.SECONDS).toString().replaceAll("[TZ]"," ");
        String customerLastUpdatedBy = login_screen.getUsername();

        CustomerQuery.customerUpdate(customerName,customerAddress, customerPostal, customerPhone, customerLastUpdate, customerLastUpdatedBy, customerDivisionID, customerID);
        showMessageDialog(null,"Customer information updated.");

        /**
         * Closes the modify window.
         */
        Stage closeModify = (Stage) btnCancel.getScene().getWindow();
        closeModify.close();
    }

    public void initCustomerData(String[] selectedArr) throws SQLException {
        this.selectedArr = selectedArr;

        customerID = selectedArr[0];
        customerName = selectedArr[1];
        customerAddress = selectedArr[2];
        customerPostal = selectedArr[3];
        customerState = selectedArr[9];
        customerPhone = selectedArr[4];

        //State goes: String Array --> String (number) --> Int --> query String to Division name.
        //Example: ["1"] --> "1" --> 1 --> "Alabama"
        int divisionID = Integer.parseInt(customerState);
        customerState = CustomerQuery.selectDivision(divisionID);
        customerCountry = CustomerQuery.selectCountry(customerState);

        customerIDField.setText(customerID);
        customerNameField.setText(customerName);
        customerAddressField.setText(customerAddress);
        customerPostalField.setText(customerPostal);
        customerStateField.setValue(customerState);
        customerPhoneField.setText(customerPhone);
        customerCountryField.setValue(customerCountry);

    }
    @FXML
    void initialize() {
        customerIDField.setDisable(true);
        customerCountryField.getItems().clear();
        customerCountryField.setItems(countries);
        customerCountryField.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observableValue, String s, String t1) {
                switch (t1.toString()){
                    case "U.S":
                        customerStateField.setItems(usStates);
                        break;
                    case "UK":
                        customerStateField.setItems(ukStates);
                        break;
                    case "Canada":
                        customerStateField.setItems(caStates);
                }
            }
        });

    }
}
