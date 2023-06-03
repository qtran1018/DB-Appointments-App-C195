package termProject;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static javax.swing.JOptionPane.showMessageDialog;

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
    private Button btnCancel;

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
        String customerName = customerNameField.getText();
        String customerAddress = customerAddressField.getText();
        String customerPostal = customerPostalField.getText();
        //TODO: set it when I make the combo boxes.
        //String customerCountry = customerCountryField.getValue();
        String customerState = customerStateField.getValue();
        int customerDivisionID = CustomerQuery.selectDivisionID(customerState);
        String customerPhone = customerPhoneField.getText();
        //Formats in UTC time like this : 2023-05-31 06:52:35
        String customerCreateDate = Instant.now().truncatedTo(ChronoUnit.SECONDS).toString().replaceAll("[TZ]"," ");
        //String customerLastUpdate = customerCreateDate;
        String customerCreatedBy = login_screen.getUsername();
        //String customerLastUpdatedBy = login_screen.getUsername();

        //Doubles up on create-date and update-date, created-by and updated-by, since they will be the same.
        CustomerQuery.customerInsert(customerName, customerAddress, customerPostal, customerPhone, customerCreateDate, customerCreatedBy, customerCreateDate, customerCreatedBy, customerDivisionID);
        showMessageDialog(null,"Customer added.");

        //TODO: somehow refresh the table.
        FXMLLoader customerLoader = new FXMLLoader(getClass().getResource("customers_screen.fxml"));
        customerLoader.load();
        CustomerQuery controller = customerLoader.getController();
        controller.refreshTable();

        Stage addCustomer = (Stage) btnCancel.getScene().getWindow();
        addCustomer.close();
    }
    @FXML
    void initialize() {
        customerIDField.setDisable(true);
        customerCountryField.getItems().clear();
        customerCountryField.setItems(countries);
        //Lambda?
        customerCountryField.getSelectionModel().selectedItemProperty().addListener((observableValue, s, t1) -> {
            //Learned about Enhanced Switch. Arrow-case, no need for break.
            switch (t1){
                case "U.S" -> customerStateField.setItems(usStates);
                case "UK" -> customerStateField.setItems(ukStates);
                case "Canada" -> customerStateField.setItems(caStates);
                default -> throw new IllegalStateException("Invalid value.");
            }
        });

    }
}
