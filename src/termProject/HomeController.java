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
import javafx.stage.Stage;
import javafx.util.Callback;

import javax.swing.*;
import javax.xml.transform.Result;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Year;
import java.util.HashMap;

import static javax.swing.JOptionPane.showMessageDialog;

public class HomeController {

    //<editor-fold desc="Variables and things folded">
    public Label labelNav;
    public Label labelTables;
    public Button btnCustomers;
    public Button btnAppointments;
    public Button btnLogout;
    public Label labelPage;
    public Label labelJan;
    public Label labelFeb;
    public Label labelMar;
    public Label labelApr;
    public Label labelMay;
    public Label labelJun;
    public Label labelJul;
    public Label labelAug;
    public Label labelSep;
    public Label labelOct;
    public Label labelNov;
    public Label labelDec;
    public Label labelCountJan;
    public Label labelCountFeb;
    public Label labelCountMar;
    public Label labelCountApr;
    public Label labelCountMay;
    public Label labelCountJun;
    public Label labelCountJul;
    public Label labelCountAug;
    public Label labelCountSep;
    public Label labelCountOct;
    public Label labelCountNov;
    public Label labelCountDec;
    public Label labelReports;
    public Label labelReportsMonths;
    public ComboBox<Integer> pickYear;
    public TableView tableAppointmentByMonth;
    public Label labelReportsByType;
    @FXML
    private Button btnHome;
    private ObservableList<Integer> years = FXCollections.observableArrayList();
    private static final HashMap<String, Integer> monthNumbers = new HashMap<>();

    //</editor-fold

