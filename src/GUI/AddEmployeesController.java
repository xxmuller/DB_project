package GUI;

import Database.Address;
import Database.Employee;
import Database.Hotel;
import Database.WorkWithDB;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;

public class AddEmployeesController {
    @FXML
    private SwitchScene switchScene = new SwitchScene();
    @FXML
    private TextField tfFirstName, tfLastName, tfPhone, tfMail, tfDateFrom, tfDateTo, tfSalary,
            tfCity, tfStreet, tfCountry, tfHouseNum, tfPostalCode;
    @FXML
    private MenuButton chooseRoleMenu;
    @FXML
    private Button continueButton;

    private WorkWithDB workWithDB = new WorkWithDB();
    private boolean loadMenuItems = true;
    private Hotel hotel;
    private String role;

    public void setHotel(Hotel hotel) {
        this.hotel = hotel;
    }

    public Hotel getHotel() {
        return hotel;
    }

    //zakazdym nastavi button text pri prehodeni na tuto scenu
    public void setButtonText(){
        if(hotel.getNumOfEmployees() > 1){
            continueButton.setText("Add next (" + (hotel.getNumOfEmployees() - 1) + ")");
        }
        else{
            continueButton.setText("Continue");
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

    //vkladanie udajov o zamestnancovi do databazy
    public void insertEmployeeInfo(Employee employee){
        try {
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
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void continueButtonClicked(ActionEvent actionEvent) throws IOException, ParseException {
        if (hotel.getNumOfEmployees() > 0) {
            SimpleDateFormat date = new SimpleDateFormat("dd.MM.yyyy");
            if (isValidDate(date, tfDateFrom.getText()) && isValidDate(date, tfDateTo.getText()) && isValidSalary(tfSalary.getText())) {
                if (!(tfCity.getText().equals("")) && !(tfStreet.getText().equals("")) && !(tfHouseNum.getText().equals(""))
                        && !(tfPostalCode.getText().equals("")) && !(tfCountry.getText().equals(""))) { // adresa je NOT NULL, preto je treba tato podmienka

                    //vytvaram datum zo stringu
                    java.util.Date df = date.parse(tfDateFrom.getText());
                    java.sql.Date dateFrom = new java.sql.Date(df.getTime()); // datum odkedy zamestnanec pracuje

                    java.util.Date dt = date.parse(tfDateTo.getText());
                    java.sql.Date dateTo = new java.sql.Date(dt.getTime()); // datum dokedy zamestnanec pracuje

                    Employee employee = new Employee(tfFirstName.getText(), tfLastName.getText(), tfPhone.getText(), tfMail.getText(),
                            dateFrom, dateTo, Float.parseFloat(tfSalary.getText()),
                            this.role, new Address(tfCity.getText(), tfStreet.getText(), tfHouseNum.getText(), tfPostalCode.getText(), tfCountry.getText()));

                    workWithDB.connectToDatabase(); // nadviaze spojenie s db a vytvori tabulky ak nie su vytvorene
                    insertEmployeeInfo(employee);
                    workWithDB.closeConnection();

                    hotel.setNumOfEmployees(hotel.getNumOfEmployees() - 1);
                    if (hotel.getNumOfEmployees() > 0) // pokial som nepridal vsetkych zamestnancov hotela pridavam dalsich, teda opat volam tuto scenu
                        switchScene.switchToAddEmployeesScene(actionEvent, hotel);
                    else
                        switchScene.switchToAddRoomsScene(actionEvent, this.hotel);
                } else {
                    System.out.println("Zadaj potrebne udaje do adresy!"); // neboli zadane udaje pre adresu
                }
            } else
                System.out.println("Skontroluj zadane datumy a salary");
        } else // ak som pridal vsetkych posuvam sa na dalsiu scenu
            switchScene.switchToAddRoomsScene(actionEvent, this.hotel);
    }

    public void upadateMenuButtonItems(){
        workWithDB.connectToDatabase(); // nadviaze spojenie s db a vytvori tabulky ak nie su vytvorene

        if (loadMenuItems) {
            try {
                String query = "SELECT * FROM Role;"; // query dostane vsetky hodnoty pre tabulku Role z databazy
                workWithDB.setResultSet(workWithDB.getStatement().executeQuery(query));
                while (workWithDB.getResultSet().next()) {
                    String roleName = workWithDB.getResultSet().getString("role_name");
                    MenuItem menuItem = new MenuItem(roleName);
                    menuItem.setId(roleName);
                    menuItem.setOnAction(actionEvent -> {
                        chooseRoleMenu.setText(roleName);
                        role = roleName;
                    });
                    chooseRoleMenu.getItems().add(menuItem); // pridam vsetky role do MenuButtonu
                }
                loadMenuItems = false;
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        workWithDB.closeConnection();
    }

    public void backButtonClicked(ActionEvent actionEvent) throws IOException {
        switchScene.switchToAddHotelScene(actionEvent);
    }

    public void nextSceneButtonClicked(ActionEvent actionEvent) throws IOException {
        switchScene.switchToAddRoomsScene(actionEvent, this.hotel);
    }
}
