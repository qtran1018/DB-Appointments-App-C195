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
    public Button btnAppointments;
    public Label labelTables;
    public Label labelCurrentPlace;
    public Button btnLogout;
    public Button btnCustomerDelete;
    public Button btnCustomerModify;
    public Label labelNav;
    /**
     * Variable declarations.
     */
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

    /**
     * SQL statement methods.
     */
    public static int customerInsert(String customerName, String customerAddress, String customerPostal, String customerPhone, String createDate, String createdBy, String lastUpdate, String lastUpdatedBy, int divisionID) throws SQLException {
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
        return ps.executeUpdate();
        //int rowsAffected = ps.executeUpdate(); Redundant, converted to inline return.
        //return rowsAffected;
    }

    public static int customerUpdate(String customerName, String customerAddress, String customerPostal, String customerPhone, String lastUpdate, String lastUpdatedBy, int divisionID, int customerID) throws SQLException {
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
        return ps.executeUpdate();
    }

    public static int customerDelete(int customerID) throws SQLException {
        //WORKS
        CustomerQuery.customerDeleteAppointment(customerID);
        String sql = "DELETE FROM customers WHERE Customer_ID = ?";
        PreparedStatement ps = JDBC.connection.prepareStatement(sql);
        ps.setInt(1, customerID);
        return ps.executeUpdate();
    }

    public static int customerDeleteAppointment(int customerID) throws SQLException {
        //WORKS
        String sql = "DELETE FROM appointments WHERE Customer_ID = ?";
        PreparedStatement ps = JDBC.connection.prepareStatement(sql);
        ps.setInt(1,customerID);
        return ps.executeUpdate();
    }

    public static ResultSet select() throws SQLException {
        String sql = "SELECT * FROM customers";
        PreparedStatement ps = JDBC.connection.prepareStatement(sql);
        return ps.executeQuery();
        /*
        while(rs.next()){
            int contactID = rs.getInt("Contact_ID");
            String contactName = rs.getString("Contact_Name");
            String contactEmail = rs.getString("Email");
            System.out.print(contactID + " | ");
            System.out.print(contactName + " | ");
            System.out.print(contactEmail + "\n");
        }
         */
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

    /**
     * FXML action methods.
     */
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
        //TODO: see if logClick and loginClick exception being different is a problem. One throws IOException, other does Try-Catch
        int confirmBtn = JOptionPane.YES_NO_OPTION;
        int resultBtn = JOptionPane.showConfirmDialog(null, "Are you sure you want to log out?", "Warning", confirmBtn);

        if (resultBtn == JOptionPane.YES_OPTION) {
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
                showMessageDialog(null, "Select a row to modify.");
            }
        }
        catch (Exception e) {
            showMessageDialog(null, "Select a row to modify.");
        }

    }
    public void customerDeleteClick() {
        try {
            int confirmBtn = JOptionPane.YES_NO_OPTION;
            int resultBtn = JOptionPane.showConfirmDialog(null, "Are you sure you want to delete this customer?", "Warning", confirmBtn);

            if (resultBtn == JOptionPane.YES_OPTION) {
                selectedItem = customersTable.getSelectionModel().getSelectedItem();
                if (selectedItem != null) {
                    String selectedString = selectedItem.toString();
                    selectedString = selectedString.substring(1, selectedString.length() - 1);
                    selectedArr = selectedString.split(",");
                    //Lambda?
                    selectedArr = Arrays.stream(selectedArr).map(String::trim).toArray(String[]::new);
                    String cusID = selectedArr[0];
                    int customerID = Integer.parseInt(cusID);

                    CustomerQuery.customerDelete(customerID);
                    btnCustomers.fire();

                    //TODO future: actually check if it was deleted. Select on customers to see if customerID exists. Write an error message if it's still there.
                    showMessageDialog(null,"Customer deleted.");
                }
                else {
                    showMessageDialog(null, "Select a row to delete.");
                }
            }
        }
        catch (Exception e) {
            showMessageDialog(null, "Select a row to delete.");
        }
    }

    /**
         * Table setting code.
         */
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
                //Let me see how many columns got pulled in.
                //System.out.println("Column [" + i + "] ");
            }
            while(rs.next()) {
                ObservableList<String> row = FXCollections.observableArrayList();
                for (int i=1; i <= rs.getMetaData().getColumnCount();i++){
                    row.add(rs.getString(i));
                }
                //TODO: remove these comments, CustomerQuery
                //Let me see the row data added;
                //System.out.println("Row [1] added " +row );
                data.add(row);
            }
            customersTable.setItems(data);
        }
        catch (Exception e){
            e.printStackTrace();
            System.out.println("Error getting data in AppointmentQuery");
        }
    }

    public void refreshTable(){
        System.out.println("before refresh");
        getData();
        System.out.println("after refresh");
    }
    public void initialize() {
        getData();
    }
}
