package termProject;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.stage.Stage;
import javafx.util.Callback;
import javax.swing.*;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;

import static javax.swing.JOptionPane.showMessageDialog;

public class AppointmentQuery {

    /**
     * Variables for FXML controllers, tableview for customer data, and for selecting an item from the tableview.
     */
    //<editor-fold desc="Variables folded">
    public Label labelNav;
    public Label labelTables;
    public Button btnCustomers;
    public Button btnAppointments;
    public Button btnLogout;
    public Button btnAppointmentAdd;
    public Button btnAppointmentsModify;
    public Button btnAppointmentDelete;
    public Button btnRefresh;
    public Label labelPlace;
    public RadioButton radioMonthly;
    public RadioButton radioWeekly;
    public RadioButton radioAll;
    public ComboBox<Integer> pickMonth;
    public Label labelPickMonth;
    public Label labelPickYear;
    public ComboBox<Integer> pickYear;
    @FXML
    private Button btnHome;
    @FXML
    private TableView appointmentsTable;
    @FXML
    public TableView appointmentsMonthTable;
    @FXML
    public TableView appointmentsWeekTable;
    String[] selectedArr;
    Object selectedItem;
    //</editor-fold

    /**
     * Observable lists of months and years.
     * Years to be added during initialize.
     */
    //<editor-fold desc="Lists folded">
    final ObservableList<Integer> months = FXCollections.observableArrayList(1,2,3,4,5,6,7,8,9,10,11,12);
    public ObservableList<Integer> years = FXCollections.observableArrayList();
    //</editor-fold

    /**
     * Takes in parameters and passes them into an SQL query to insert appointment information.
     * @param title appointment title
     * @param description appointment description
     * @param location appointment location
     * @param type appointment type
     * @param start appointment start timedate
     * @param end appointment end timedate
     * @param createDate appointment create date
     * @param createdBy appointment creater
     * @param lastUpdate appointment update date
     * @param lastUpdatedBy appointment updater user
     * @param customerID customer ID of the appointment's customer
     * @param userID appointment user's ID
     * @param contactID appointment contact's ID
     */
    public static void appointmentInsert(String title, String description, String location, String type, String start, String end, String createDate, String createdBy, String lastUpdate, String lastUpdatedBy, int customerID, int userID, int contactID) throws SQLException {
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
        ps.executeUpdate();
    }

    /**
     * Takes in a list of parameters and passes to a query that updates the existing appointment based on Appointment ID.
     */
    public static void appointmentUpdate(String title, String description, String location, String type, String start, String end, String lastUpdate, String lastUpdatedBy, int customerID, int userID, int contactID, int appointmentID) throws SQLException {
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
        ps.executeUpdate();
    }

    /**
     * Deletes appointment from DB based on appointment ID.
     * @param appointmentID ID of appointment to be deleted
     */
    public static void appointmentDelete(int appointmentID) throws SQLException {
        String sql = "DELETE FROM appointments WHERE Appointment_ID = ?";
        PreparedStatement ps = JDBC.connection.prepareStatement(sql);
        ps.setInt(1, appointmentID);
        ps.executeUpdate();
    }

    /**
     * Returns the full result set of a query on the appointments table.
     * @return returns result set
     */
    public static ResultSet select() throws SQLException {
        String sql = "SELECT * FROM appointments";
        PreparedStatement ps = JDBC.connection.prepareStatement(sql);
        return ps.executeQuery();
    }

    /**
     * Takes in 2 parameters and queries for appointments with those as the date start.
     * @param month user selected month
     * @param year user selected year
     * @return a result set of the month and year selected, of appointments.
     */
    public static ResultSet selectMonthly(int month, int year) throws SQLException{
        String sql = "SELECT * FROM appointments WHERE MONTH(Start) = ? AND YEAR(Start) = ?";
        PreparedStatement ps = JDBC.connection.prepareStatement(sql);
        ps.setInt(1,month);
        ps.setInt(2,year);
        return ps.executeQuery();
    }

    /**
     * Returns a result set based on the current week.
     * @return a result of the appointments in the current week.
     */
    private static ResultSet selectWeekly() throws SQLException {
        String sql = "SELECT * FROM appointments WHERE YEARWEEK(`Start`, 1) = YEARWEEK(CURDATE(), 1)";
        PreparedStatement ps = JDBC.connection.prepareStatement(sql);
        return ps.executeQuery();
    }

