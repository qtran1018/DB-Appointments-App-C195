package termProject;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import java.sql.SQLException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import static javax.swing.JOptionPane.showMessageDialog;

public class modifyCustomerController {

    /**
     * Variables for FXML controllers and tableview selections.
     */
    //<editor-fold desc="Variables folded">
    public Button saveCustomer;
    public Label labelName;
    public Label labelCountry;
    public Label labelAddress;
    public Label labelPostal;
    public Label labelState;
    public Label labelPhone;
    public Label labelPlace;
    public Label labelCustomerID;
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
    public String[] selectedArr;
    //</editor-fold

    //<editor-fold desc="Country State lists folded">
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

    /**
     * Closes the window without saving any changes.
     */
    @FXML
    public void customerCancel() {
        Stage customerAddStage = (Stage) btnCancel.getScene().getWindow();
        //Closes current window
        customerAddStage.close();
    }

    /**
     * This method saves the data in the fields into a record for the DB.
     * First it checks for failure; all fields are checked for empty values.
     * If any are empty, displays a message noting all fields are required.
     * If all is good, assigns the info of each field to a variable, and then pass those variables to the customer INSERT method.
     * Closes the scene window on completion.
     */
    @FXML
    public void customerSave() throws SQLException {
        if (customerNameField.getText().isEmpty() ||
            customerAddressField.getText().isEmpty() ||
            customerPhoneField.getText().isEmpty() ||
            customerCountryField.getValue().isEmpty() ||
            customerStateField.getValue().isEmpty() ||
            customerPostalField.getText().isEmpty())
            {
                if (login_screen.isEnglish()){
                    showMessageDialog(null,"Error: All fields are required.");
                }
                else {
                    showMessageDialog(null,"Erreur: Tous les champs sont obligatoires.");
                }
            }
        else {
            int customerID = Integer.parseInt(customerIDField.getText());
            String customerName = customerNameField.getText();
            String customerAddress = customerAddressField.getText();
            String customerPostal = customerPostalField.getText();
            String customerState = customerStateField.getValue();
            int customerDivisionID = CustomerQuery.selectDivisionID(customerState);
            String customerPhone = customerPhoneField.getText();
            //Formats in UTC time like this : 2023-05-31 06:52:35
            String customerLastUpdate = Instant.now().truncatedTo(ChronoUnit.SECONDS).toString().replaceAll("[TZ]", " ");
            String customerLastUpdatedBy = login_screen.getUsername();

            CustomerQuery.customerUpdate(customerName, customerAddress, customerPostal, customerPhone, customerLastUpdate, customerLastUpdatedBy, customerDivisionID, customerID);
            if (login_screen.isEnglish()) {
                showMessageDialog(null, "Customer information updated.");
            } else {
                showMessageDialog(null, "Informations client mises à jour.");
            }

            Stage closeModify = (Stage) btnCancel.getScene().getWindow();
            closeModify.close();
        }
    }

    /**
     * Function that allows data from the tableview in CustomerQuery to be accessed by the modify-class.
     * Takes in an array and then assigns variables the values of its corresponding location in the array.
     * @param selectedArr the Array that is taken from the tableview, which contains the data of the customer being modified.
     */
    public void initCustomerData(String[] selectedArr) throws SQLException {
        this.selectedArr = selectedArr;

        String customerID = selectedArr[0];
        String customerName = selectedArr[1];
        String customerAddress = selectedArr[2];
        String customerPostal = selectedArr[3];
        String customerState = selectedArr[9];
        String customerPhone = selectedArr[4];

        //State goes: String Array --> String (number) --> Int --> query String to Division name.
        //Example: ["1"] --> "1" --> 1 --> "Alabama"
        int divisionID = Integer.parseInt(customerState);
        customerState = CustomerQuery.selectDivision(divisionID);
        String customerCountry = CustomerQuery.selectCountry(customerState);

        customerIDField.setText(customerID);
        customerNameField.setText(customerName);
        customerAddressField.setText(customerAddress);
        customerPostalField.setText(customerPostal);
        customerStateField.setValue(customerState);
        customerPhoneField.setText(customerPhone);
        customerCountryField.setValue(customerCountry);

    }
    /**
     * Set the combobox options to the respective observable lists.
     * Changes the list of STATEs dependent on the Country selection.
     * Lambda expression: by using a lambda expression here inside the initialize method, we can simplify the reading of the basic switch-case statements, linked to a listener method on our combo box's selection.
     */
    @FXML
    void initialize() {
        customerIDField.setDisable(true);
        customerCountryField.getItems().clear();
        customerCountryField.setItems(countries);
        //Lambda?
        customerCountryField.getSelectionModel().selectedItemProperty().addListener((observableValue, s, t1) -> {
            switch (t1){
                case "U.S" -> customerStateField.setItems(usStates);
                case "UK" -> customerStateField.setItems(ukStates);
                case "Canada" -> customerStateField.setItems(caStates);
                default -> throw new IllegalStateException("EN: Invalid value.\n FR:Valeur invalide.");
            }
        });
        if (!login_screen.isEnglish()) {
            labelCustomerID.setText("N ° de client");
            labelName.setText("Nom et prénom");
            labelCountry.setText("Pays");
            labelAddress.setText("Adresse");
            labelPostal.setText("Code Postal");
            labelState.setText("Province");
            labelPhone.setText("Téléphone");
            labelPlace.setText("Client");
            btnCancel.setText("Annuler");
            saveCustomer.setText("Sauvegarder");
        }
    }
}
