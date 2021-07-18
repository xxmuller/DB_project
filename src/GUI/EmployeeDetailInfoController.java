package GUI;

import Database.Address;
import Database.Employee;
import Database.Hotel;
import Database.WorkWithDB;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.io.IOException;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ResourceBundle;

public class EmployeeDetailInfoController implements Initializable {
    private SwitchScene switchScene = new SwitchScene();
    private WorkWithDB workWithDB = new WorkWithDB();
    private Employee employee;
    private Hotel hotel;

    @FXML
    private Label nameOfEmployee;
    @FXML
    private MenuButton chooseRoleMenu;
    @FXML
    private Button backButton, saveChangesButton;
    @FXML
    private TextField tfLastName, tfPhone, tfMail, tfSalary, tfDateFrom, tfDateTo, tfCountry, tfStreet, tfHouseNum, tfPostalCode, tfCity;

    public EmployeeDetailInfoController(Hotel hotel, Employee employee){
        this.employee = employee;
        this.hotel = hotel;
    }

    public void textFieldData(TextField textField, String data){
        textField.setText(data);
    }

    public void displayEmployeeDetailData(){
        DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
        nameOfEmployee.setText(employee.getFirstName() + " " + employee.getLastName());
        textFieldData(tfLastName, employee.getLastName());
        textFieldData(tfPhone, employee.getPhone());
        textFieldData(tfMail, employee.getMail());
        textFieldData(tfSalary, Float.toString(employee.getSalary()));
        // zmena date na string
        String strDateFrom = dateFormat.format(employee.getDateFrom());
        textFieldData(tfDateFrom, strDateFrom);
        String strDateTo = dateFormat.format(employee.getDateTo());
        textFieldData(tfDateTo, strDateTo);

        textFieldData(tfCountry, employee.getAddress().getCountry());
        textFieldData(tfStreet, employee.getAddress().getStreet());
        textFieldData(tfHouseNum, employee.getAddress().getHouseNum());
        textFieldData(tfPostalCode, employee.getAddress().getPostalCode());
        textFieldData(tfCity, employee.getAddress().getCity());
    }

