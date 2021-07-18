package GUI;

import Database.Address;
import Database.Employee;
import Database.Hotel;
import Database.WorkWithDB;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ResourceBundle;

public class AddEmployeesHotelController implements Initializable {
    private SwitchScene switchScene = new SwitchScene();
    private WorkWithDB workWithDB = new WorkWithDB();
    private Hotel hotel;
    private String role;

    @FXML
    private TextField tfFirstName, tfLastName, tfPhone, tfMail, tfDateFrom, tfDateTo, tfSalary,
            tfCity, tfStreet, tfCountry, tfHouseNum, tfPostalCode;
    @FXML
    private MenuButton chooseRoleMenu;
    @FXML
    private Button backButton, addEmployeeButton;


    public AddEmployeesHotelController(Hotel hotel){
        this.hotel = hotel;
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
                    role = roleName;
                });
                chooseRoleMenu.getItems().add(menuItem); // pridam vsetky role do MenuButtonu
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

    public void insertEmployeeInfo(Employee employee){
        try {
            workWithDB.connectToDatabase(); // nadviaze spojenie s db a vytvori tabulky ak nie su vytvorene
            String query = "INSERT INTO Address (city, street, house_num, postal_code, country) " +
                    "VALUES ('" + employee.getAddress().getCity() + "', '" + employee.getAddress().getStreet() + "', '" +
                    employee.getAddress().getHouseNum() + "', '" + employee.getAddress().getPostalCode() + "', '" + employee.getAddress().getCountry() + "');";

            workWithDB.getStatement().executeUpdate(query); // query pridalo novy riadok do tabulky Address

            query = "SELECT id FROM Address ORDER BY ID DESC LIMIT 1;"; // query, ktore mi vrati hodnotu id posledneho pridaneho riadku
            workWithDB.setResultSet(workWithDB.getStatement().executeQuery(query));
            workWithDB.getResultSet().next();
            int addressID = workWithDB.getResultSet().getInt("id"); // ziskavam ID adresy

            query = "INSERT INTO Employee (first_name, last_name, phone, mail, date_from, date_to, salary, hotel_id, emp_role, address_id) " +
                    "VALUES ('" + employee.getFirstName() + "', '" + employee.getLastName() + "', '" +
                    employee.getPhone() + "', '" + employee.getMail() + "', '" + employee.getDateFrom() + "', '"+
                    employee.getDateTo()+"', '"+employee.getSalary()+"', '"+hotel.getId()+"', '"+
                    employee.getRole()+"', '"+addressID+"');";
            workWithDB.getStatement().executeUpdate(query); // pridanie noveho zamestnanca do tabulky Employee
            workWithDB.getConn().setAutoCommit(false);

            query = "UPDATE Hotel SET num_of_employees=? WHERE id=?;";
            PreparedStatement preparedStatement = workWithDB.getConn().prepareStatement(query);
            preparedStatement.setInt(1, hotel.getNumOfEmployees() + 1);
            preparedStatement.setInt(2, hotel.getId());
            preparedStatement.executeUpdate();

            hotel.setNumOfEmployees(hotel.getNumOfEmployees() + 1);
            workWithDB.getConn().commit();
            workWithDB.getConn().setAutoCommit(true);
            workWithDB.closeConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void addEmployeeButtonClicked() throws ParseException {
        SimpleDateFormat date = new SimpleDateFormat("dd.MM.yyyy");
        if (!isValidDate(date, tfDateFrom.getText()) || !isValidDate(date, tfDateTo.getText()) || !isValidSalary(tfSalary.getText())) {
            System.out.println("Skontroluj zadane datumy a salary");
            return;
        }
        if (!(tfCity.getText().equals("")) && !(tfStreet.getText().equals("")) && !(tfHouseNum.getText().equals(""))
                && !(tfPostalCode.getText().equals("")) && !(tfCountry.getText().equals(""))) { // adresa je NOT NULL, preto je treba tato podmienka

            //vytvaram datum zo stringu
            java.util.Date df = date.parse(tfDateFrom.getText());
            java.sql.Date dateFrom = new java.sql.Date(df.getTime()); // datum odkedy zamestnanec pracuje

            java.util.Date dt = date.parse(tfDateTo.getText());
            java.sql.Date dateTo = new java.sql.Date(dt.getTime()); // datum dokedy zamestnanec pracuje

            Employee employee = new Employee(tfFirstName.getText(), tfLastName.getText(), tfPhone.getText(), tfMail.getText(),
                                            dateFrom, dateTo, Float.parseFloat(tfSalary.getText()), role,
                                            new Address(tfCity.getText(), tfStreet.getText(), tfHouseNum.getText(), tfPostalCode.getText(), tfCountry.getText()));

            insertEmployeeInfo(employee);
        } else {
            System.out.println("Zadaj potrebne udaje do adresy!"); // neboli zadane udaje pre adresu
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        upadateMenuButtonItems();

        addEmployeeButton.setOnAction(actionEvent -> {
            try {
                addEmployeeButtonClicked();
            } catch (ParseException e) {
                e.printStackTrace();
            }
        });

        backButton.setOnAction(actionEvent -> {
            try {
                switchScene.switchToManageHotelEmployees(actionEvent, hotel);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }
}
