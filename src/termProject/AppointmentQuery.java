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
import javafx.scene.control.TableColumn.CellDataFeatures;
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

public class AppointmentQuery {
    public Label labelNav;
    public Label labelTables;
    public Button btnCustomers;
    public Button btnAppointments;
    public Button btnLogout;
    public Button btnAppointmentAdd;
    public Button btnAppointmentsModify;
    public Button btnAppointmentDelete;
    public Button btnRefresh;
    /**
     * Variable declarations.
     */
    @FXML
    private Button btnHome;
    @FXML
    private TableView appointmentsTable;
    String[] selectedArr;
    Object selectedItem;

    /**
     * SQL statement methods.
     */
    public static int appointmentInsert(String title, String description, String location, String type, String start, String end, String createDate, String createdBy, String lastUpdate, String lastUpdatedBy, int customerID, int userID, int contactID) throws SQLException {
        String sql = "INSERT INTO appointments (Title, Description, Location, Type, Start, End, Create_Date, Created_By, Last_Update, Last_Updated_By, Customer_ID, User_ID, Contact_ID) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        PreparedStatement ps = JDBC.connection.prepareStatement(sql);
        ps.setString(1, title);
        ps.setString(2, description);
        ps.setString(3, location);
        ps.setString(4, type);
        ps.setString(5, start);
        ps.setString(6, end);
        ps.setString(7, createDate);
        ps.setString(8, createdBy);
        ps.setString(9, lastUpdate);
        ps.setString(10, lastUpdatedBy);
        ps.setInt(11, customerID);
        ps.setInt(12, userID);
        ps.setInt(13, contactID);
        return ps.executeUpdate();
    }

    public static int appointmentUpdate(String title, String description, String location, String type, String start, String end, String lastUpdate, String lastUpdatedBy, int customerID, int userID, int contactID, int appointmentID) throws SQLException {
        String sql = "UPDATE appointments SET Title = ?, Description = ?, Location = ?, Type = ?, Start = ?, End = ?, Last_Update = ?, Last_Updated_By = ?, Customer_ID = ?, User_ID = ?, Contact_ID = ? WHERE Appointment_ID = ?";
        PreparedStatement ps = JDBC.connection.prepareStatement(sql);
        ps.setString(1, title);
        ps.setString(2, description);
        ps.setString(3, location);
        ps.setString(4, type);
        ps.setString(5, start);
        ps.setString(6, end);
        ps.setString(7, lastUpdate);
        ps.setString(8, lastUpdatedBy);
        ps.setInt(9, customerID);
        ps.setInt(10, userID);
        ps.setInt(11, contactID);
        ps.setInt(12, appointmentID);
        return ps.executeUpdate();
    }

    public static int appointmentDelete(int appointmentID) throws SQLException {
        String sql = "DELETE FROM appointments WHERE Appointment_ID = ?";
        PreparedStatement ps = JDBC.connection.prepareStatement(sql);
        ps.setInt(1, appointmentID);
        return ps.executeUpdate();
    }

    public static ResultSet select() throws SQLException {
        String sql = "SELECT * FROM appointments";
        PreparedStatement ps = JDBC.connection.prepareStatement(sql);
        return ps.executeQuery();
    }
    public static int selectContactID(String contactName) throws SQLException {
        String sql = "SELECT Contact_ID FROM contacts WHERE Contact_Name = ?";
        PreparedStatement ps = JDBC.connection.prepareStatement(sql);
        ps.setString(1,contactName);
        ResultSet rs = ps.executeQuery();
        int contactID = -1;
        while(rs.next()){
            contactID = rs.getInt("Contact_ID");
        }
        return contactID;
    }
    public static String selectContactName (int contactID) throws SQLException {
        String sql = "SELECT Contact_Name FROM contacts WHERE Contact_ID = ?";
        PreparedStatement ps = JDBC.connection.prepareStatement(sql);
        ps.setInt(1,contactID);
        ResultSet rs = ps.executeQuery();
        String contactName = "Contact name not found";
        while(rs.next()){
            contactName = rs.getString("Contact_Name");
        }
        return contactName;
    }

    public static int selectUser() throws SQLException {
        String sql = "SELECT User_ID FROM users User_ID WHERE User_Name = ?";
        PreparedStatement ps = JDBC.connection.prepareStatement(sql);
        ps.setString(1, login_screen.getUsername());
        ResultSet rs = ps.executeQuery();
        //-1 not a problem because user must be logged in, so it WILL find a user_id unless trickery is involved, such as removing a user while user is already logged in.
        int userID = -1;
        while(rs.next()){
            userID = rs.getInt("User_ID");
        }
        return userID;
    }