    /**
     * Gets the contact ID of the contact as part of the appointment.
     * @param contactName name of the contact selected in the combobox
     * @return return the ID of the selected contact
     */
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

    /**
     * To Modify an appointment, this function is used to query the name from an ID.
     * @param contactID ID of the contact selected.
     * @return returns a String of the contact's name.
     */
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

    /**
     * Assigns the user of the appointment to the currently logged-in user.
     * Gets a result set and gets the only result.
     * @return returns integer ID of the logged in user.
     */
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

    /**
     * Gets contact names from the DB.
     * @return returns a result set of all names from the contact table.
     */
    public static ResultSet getContactList() throws SQLException {
        String sql = "SELECT Contact_Name FROM contacts";
        PreparedStatement ps = JDBC.connection.prepareStatement(sql);
        return ps.executeQuery();
    }
    public static ResultSet getTypeList() throws SQLException {
        String sql = "SELECT DISTINCT Type FROM appointments ORDER BY Type ASC";
        PreparedStatement ps = JDBC.connection.prepareStatement(sql);
        return ps.executeQuery();
    }

    /**
     * Used to determine if a pair of datetime inputs (start and end) overlap with existing appointments.
     * This DOES allow for appointments to start right when another ends, e.g 8AM - 9AM works with 9AM to 10AM
     * @param customerID ID of the customer we're looking at appointments for
     * @param startDatetime start datetime of input
     * @param endDatetime end datetime of input
     * @return returns true if there is an overlap, false if not
     */
    public static boolean timesOverlap(int customerID, String startDatetime, String endDatetime) throws SQLException {
        String sql = "SELECT Appointment_ID FROM appointments WHERE Customer_ID = ? AND ? < END AND ? > START";
        PreparedStatement ps = JDBC.connection.prepareStatement(sql);
        ps.setInt(1, customerID);
        ps.setString(2, startDatetime);
        ps.setString(3, endDatetime);

        ResultSet rs = ps.executeQuery();

        return rs.isBeforeFirst();
    }
//-----------------------------------------------------------------------------------------------

    /**
     * Determines if an appointment's start and end time are in business hours.
     * @param dateTimeStart appointment start datetime
     * @param dateTimeEnd appointment end datetime
     * @return true if the times are out of business hours, false if within.
     */
    public static boolean isOutOfHours(String dateTimeStart, String dateTimeEnd) {
        //Ids and formats
        String DATE_FORMAT = "yyyy-MM-dd HH:mm";
        DateTimeFormatter format = DateTimeFormatter.ofPattern(DATE_FORMAT);
        ZoneId estZoneId = ZoneId.of("America/New_York");

        // START: LDT (ldt parse string in format) --> System DT atZone(zoneID) --> Target DT .withZoneSameInstant(zoneID) --> String of ZDT in format
        LocalDateTime ldtStart = LocalDateTime.parse(dateTimeStart, format);
        ZonedDateTime estZdtStart = ldtStart.atZone(ZoneId.systemDefault()).withZoneSameInstant(estZoneId);
        String estFormattedStart  = format.format(estZdtStart);
        //Weekend is part of business hours, not needed --> String startDay = estZdtStart.getDayOfWeek().toString();

        // END: same as Start
        LocalDateTime ldtEnd = LocalDateTime.parse(dateTimeEnd, format);
        ZonedDateTime estZdtEnd = ldtEnd.atZone(ZoneId.systemDefault()).withZoneSameInstant(estZoneId);
        String estFormattedEnd  = format.format(estZdtEnd);
        //Weekend is part of business hours, not needed --> String endDay = estZdtEnd.getDayOfWeek().toString();

        //2023-06-12 09:00:00 -- formatted datetime

        return Integer.parseInt(estFormattedStart.substring(11, 13)) < 8 || Integer.parseInt(estFormattedStart.substring(11, 13)) > 22 || Integer.parseInt(estFormattedEnd.substring(11, 13)) < 8 || Integer.parseInt(estFormattedEnd.substring(11, 13)) > 22;
    }

    /**
     * Loads the scene to add appointments.
     */
    public void appointmentAddClick() throws IOException {
        FXMLLoader partLoader = new FXMLLoader(getClass().getResource("appointment_add.fxml"));
        Parent partRoot = partLoader.load();
        Stage partStage = new Stage();
        partStage.setScene(new Scene(partRoot));
        partStage.show();
    }

