package termProject;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Stage;
import javafx.util.Callback;
import javax.swing.*;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import static javax.swing.JOptionPane.showMessageDialog;

public class CustomerQuery {

    //<editor-fold desc="Variables folded">
    public Button btnAppointments;
    public Label labelTables;
    public Label labelCurrentPlace;
    public Button btnLogout;
    public Button btnCustomerDelete;
    public Button btnCustomerModify;
    public Label labelNav;
    public Button btnRefresh;
    @FXML
    private Button btnHome;
    @FXML
    //Changed from nothing to <ObservableList>
    private TableView<ObservableList> customersTable;
    @FXML
    public Button btnCustomerAdd;
    @FXML
    public Button btnCustomers;
    Object selectedItem;
    String[] selectedArr;
    //</editor-fold
    //-------------------------------------------------------------------------------
    public static void customerInsert(String customerName, String customerAddress, String customerPostal, String customerPhone, String createDate, String createdBy, String lastUpdate, String lastUpdatedBy, int divisionID) throws SQLException {
        String sql = "INSERT INTO customers (Customer_Name, Address, Postal_Code, Phone, Create_Date, Created_By, Last_Update, Last_Updated_By, Division_ID) VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?)";
        PreparedStatement ps = JDBC.connection.prepareStatement(sql);
        ps.setString(1,customerName);
        ps.setString(2,customerAddress);
        ps.setString(3,customerPostal);
        ps.setString(4,customerPhone);
        ps.setString(5,createDate);
        ps.setString(6, createdBy);
        ps.setString(7, lastUpdate);
        ps.setString(8,lastUpdatedBy);
        ps.setInt(9, divisionID);
        ps.executeUpdate();
        //int rowsAffected = ps.executeUpdate(); Redundant, converted to inline return.
        //return rowsAffected;
    }

    public static void customerUpdate(String customerName, String customerAddress, String customerPostal, String customerPhone, String lastUpdate, String lastUpdatedBy, int divisionID, int customerID) throws SQLException {
        String sql = "UPDATE customers SET Customer_Name = ?, Address = ?, Postal_Code = ?, Phone = ?, Last_Update = ?, Last_Updated_By = ?, Division_ID = ? WHERE Customer_ID = ?";
        PreparedStatement ps = JDBC.connection.prepareStatement(sql);
        ps.setString(1, customerName);
        ps.setString(2, customerAddress);
        ps.setString(3, customerPostal);
        ps.setString(4, customerPhone);
        ps.setString(5, lastUpdate);
        ps.setString(6, lastUpdatedBy);
        ps.setInt(7,divisionID);
        ps.setInt(8,customerID);
        ps.executeUpdate();
    }

    public static void customerDelete(int customerID) throws SQLException {
        //WORKS
        CustomerQuery.customerDeleteAppointment(customerID);
        String sql = "DELETE FROM customers WHERE Customer_ID = ?";
        PreparedStatement ps = JDBC.connection.prepareStatement(sql);
        ps.setInt(1, customerID);
        ps.executeUpdate();
    }

    public static void customerDeleteAppointment(int customerID) throws SQLException {
        //WORKS
        String sql = "DELETE FROM appointments WHERE Customer_ID = ?";
        PreparedStatement ps = JDBC.connection.prepareStatement(sql);
        ps.setInt(1,customerID);
        ps.executeUpdate();
    }

    public static ResultSet select() throws SQLException {
        String sql = "SELECT * FROM customers";
        PreparedStatement ps = JDBC.connection.prepareStatement(sql);
        return ps.executeQuery();
    }

