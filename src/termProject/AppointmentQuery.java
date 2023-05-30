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
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.TableView;
import javafx.stage.Stage;
import javafx.util.Callback;
import javax.swing.*;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AppointmentQuery {
    /**
     * Variable declarations.
     */
    @FXML
    private Button btnHome;
    @FXML
    private TableView appointmentsTable;
    private ObservableList<ObservableList> data;

    /**
     * SQL statement methods.
     */
    public static int appointmentInsert(String title, int customerID, int userID, int contactID) throws SQLException {
        String sql = "INSERT INTO appointments (Title, Customer_ID, User_ID, Contact_ID) VALUES (?, ?, ?, ?)";
        PreparedStatement ps = JDBC.connection.prepareStatement(sql);
        ps.setString(1, title);
        ps.setInt(2, customerID);
        ps.setInt(3, userID);
        ps.setInt(4, contactID);
        return ps.executeUpdate();
    }

    public static int appointmentUpdate(int customerID, String customerName, String customerAddress, String customerPostal, String customerPhone) throws SQLException {
        String sql = "";
        PreparedStatement ps = JDBC.connection.prepareStatement(sql);
        //ps.setXXX all of them here
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
        ResultSet rs = ps.executeQuery();
        return rs;
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

    /**
     * Table setting code.
     */
    public void getData() {
        try {
            data = FXCollections.observableArrayList();
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
        }
    }
    public void initialize() {
        getData();
    }
}