    public static ResultSet getContactList() throws SQLException {
        String sql = "SELECT Contact_Name FROM contacts";
        PreparedStatement ps = JDBC.connection.prepareStatement(sql);
        return ps.executeQuery();
    }

    /**
     * FXML action methods.
     */
    public void appointmentAddClick() throws IOException {
        FXMLLoader partLoader = new FXMLLoader(getClass().getResource("appointment_add.fxml"));
        Parent partRoot = partLoader.load();
        Stage partStage = new Stage();
        partStage.setScene(new Scene(partRoot));
        partStage.show();
    }

    public void appointmentModifyClick() {
        try {
        selectedItem = appointmentsTable.getSelectionModel().getSelectedItem();
        System.out.println(selectedItem.toString());
        if (selectedItem != null){
            String selectedString = selectedItem.toString();
            selectedString = selectedString.substring(1,selectedString.length()-1);
            selectedArr = selectedString.split(",");
            //Lambda?
            selectedArr = Arrays.stream(selectedArr).map(String::trim).toArray(String[]::new);

            FXMLLoader appointmentLoader = new FXMLLoader(getClass().getResource("appointment_modify.fxml"));
            Parent appointmentRoot = appointmentLoader.load();
            Stage appointmentModifyStage = new Stage();
            appointmentModifyStage.setScene(new Scene(appointmentRoot));

            modifyAppointmentController modControl = appointmentLoader.getController();
            modControl.initAppointmentData(selectedArr);
            appointmentModifyStage.show();

        }
        else {
            showMessageDialog(null, "Select a row to modify.");
        }
        }
        catch (Exception e) {
        showMessageDialog(null, "Select a row to modify.");
        }
    }
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
    public void customersClick() throws IOException {
        loadScreen("customers_screen.fxml");
    }
    public void appointmentDeleteClick() {
        try {
            selectedItem = appointmentsTable.getSelectionModel().getSelectedItem();
            if (selectedItem != null) {
                int confirmBtn = JOptionPane.YES_NO_OPTION;
                int resultBtn = JOptionPane.showConfirmDialog(null, "Are you sure you want to delete this appointment?", "Warning", confirmBtn);

                if (resultBtn == JOptionPane.YES_OPTION) {
                        String selectedString = selectedItem.toString();
                        selectedString = selectedString.substring(1, selectedString.length() - 1);
                        selectedArr = selectedString.split(",");
                        //Lambda?
                        selectedArr = Arrays.stream(selectedArr).map(String::trim).toArray(String[]::new);
                        String appID = selectedArr[0];
                        int appointmentID = Integer.parseInt(appID);

                        AppointmentQuery.appointmentDelete(appointmentID);
                        refreshTable();

                        //TODO future: actually check if it was deleted. Select on customers to see if appointmentID exists. Write an error message if it's still there.
                        showMessageDialog(null,"Appointment deleted.");
                    }
                }
                else {
                    showMessageDialog(null,"Select a row to delete.");
                }
            }
            catch (SQLException ex) {
            showMessageDialog(null, "Select an appointment to delete.");
            throw new RuntimeException(ex);

        }
    }

    /**
     * Table setting code.
     */
    public void getData() {
        try {
            ObservableList<ObservableList> data = FXCollections.observableArrayList();
            data.clear();
            appointmentsTable.getItems().clear();
            appointmentsTable.getColumns().clear();
            ResultSet rs = AppointmentQuery.select();

            for (int i = 0; i < rs.getMetaData().getColumnCount(); i++){
                final int j = i;
                TableColumn tblCol = new TableColumn(rs.getMetaData().getColumnName(i+1));
                tblCol.setCellValueFactory((Callback<CellDataFeatures<ObservableList, String>, ObservableValue<String>>) param -> new SimpleStringProperty(param.getValue().get(j).toString()));
                appointmentsTable.getColumns().addAll(tblCol);
                //Let me see how many columns got pulled in.
                //System.out.println("Column [" + i + "] ");
                }
            while(rs.next()){
                ObservableList<String> row = FXCollections.observableArrayList();
                for (int i=1; i <= rs.getMetaData().getColumnCount();i++){
                    row.add(rs.getString(i));
                }
                data.add(row);
            }
            appointmentsTable.setItems(data);
            }
        catch (Exception e){
            e.printStackTrace();
            System.out.println("Error getting data in AppointmentQuery");
            System.out.println(e);
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