    public static int selectDivisionID(String state) throws SQLException {
        String sql = "SELECT Division_ID FROM first_level_divisions WHERE Division = ?";
        PreparedStatement ps = JDBC.connection.prepareStatement(sql);
        ps.setString(1,state);
        ResultSet rs = ps.executeQuery();
        int divisionID = -1;
        while(rs.next()){
            divisionID = rs.getInt("Division_ID");
        }
        return divisionID;
    }
    public static String selectDivision(int division_id) throws SQLException {
        String sql = "SELECT Division FROM first_level_divisions WHERE Division_ID = ?";
        PreparedStatement ps = JDBC.connection.prepareStatement(sql);
        ps.setInt(1,division_id);
        ResultSet rs = ps.executeQuery();
        String division = "Division not found.";
        while(rs.next()){
            division = rs.getString("Division");
        }
        return division;
    }

    public static String selectCountry(String state) throws SQLException {
        String sql = "SELECT Country FROM first_level_divisions as f INNER JOIN countries as c ON f.Country_ID = c.Country_ID WHERE Division = ?";
        PreparedStatement ps = JDBC.connection.prepareStatement(sql);
        ps.setString(1,state);
        ResultSet rs = ps.executeQuery();
        String country = "State not found.";
        while(rs.next()){
            country = rs.getString("Country");
        }
        return country;
    }
    //-------------------------------------------------------------------------------
    public void loadScreen(String screenName) throws IOException {
        FXMLLoader screenLoader = new FXMLLoader(login_screen.class.getResource(screenName));
        Parent screenRoot = screenLoader.load();
        Stage screenStage = new Stage();
        screenStage.setScene(new Scene(screenRoot));
        screenStage.setTitle("Term Project Application");
        screenStage.show();

        Stage closeStage = (Stage) btnHome.getScene().getWindow();
        closeStage.close();
    }
    public void logoutClick() throws IOException {
        int confirmBtn = JOptionPane.YES_NO_OPTION;
        int resultBtn;
        if (login_screen.isEnglish()){
            resultBtn = JOptionPane.showConfirmDialog(null, "Are you sure you want to logout?", "Warning", confirmBtn);
        }
        else {
            resultBtn = JOptionPane.showConfirmDialog(null, "Êtes-vous sûr de vouloir vous déconnecter?", "Avertissement", confirmBtn);
        }
        if (resultBtn == JOptionPane.YES_OPTION) {
            login_screen.unsetUsername();
            loadScreen("login_screen.fxml");
        }
    }
    public void homeClick() throws IOException {
        loadScreen("home_screen.fxml");
    }
    public void appointmentsClick() throws IOException {
        loadScreen("appointments_screen.fxml");
    }
    public void customerAddClick() throws IOException {
        FXMLLoader partLoader = new FXMLLoader(getClass().getResource("customer_add.fxml"));
        Parent partRoot = partLoader.load();
        Stage partStage = new Stage();
        partStage.setScene(new Scene(partRoot));
        partStage.show();
    }
    public void customerModifyClick() {
        try {
            selectedItem = customersTable.getSelectionModel().getSelectedItem();
            if (selectedItem != null){
                String selectedString = selectedItem.toString();
                selectedString = selectedString.substring(1,selectedString.length()-1);
                selectedArr = selectedString.split(",");
                //Lambda?
                selectedArr = Arrays.stream(selectedArr).map(String::trim).toArray(String[]::new);

                FXMLLoader customerLoader = new FXMLLoader(getClass().getResource("customer_modify.fxml"));
                Parent customerRoot = customerLoader.load();
                Stage customerModifyStage = new Stage();
                customerModifyStage.setScene(new Scene(customerRoot));

                modifyCustomerController modControl = customerLoader.getController();
                modControl.initCustomerData(selectedArr);

                customerModifyStage.show();
            }
            else {
                if (login_screen.isEnglish()){
                    showMessageDialog(null, "Select a row to modify.");
                }
                else {
                    showMessageDialog(null, "Sélectionnez une ligne à modifier.");
                }
            }
        }
        catch (Exception e) {
            if (login_screen.isEnglish()){
                showMessageDialog(null, "Select a row to modify.");
            }
            else {
                showMessageDialog(null, "Sélectionnez une ligne à modifier.");
            }
        }
    }
    public void customerDeleteClick() {
        try {
            selectedItem = customersTable.getSelectionModel().getSelectedItem();
            if (selectedItem != null) {
                int confirmBtn = JOptionPane.YES_NO_OPTION;
                int resultBtn;
                if (login_screen.isEnglish()){
                    resultBtn = JOptionPane.showConfirmDialog(null, "Are you sure you want to delete this customer?", "Warning", confirmBtn);
                }
                else {
                    resultBtn = JOptionPane.showConfirmDialog(null, "Voulez-vous vraiment supprimer ce client?", "Avertissement", confirmBtn);
                }

                if (resultBtn == JOptionPane.YES_OPTION) {
                    String selectedString = selectedItem.toString();
                    selectedString = selectedString.substring(1, selectedString.length() - 1);
                    selectedArr = selectedString.split(",");
                    //Lambda?
                    selectedArr = Arrays.stream(selectedArr).map(String::trim).toArray(String[]::new);
                    String cusID = selectedArr[0];
                    int customerID = Integer.parseInt(cusID);

                    CustomerQuery.customerDelete(customerID);
                    refreshTable();

                    //TODO future: actually check if it was deleted. Select on customers to see if appointmentID exists. Write an error message if it's still there.
                    if (login_screen.isEnglish()){
                        showMessageDialog(null, "Customer deleted.");
                    }
                    else {
                        showMessageDialog(null, "Client supprimé.");
                    }
                }
            }
            else {
                if (login_screen.isEnglish()){
                    showMessageDialog(null, "Select a row to modify.");
                }
                else {
                    showMessageDialog(null, "Sélectionnez une ligne à modifier.");
                }
            }
        }
        catch (SQLException ex) {
            if (login_screen.isEnglish()){
                showMessageDialog(null, "Select a row to delete.");
            }
            else {
                showMessageDialog(null, "Sélectionnez une ligne à supprimer.");
            }
            throw new RuntimeException(ex);
        }
    }