    public void upadateMenuButtonItems(){
        try {
            workWithDB.connectToDatabase(); // nadviaze spojenie s db a vytvori tabulky ak nie su vytvorene
            String query = "SELECT * FROM Role;"; // query dostane vsetky hodnoty pre tabulku Role z databazy
            workWithDB.setResultSet(workWithDB.getStatement().executeQuery(query));
            while(workWithDB.getResultSet().next()) {
                String roleName = workWithDB.getResultSet().getString("role_name");
                MenuItem menuItem = new MenuItem(roleName);
                menuItem.setId(roleName);
                menuItem.setOnAction(actionEvent -> {
                    chooseRoleMenu.setText(roleName);
                });
                chooseRoleMenu.getItems().add(menuItem); // pridam vsetky role do MenuButtonu
                chooseRoleMenu.setText(employee.getRole());
            }
            workWithDB.closeConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean isValidSalary(String salary){
        try{
            Float.parseFloat(salary);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public boolean isValidDate(SimpleDateFormat simpleDateFormat, String date){
        try {
            simpleDateFormat.parse(date);
            return true;
        } catch (ParseException e) {
            return false;
        }
    }

    public Date changeToDate(SimpleDateFormat simpleDateFormat, String date){
        Date dt = null;
        try {
            dt = simpleDateFormat.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return dt;
    }

    public float changeToFloat(String salary){
        float flt = 0;
        try{
            flt = Float.parseFloat(salary);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }

        return flt;
    }

    public boolean updateEmployeeByQuery(Employee employee1){
        try {
            // treba spravit transakcie
            workWithDB.connectToDatabase(); // nadviaze spojenie s db a vytvori tabulky ak nie su vytvorene
            workWithDB.getConn().setAutoCommit(false);

            String query = "UPDATE Address SET city =?, street =?, house_num =?, postal_code =?, country =? WHERE id =?;";
            PreparedStatement preparedStatement = workWithDB.getConn().prepareStatement(query);
            preparedStatement.setString(1, employee1.getAddress().getCity());
            preparedStatement.setString(2, employee1.getAddress().getStreet());
            preparedStatement.setString(3, employee1.getAddress().getHouseNum());
            preparedStatement.setString(4, employee1.getAddress().getPostalCode());
            preparedStatement.setString(5, employee1.getAddress().getCountry());
            preparedStatement.setInt(6, employee.getAddress().getId());
            preparedStatement.executeUpdate();

            workWithDB.getConn().commit();

            query = "UPDATE Employee SET last_name=?, phone=?, mail=?, date_from=?, date_to=?, salary=?, emp_role=? WHERE id=?;";
            PreparedStatement nextPrepStatement = workWithDB.getConn().prepareStatement(query);
            nextPrepStatement.setString(1, employee1.getLastName());
            nextPrepStatement.setString(2, employee1.getPhone());
            nextPrepStatement.setString(3, employee1.getMail());
            nextPrepStatement.setDate(4, employee1.getDateFrom());
            nextPrepStatement.setDate(5, employee1.getDateTo());
            nextPrepStatement.setDouble(6, employee1.getSalary());
            nextPrepStatement.setString(7, employee1.getRole());
            nextPrepStatement.setInt(8, employee.getId());
            nextPrepStatement.executeUpdate();
            workWithDB.getConn().commit();
            workWithDB.getConn().setAutoCommit(true);
            workWithDB.closeConnection();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public void  updateEmployeeInfo(){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MM.yyyy");
        // podmienka zisti, ci sa zadane udaje daju convertnut na potrebne datove typy
        if (!isValidDate(simpleDateFormat, tfDateFrom.getText()) || !isValidDate(simpleDateFormat, tfDateTo.getText()) || !isValidSalary(tfSalary.getText())) {
            System.out.println("Skontroluj zadane datumy a salary");
            return;
        }
        // adresa je NOT NULL, preto je treba tato podmienka
        if (tfCity.getText().equals("") || tfStreet.getText().equals("") || tfHouseNum.getText().equals("")
                || tfPostalCode.getText().equals("") || tfCountry.getText().equals("")) {
            System.out.println("Zadajte potrebne udaje do adresy.");
            return;
        }

        java.sql.Date dateFrom = new java.sql.Date(changeToDate(simpleDateFormat, tfDateFrom.getText()).getTime());
        java.sql.Date dateTo = new java.sql.Date(changeToDate(simpleDateFormat, tfDateTo.getText()).getTime());
        float salary = changeToFloat(tfSalary.getText());

        Employee employee1 = new Employee(employee.getFirstName(), tfLastName.getText(), tfPhone.getText(), tfMail.getText(),
                                         dateFrom, dateTo, salary, chooseRoleMenu.getText(),
                                         new Address(tfCity.getText(), tfStreet.getText(), tfHouseNum.getText(), tfPostalCode.getText(), tfCountry.getText()));
        // ak sa neupdatnu udaje v databaze, tak ich nezmenim ani objektu employee
        if (!updateEmployeeByQuery(employee1)) {
            System.out.println("Nepodarilo sa updatnut udaje v databaze.");
            return;
        }

        nameOfEmployee.setText(employee1.getFirstName() + " " + employee1.getLastName());
        employee1.getAddress().setId(employee.getAddress().getId());
        employee1.setId(employee.getId());
        employee = employee1;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        displayEmployeeDetailData(); // zobrazia sa vsetky udaje pre daneho zamestnanca
        upadateMenuButtonItems(); // do MenuButtonu sa pridaju vsetky role z databazy

        backButton.setOnAction(actionEvent -> {
            try {
                switchScene.switchToManageHotelEmployees(actionEvent, hotel);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        saveChangesButton.setOnAction(actionEvent -> {
            updateEmployeeInfo();
        });
    }
}
