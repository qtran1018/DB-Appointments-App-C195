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
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Stage;
import javafx.util.Callback;
import javax.swing.*;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class CustomerQuery {
    @FXML
    private Button btnHome;
    @FXML
    private TableView customersTable;
    private ObservableList<ObservableList> data;

    /**
     * SQL statement methods.
     */
    public static int customerInsert(String customerName, String customerAddress, String customerPostal, String customerPhone, int divisionID) throws SQLException {
        String sql = "INSERT INTO customers (Customer_Name, Address, Postal_Code, Phone, Division_ID) VALUES(?, ?, ?, ?, ?)";
        PreparedStatement ps = JDBC.connection.prepareStatement(sql);
        ps.setString(1,customerName);
        ps.setString(2,customerAddress);
        ps.setString(3,customerPostal);
        ps.setString(4,customerPhone);
        ps.setInt(5,divisionID);
        return ps.executeUpdate();
        //int rowsAffected = ps.executeUpdate(); Redundant, converted to inline return.
        //return rowsAffected;
    }

    public static int customerUpdate(int customerID, String customerName, String customerAddress, String customerPostal, String customerPhone) throws SQLException {
        String sql = "UPDATE customers SET Customer_Name = ?, Address = ?, Postal_Code = ?, Phone = ? WHERE Customer_ID = ?";
        PreparedStatement ps = JDBC.connection.prepareStatement(sql);
        ps.setString(1, customerName);
        ps.setString(2, customerAddress);
        ps.setString(3, customerPostal);
        ps.setString(4, customerPhone);
        ps.setInt(5, customerID);
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
        ResultSet rs = ps.executeQuery();
        return rs;
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

    /**
     * Table setting code.
     */
    public void getData() {
        try {
            data = FXCollections.observableArrayList();
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
    public void initialize() {
        getData();
    }
}