    //-------------------------------------------------------------------------------
    public void getData() {
        try {
            ObservableList<ObservableList> data = FXCollections.observableArrayList();
            data.clear();
            customersTable.getItems().clear();
            customersTable.getColumns().clear();
            ResultSet rs = CustomerQuery.select();

            for (int i = 0; i < rs.getMetaData().getColumnCount(); i++){
                final int j = i;
                TableColumn tblCol = new TableColumn(rs.getMetaData().getColumnName(i+1));
                tblCol.setCellValueFactory((Callback<TableColumn.CellDataFeatures<ObservableList, String>, ObservableValue<String>>) param -> new SimpleStringProperty(param.getValue().get(j).toString()));
                customersTable.getColumns().addAll(tblCol);
            }
            while(rs.next()) {
                ObservableList<String> row = FXCollections.observableArrayList();
                for (int i=1; i <= rs.getMetaData().getColumnCount();i++){
                    row.add(rs.getString(i));
                }
                data.add(row);
            }
            customersTable.setItems(data);
        }
        catch (Exception e){
            e.printStackTrace();
            if (login_screen.isEnglish()){
                showMessageDialog(null, "Error getting data.");
            }
            else {
                showMessageDialog(null, "Erreur lors de l'obtention des données.");
            }
        }
    }

    public void refreshTable(){
        getData();
    }
    //-------------------------------------------------------------------------------
    public void initialize() {
        getData();
        if (!login_screen.isEnglish()){
            //TODO: labelnav is too long in french, fix.
            labelNav.setText("La navigation");
            btnHome.setText("Maison");
            labelTables.setText("Les tables");
            btnCustomers.setText("Clients");
            btnAppointments.setText("Rendez-vous");
            btnLogout.setText("Se déconnecter");
            labelCurrentPlace.setText("Clients");
            btnRefresh.setText("Rafraîchir");
            btnCustomerAdd.setText("Ajouter");
            btnCustomerModify.setText("Modifier");
            btnCustomerDelete.setText("Supprimer");
        }
    }
}
