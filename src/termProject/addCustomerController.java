package termProject;

import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.beans.value.ChangeListener;

import java.io.IOException;
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

        //Testing bits
        //System.out.println(customerDivisionID);
        //System.out.println(customerCreateDate);

        //Doubles up on create-date and update-date, created-by and updated-by, since they will be the same.
        CustomerQuery.customerInsert(customerName, customerAddress, customerPostal, customerPhone, customerCreateDate, customerCreatedBy, customerCreateDate, customerCreatedBy, customerDivisionID);
        //TODO: somehow refresh the table.

        FXMLLoader loader = new FXMLLoader(this.getClass().getResource("customers_screen.fxml"));
        loader.load();
        CustomerQuery controller = loader.getController();
        //TODO: Fix, doesn't refresh from the save, but does if you click Customer on left.
        controller.refreshTable();

        Stage addCustomer = (Stage) btnCancel.getScene().getWindow();
        addCustomer.close();
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