    /**
     * Loads the scene to modify appointments based on the tableview selected item.
     * Uses initAppointmentData to "move" data into the other class to be prefilled with the selected data.
     */
    public void appointmentModifyClick() {
        try {
        selectedItem = appointmentsTable.getSelectionModel().getSelectedItem();
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

    /**
     * Deletes an appointment based on appointment ID.
     */
    public void appointmentDeleteClick() {
        try {
            selectedItem = appointmentsTable.getSelectionModel().getSelectedItem();
            if (selectedItem != null) {
                int confirmBtn = JOptionPane.YES_NO_OPTION;
                int resultBtn;
                if (login_screen.isEnglish()){
                    resultBtn = JOptionPane.showConfirmDialog(null, "Are you sure you want to delete this appointment?", "Warning", confirmBtn);
                }
                else {
                    resultBtn = JOptionPane.showConfirmDialog(null, "Voulez-vous vraiment supprimer ce rendez-vous?", "Avertissement", confirmBtn);
                }

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
                    if (login_screen.isEnglish()){
                        showMessageDialog(null,"Appointment deleted.\n" + "Appointment ID: " + appointmentID + "\n" + "Type: " + selectedArr[4]);
                    }
                    else {
                        showMessageDialog(null, "Rendez-vous supprimé.");
                    }
                }
            }
            else {
                if (login_screen.isEnglish()){
                    showMessageDialog(null, "Select a row to delete.");
                }
                else {
                    showMessageDialog(null, "Sélectionnez une ligne à supprimer.");
                }
            }
        }
        catch (SQLException ex) {
            if (login_screen.isEnglish()){
                showMessageDialog(null, "Select an appointment to delete.");
            }
            else {
                showMessageDialog(null, "Sélectionnez un rendez-vous à supprimer.");
            }
            throw new RuntimeException(ex);

        }
    }

    /**
     * Takes a string parameter and opens the FXML of that name into a new scene window.
     * @param screenName filename of the FXML file being loaded.
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

    /**
     * Logs the user out by loading into the login screen.
     * Assigns login_screen's loggedUser to null.
     */
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

    /**
     * Loads the home screen scene window using its fxml file.
     */
    public void homeClick() throws IOException {
        loadScreen("home_screen.fxml");
    }
    /**
     * Loads the customers scene window using its fxml file.
     */
    public void customersClick() throws IOException {
        loadScreen("customers_screen.fxml");
    }

    /**
     * Linked to the All-TABLES radio button.
     * Hides all tableview and elements not related to showing ALL appointment data.
     * Gets the data again to refresh the table.
     */
    public void setRadioAll(){
        radioAll.setSelected(true);
        radioMonthly.setSelected(false);
        radioWeekly.setSelected(false);
        appointmentsTable.setVisible(true);
        appointmentsWeekTable.setVisible(false);
        appointmentsMonthTable.setVisible(false);
        labelPickMonth.setVisible(false);
        labelPickYear.setVisible(false);
        pickMonth.setVisible(false);
        pickYear.setVisible(false);
        getData();
        if (login_screen.isEnglish()) {
            labelPlace.setText("All Appointments");
        }
        else {
            labelPlace.setText("Tous les rendez-vous");
        }
    }

    /**
     * Linked to the monthly radio button.
     * Hides all tableview and elements not related to showing monthly appointment data.
     * Gets the data again to refresh the table.
     */
    public void setRadioMonthly(){
        radioAll.setSelected(false);
        radioMonthly.setSelected(true);
        radioWeekly.setSelected(false);
        appointmentsTable.setVisible(false);
        appointmentsWeekTable.setVisible(false);
        appointmentsMonthTable.setVisible(true);
        labelPickMonth.setVisible(true);
        labelPickYear.setVisible(true);
        pickMonth.setVisible(true);
        pickYear.setVisible(true);
        getDataMonthly();
        if (login_screen.isEnglish()) {
            labelPlace.setText("Monthly Appointments");
        }
        else {
            labelPlace.setText("Rendez-vous mensuels");
            labelPickMonth.setText("Mois");
            labelPickYear.setText("Année");
        }
    }

    /**
     * Linked to the weekly radio button.
     * Hides all tableview and elements not related to showing weekly appointment data.
     * Gets the data again to refresh the table.
     */
    public void setRadioWeekly(){
        radioAll.setSelected(false);
        radioMonthly.setSelected(false);
        radioWeekly.setSelected(true);
        appointmentsTable.setVisible(false);
        appointmentsWeekTable.setVisible(true);
        appointmentsMonthTable.setVisible(false);
        labelPickYear.setVisible(false);
        labelPickMonth.setVisible(false);
        pickYear.setVisible(false);
        pickMonth.setVisible(false);
        getDataWeekly();
        //TODO: set visibility of week things
        if (login_screen.isEnglish()) {
            labelPlace.setText("Weekly Appointments");
        }
        else {
            labelPlace.setText("Rendez-vous hebdomadaires");
        }
    }
    //TODO future: refreshTable does all 3.
    // Can make more buttons and refresh methods to only refresh the currently selected view and save on extra queries.
    // Stack the buttons on top of the others and hide/show where needed.

    /**
     * Gets data for all appointments.
     * Dynamically creates the columns and fills them for appointments, based on the Result Set queried.
     * Receives the data and then puts it into the observable list, then setting the Tableview with that list.
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
            if (login_screen.isEnglish()){
                showMessageDialog(null, "Error getting data.");
            }
            else {
                showMessageDialog(null, "Erreur lors de l'obtention des données.");
            }
        }
    }

    /**
     * Variation of the getData function that queries for a result set based on month and year.
     */
    public void getDataMonthly() {
        try {
            ObservableList<ObservableList> data = FXCollections.observableArrayList();
            data.clear();
            appointmentsMonthTable.getItems().clear();
            appointmentsMonthTable.getColumns().clear();
            ResultSet rs = AppointmentQuery.selectMonthly(pickMonth.getValue(), pickYear.getValue());

            for (int i = 0; i < rs.getMetaData().getColumnCount(); i++){
                final int j = i;
                TableColumn tblCol = new TableColumn(rs.getMetaData().getColumnName(i+1));
                tblCol.setCellValueFactory((Callback<CellDataFeatures<ObservableList, String>, ObservableValue<String>>) param -> new SimpleStringProperty(param.getValue().get(j).toString()));
                appointmentsMonthTable.getColumns().addAll(tblCol);
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
            appointmentsMonthTable.setItems(data);
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

    /**
     * Variation of the getData function that queries for a result set based on current week.
     */
    public void getDataWeekly() {
        try {
            ObservableList<ObservableList> data = FXCollections.observableArrayList();
            data.clear();
            appointmentsWeekTable.getItems().clear();
            appointmentsWeekTable.getColumns().clear();
            ResultSet rs = AppointmentQuery.selectWeekly();

            for (int i = 0; i < rs.getMetaData().getColumnCount(); i++){
                final int j = i;
                TableColumn tblCol = new TableColumn(rs.getMetaData().getColumnName(i+1));
                tblCol.setCellValueFactory((Callback<CellDataFeatures<ObservableList, String>, ObservableValue<String>>) param -> new SimpleStringProperty(param.getValue().get(j).toString()));
                appointmentsWeekTable.getColumns().addAll(tblCol);
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
            appointmentsWeekTable.setItems(data);
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

    /**
     * Refreshes all tables by calling their version of the getData method.
     */
    public void refreshTable(){
        System.out.println("before refresh");
        getData();
        getDataMonthly();
        getDataWeekly();
        System.out.println("after refresh");
    }

    /**
     * Defaults to settings to display all appointments.
     */
    public void initialize() {
        setRadioAll();
        //Don't think I need to clear because it's a final list.
        //pickMonth.getItems().clear();
        pickYear.getItems().clear();
        pickMonth.setItems(months);
        //Set items for the year-picker to be everything within 20 years of current year.
        for (int i = Year.now().getValue()-20; i<=Year.now().getValue()+20; i++){
            years.add(i);
        }
        pickYear.setItems(years);
        pickMonth.setValue(LocalDate.now().getMonthValue());
        pickYear.setValue(Year.now().getValue());
        getData();

        if (!login_screen.isEnglish()){
            //TODO: labelnav is too long in french, fix.
            labelNav.setText("La navigation");
            btnHome.setText("Maison");
            labelTables.setText("Les tables");
            btnCustomers.setText("Clients");
            btnAppointments.setText("Rendez-vous");
            btnLogout.setText("Se déconnecter");
            labelPlace.setText("Tous les rendez-vous");
            btnRefresh.setText("Rafraîchir");
            btnAppointmentAdd.setText("Ajouter");
            btnAppointmentsModify.setText("Modifier");
            btnAppointmentDelete.setText("Supprimer");
        }
    }
}
