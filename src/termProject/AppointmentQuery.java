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
    public void customersClick() throws IOException {
        loadScreen("customers_screen.fxml");
    }
    public void appointmentDeleteClick() {
        try {
            int confirmBtn = JOptionPane.YES_NO_OPTION;
            int resultBtn = JOptionPane.showConfirmDialog(null, "Are you sure you want to delete this appointment?", "Warning", confirmBtn);

            if (resultBtn == JOptionPane.YES_OPTION) {
                selectedItem = appointmentsTable.getSelectionModel().getSelectedItem();
                if (selectedItem != null) {
                    String selectedString = selectedItem.toString();
                    selectedString = selectedString.substring(1, selectedString.length() - 1);
                    selectedArr = selectedString.split(",");
                    //Lambda?
                    selectedArr = Arrays.stream(selectedArr).map(String::trim).toArray(String[]::new);
                    String appID = selectedArr[0];
                    int appointmentID = Integer.parseInt(appID);

                    AppointmentQuery.appointmentDelete(appointmentID);
                    //TODO: change this to a refresh button or something
                    btnCustomers.fire();

                    //TODO future: actually check if it was deleted. Select on customers to see if appointmentID exists. Write an error message if it's still there.
                    showMessageDialog(null,"Customer deleted.");
                }
                else {
                    showMessageDialog(null, "Select an appointment to delete.");
                }
            }
        }
        catch (Exception e) {
            showMessageDialog(null, "Select an appointment to delete.");
        }
    }

    /**
     * Table setting code.
     */
    public void getData() {
        try {
            ObservableList<ObservableList> data = FXCollections.observableArrayList();
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
                //TODO: remove these comments, AppointmentQuery
                //Let me see the row data added;
                //System.out.println("Row [1] added " +row );
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
    public void initialize() {
        getData();
    }
}