    public static ResultSet getAppointmentByMonth() throws SQLException {
        String sql = "SELECT MONTHNAME(Start) as Month, Type, COUNT(*) as Count FROM appointments GROUP BY Month, Type ORDER BY Month ASC";
        PreparedStatement ps = JDBC.connection.prepareStatement(sql);
        return ps.executeQuery();
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
    public void customersClick() throws IOException {
        loadScreen("customers_screen.fxml");
    }
    public void appointmentsClick() throws IOException {
        loadScreen("appointments_screen.fxml");
    }
    public static int getAppointmentsMonthCount(String month, int year) throws SQLException{
        String sql = "SELECT COUNT('Type') FROM appointments WHERE MONTH(Start) = ? AND YEAR(Start) = ?";
        PreparedStatement ps = JDBC.connection.prepareStatement(sql);
        ps.setInt(1,monthNumbers.get(month));
        ps.setInt(2,year);
        ResultSet rs = ps.executeQuery();
        int numAppointments = 0;
        while (rs.next()){
            numAppointments = rs.getInt(1);
        }
        return numAppointments;
    }
    public static ResultSet checkAppointmentsSoon() throws SQLException {
        String sql = "SELECT Appointment_ID, Start, TIMESTAMPDIFF(MINUTE, UTC_TIMESTAMP(), Start) As DIFF FROM appointments WHERE TIMESTAMPDIFF(MINUTE, UTC_TIMESTAMP(), Start) > 0 AND TIMESTAMPDIFF(MINUTE, UTC_TIMESTAMP(), Start) < 15";
        PreparedStatement ps = JDBC.connection.prepareStatement(sql);
        return ps.executeQuery();
    }

    //TODO future: optimize somehow. 12 SQL queries for every change is probably not good. Maybe figure out how to get counts from 1 whole ResultSet and work from that instead.
    private void setYearListenerAppointmentCount(Integer t1, Label labelCountJan, Label labelJan, Label labelCountFeb, Label labelFeb, Label labelCountMar, Label labelMar, Label labelCountApr, Label labelApr, Label labelCountMay, Label labelMay, Label labelCountJun, Label labelJun) throws SQLException {
        labelCountJan.setText(Integer.toString(getAppointmentsMonthCount(labelJan.getText(),t1)));
        labelCountFeb.setText(Integer.toString(getAppointmentsMonthCount(labelFeb.getText(),t1)));
        labelCountMar.setText(Integer.toString(getAppointmentsMonthCount(labelMar.getText(),t1)));
        labelCountApr.setText(Integer.toString(getAppointmentsMonthCount(labelApr.getText(),t1)));
        labelCountMay.setText(Integer.toString(getAppointmentsMonthCount(labelMay.getText(),t1)));
        labelCountJun.setText(Integer.toString(getAppointmentsMonthCount(labelJun.getText(),t1)));
    }
    private void setFirstLoadAppointmentCount(Label labelCountJan, Label labelJan, Label labelCountFeb, Label labelFeb, Label labelCountMar, Label labelMar, Label labelCountApr, Label labelApr, Label labelCountMay, Label labelMay, Label labelCountJun, Label labelJun) throws SQLException {
        labelCountJan.setText(Integer.toString(getAppointmentsMonthCount(labelJan.getText(),pickYear.getValue())));
        labelCountFeb.setText(Integer.toString(getAppointmentsMonthCount(labelFeb.getText(),pickYear.getValue())));
        labelCountMar.setText(Integer.toString(getAppointmentsMonthCount(labelMar.getText(),pickYear.getValue())));
        labelCountApr.setText(Integer.toString(getAppointmentsMonthCount(labelApr.getText(),pickYear.getValue())));
        labelCountMay.setText(Integer.toString(getAppointmentsMonthCount(labelMay.getText(),pickYear.getValue())));
        labelCountJun.setText(Integer.toString(getAppointmentsMonthCount(labelJun.getText(),pickYear.getValue())));
    }
    public void getData() {
        try {
            ObservableList<ObservableList> data = FXCollections.observableArrayList();
            data.clear();
            tableAppointmentByMonth.getItems().clear();
            tableAppointmentByMonth.getColumns().clear();
            ResultSet rs = HomeController.getAppointmentByMonth();

            for (int i = 0; i < rs.getMetaData().getColumnCount(); i++){
                final int j = i;
                TableColumn tblCol = new TableColumn(rs.getMetaData().getColumnName(i+1));
                tblCol.setCellValueFactory((Callback<TableColumn.CellDataFeatures<ObservableList, String>, ObservableValue<String>>) param -> new SimpleStringProperty(param.getValue().get(j).toString()));
                tableAppointmentByMonth.getColumns().addAll(tblCol);
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
            tableAppointmentByMonth.setItems(data);
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
    public void initialize() throws SQLException {
        getData();
        pickYear.getItems().clear();
        for (int i = Year.now().getValue()-20; i<=Year.now().getValue()+20; i++){
            years.add(i);
        }
        pickYear.setItems(years);
        pickYear.setValue(Year.now().getValue());

        //Month-->Number HashMap insert
        if (monthNumbers.isEmpty()) {
            monthNumbers.put("Jan", 1);
            monthNumbers.put("Feb", 2);
            monthNumbers.put("Mar", 3);
            monthNumbers.put("Apr", 4);
            monthNumbers.put("May", 5);
            monthNumbers.put("Jun", 6);
            monthNumbers.put("Jul", 7);
            monthNumbers.put("Aug", 8);
            monthNumbers.put("Sep", 9);
            monthNumbers.put("Oct", 10);
            monthNumbers.put("Nov", 11);
            monthNumbers.put("Dec", 12);
            //French, Oct and Nov are the same as English
            monthNumbers.put("Janv",1);
            monthNumbers.put("Févr",2);
            monthNumbers.put("Mars",3);
            monthNumbers.put("Avr",4);
            monthNumbers.put("Mai",5);
            monthNumbers.put("Juin",6);
            monthNumbers.put("Juil",7);
            monthNumbers.put("Aout",8);
            monthNumbers.put("Sept",9);
            //monthNumbers.put("Oct",10);
            //monthNumbers.put("Nov",11);
            monthNumbers.put("Déc",12);
        }

        //Language translation to French
        if (!login_screen.isEnglish()){
            //TODO: labelnav is too long in french, fix.
            labelNav.setText("La navigation");
            btnHome.setText("Maison");
            labelTables.setText("Les tables");
            btnCustomers.setText("Clients");
            btnAppointments.setText("Rendez-vous");
            btnLogout.setText("Se déconnecter");
            labelPage.setText("Page d'accueil");
            labelJan.setText("Janv");
            labelFeb.setText("Févr");
            labelMar.setText("Mars");
            labelApr.setText("Avr");
            labelMay.setText("Mai");
            labelJun.setText("Juin");
            labelJul.setText("Juil");
            labelAug.setText("Aout");
            labelSep.setText("Sept");
            labelDec.setText("Déc");
            labelReports.setText("Rapports");
            labelReportsMonths.setText("Rendez-vous par mois");
            labelReportsByType.setText("Nominations par type");
        }

        //On Home Page load, sets all values for appointment counts by month by current year.
        setFirstLoadAppointmentCount(labelCountJan, labelJan, labelCountFeb, labelFeb, labelCountMar, labelMar, labelCountApr, labelApr, labelCountMay, labelMay, labelCountJun, labelJun);
        setFirstLoadAppointmentCount(labelCountJul, labelJul, labelCountAug, labelAug, labelCountSep, labelSep, labelCountOct, labelOct, labelCountNov, labelNov, labelCountDec, labelDec);

        //On year change, set appointment count values.
        pickYear.getSelectionModel().selectedItemProperty().addListener((observableValue, s, t1) -> {
            try {
                setYearListenerAppointmentCount(t1, labelCountJan, labelJan, labelCountFeb, labelFeb, labelCountMar, labelMar, labelCountApr, labelApr, labelCountMay, labelMay, labelCountJun, labelJun);
                setYearListenerAppointmentCount(t1, labelCountJul, labelJul, labelCountAug, labelAug, labelCountSep, labelSep, labelCountOct, labelOct, labelCountNov, labelNov, labelCountDec, labelDec);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
        }
    }
