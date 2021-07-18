package GUI;

import Database.Address;
import Database.Employee;
import Database.Hotel;
import Database.WorkWithDB;

import java.sql.PreparedStatement;
import java.text.DecimalFormat;
import TableModels.EmployeeTable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import java.io.IOException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.ResourceBundle;

public class ManageHotelEmployeesController implements Initializable {
    public static class RoleSum {
        String empRole;
        String countEmp;
        String sumSalary;

        public RoleSum(String empRole, String countEmp, String sumSalary){
            this.empRole = empRole;
            this.countEmp = countEmp;
            this.sumSalary = sumSalary;
        }

        public String getEmpRole() {
            return empRole;
        }

        public String getCountEmp() { return countEmp; }

        public String getSumSalary() { return sumSalary; }
    }

    private ObservableList<EmployeeTable> observableList = FXCollections.observableArrayList(); // vytvorenie listu, ktory prida udaje do tabulky pre zamestnancov
    private ObservableList<RoleSum> observableListSum = FXCollections.observableArrayList(); // vytvorenie listu, ktory prida udaje do tabulky pre sumovanie salary podla role
    private SwitchScene switchScene = new SwitchScene();
    private WorkWithDB workWithDB = new WorkWithDB();
    private String chosenRole = "none";
    private Hotel hotel;

    @FXML
    private TableView <EmployeeTable> tableView; // v tabulke budu iba udaje z classy EmployeeTable
    @FXML
    private TableColumn<EmployeeTable, String> columnId; // podla toho aky datovy typ je Id v classe EmployeeTable som vytvoril <EmployeeTable, String>
    @FXML
    private TableColumn <EmployeeTable, String> columnFirstName; // podla toho aky datovy typ je firstName v classe EmployeeTable som vytvoril <EmployeeTable, String>
    @FXML
    private TableColumn <EmployeeTable, String> columnLastName; // podla toho aky datovy typ je lastName v classe EmployeeTable som vytvoril <EmployeeTable, String>
    @FXML
    private TableColumn <EmployeeTable, String> columnSalary; // podla toho aky datovy typ je salary v classe EmployeeTable som vytvoril <EmployeeTable, String>
    @FXML
    private TableColumn <EmployeeTable, String> columnRole; // podla toho aky datovy typ je role v classe EmployeeTable som vytvoril <EmployeeTable, String
    @FXML
    private TableView <RoleSum> tableSumSalaryByRole;
    @FXML
    private TableColumn <RoleSum, String> columnRoleSalary;
    @FXML
    private TableColumn <RoleSum, String> columnSumSalary;
    @FXML
    private TableColumn <RoleSum, String> columnTotalEmployeesSalary;
    @FXML
    private Button backButton, addEmployeeButton, deleteEmployeeButton, sumSalaryByRoleButton, detailInfoButton, roleFilterButton, employeeTableButton;
    @FXML
    private TextField textFieldSalary;
    @FXML
    private MenuButton chooseRoleMenu;

    public ManageHotelEmployeesController(Hotel hotel){
        this.hotel = hotel;
    }

    public void addToObservableListAndDisplay(ResultSet resultSet){
        // pokial nie je koniec udajov, ktore boli nacitane do ResultSet pomocou query, tak bude pokracovat v cykle
        try {
            observableList.clear(); // vycisty list, aby som tam mohol dat udaje, ktore sa vyfiltrovali
            while (resultSet.next()) {
                // pridanie jedneho zaznamu do listu, ktory sa potom zobrazi v tableView, udaje do neho taham z vysledku, ktory som dostal pomocou predosleho query
                observableList.add(new EmployeeTable(workWithDB.getResultSet().getString("id"),
                        workWithDB.getResultSet().getString("first_name"),
                        workWithDB.getResultSet().getString("last_name"),
                        workWithDB.getResultSet().getString("salary"),
                        workWithDB.getResultSet().getString("emp_role")));

            }
            tableView.setItems(observableList); // zobrazenie v tabulke, ktora sa nachadza v scene, ktoru ovlada tento controller
            tableSumSalaryByRole.setVisible(false);
            employeeTableButton.setVisible(false);
            tableView.setVisible(true);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void displayDataInTableView(){ // v tabulke sa zobrazia udaje zamestnancov, ktory pracuju v hoteli, ktory sme pridali
        try {
            String query = "SELECT id, first_name, last_name, salary, emp_role " +
                           "FROM employee " +
                           "WHERE hotel_id=" + hotel.getId() + ";";
            workWithDB.setResultSet(workWithDB.getStatement().executeQuery(query)); // query, ktore vytiahne iba potrebne udaje do tabulky zamestnancov a vytiahne iba zamestnancov, ktory pracuju v tomto hoteli
            addToObservableListAndDisplay(workWithDB.getResultSet());
        } catch (SQLException e) {
            e.printStackTrace();
        }
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
                    chosenRole = roleName;
                });
                chooseRoleMenu.getItems().add(menuItem); // pridam vsetky role do MenuButtonu
            }
            MenuItem menuItem = new MenuItem("none");
            menuItem.setId("none");
            menuItem.setOnAction(actionEvent -> {
                chooseRoleMenu.setText("none");
                chosenRole = "none";
            });
            chooseRoleMenu.getItems().add(menuItem);
            workWithDB.closeConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Address getEmployeeAddress(int employeeId){
        try {
            String query = "SELECT address_id FROM Employee WHERE id=" + employeeId + ";";
            workWithDB.setResultSet(workWithDB.getStatement().executeQuery(query));
            workWithDB.getResultSet().next();
            int addressId = workWithDB.getResultSet().getInt("address_id");

            query = "SELECT * FROM Address WHERE id=" + addressId + ";";
            workWithDB.setResultSet(workWithDB.getStatement().executeQuery(query));
            workWithDB.getResultSet().next();
            Address employeeAddress = new Address(workWithDB.getResultSet().getString("city"),
                    workWithDB.getResultSet().getString("street"),
                    workWithDB.getResultSet().getString("house_num"),
                    workWithDB.getResultSet().getString("postal_code"),
                    workWithDB.getResultSet().getString("country"));
            employeeAddress.setId(addressId);
            return employeeAddress;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public Employee chosenEmployee(){
        workWithDB.connectToDatabase(); // nadviaze spojenie s db a vytvori tabulky ak nie su vytvorene

        if (!tableView.isVisible()) {
            displayDataInTableView();
            workWithDB.closeConnection();
            return null;
        }

        if (tableView.getSelectionModel().getSelectedItem() != null) { // je vybrany zamestnance, takze mozem vymazat jeho zaznam
            try {
                EmployeeTable selectedEmployee = tableView.getSelectionModel().getSelectedItem();

                Address employeeAddress = getEmployeeAddress(Integer.parseInt(selectedEmployee.getId())); // naprv zistim adresu zamesntnaca
                if (employeeAddress == null) {
                    System.out.println("Nepodarli sa ziskat adresu zamestnanca.");
                    return null;
                }

                String query = "SELECT * FROM employee WHERE id=" + selectedEmployee.getId() + ";";
                workWithDB.setResultSet(workWithDB.getStatement().executeQuery(query));
                workWithDB.getResultSet().next();
                Employee employee = new Employee(workWithDB.getResultSet().getString("first_name"),
                        workWithDB.getResultSet().getString("last_name"),
                        workWithDB.getResultSet().getString("phone"),
                        workWithDB.getResultSet().getString("mail"),
                        workWithDB.getResultSet().getDate("date_from"),
                        workWithDB.getResultSet().getDate("date_to"),
                        workWithDB.getResultSet().getFloat("salary"),
                        workWithDB.getResultSet().getString("emp_role"),
                        employeeAddress);
                employee.setId(Integer.parseInt(selectedEmployee.getId()));
                workWithDB.closeConnection();
                return employee;
            } catch (SQLException e) {
                System.out.println("Nepodarili sa ziskat udaje o zamestnancovi");
                e.printStackTrace();
                workWithDB.closeConnection();
                return null;
            }
        }
        workWithDB.closeConnection();
        System.out.println("Treba vybrat zamestnanca");
        return null;
    }

    public void sumSalaryByRole(){
        try {
            workWithDB.connectToDatabase(); // nadviaze spojenie s db a vytvori tabulky ak nie su vytvorene
            // prvy netrivialny dopyt. Vypocita sucet vsetkych zamestnancov hotela podla ich role a zoradi to od tej role,
            // ktora zaraba najviac. Tiez sa vypise pocet zamestnancov, ktory vykonavaju tuto rolu
            String query = "SELECT emp_role, count(id) AS count_emp, sum(salary) AS summary FROM Employee WHERE hotel_id=" + hotel.getId() + " GROUP BY emp_role ORDER BY summary DESC;";
            workWithDB.setResultSet(workWithDB.getStatement().executeQuery(query));
            observableListSum.clear();

            while (workWithDB.getResultSet().next()) {
                // pridanie jedneho zaznamu do listu, ktory sa potom zobrazi v tableView, udaje do neho taham z vysledku, ktory som dostal pomocou predosleho query
                observableListSum.add(new RoleSum(workWithDB.getResultSet().getString("emp_role"),
                                      workWithDB.getResultSet().getString("count_emp"),
                                      workWithDB.getResultSet().getString("summary")));
            }
            workWithDB.closeConnection();

            tableSumSalaryByRole.setItems(observableListSum); // zobrazenie tabulky s vyfiltrovanymi udajmi o sum salary by role
            tableSumSalaryByRole.setVisible(true); // zobrazi tabulku s udajmi z databazy
            employeeTableButton.setVisible(true); // zobrazi button, pomocou ktoreho je mozne prepnut na predoslu tabulku
            tableView.setVisible(false); // schova predoslu tabulku
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean checkIfDoubleInTextField(){
        try {
            Double.parseDouble(textFieldSalary.getText());
        } catch (NullPointerException | NumberFormatException e1) {
            return false;
        }

        return true;
    }

    public void filterByRoleSalary(){
        try {
            String query = "SELECT id, first_name, last_name, salary, emp_role" +
                           " FROM Employee WHERE " +
                           "hotel_id=" + hotel.getId() + " AND emp_role='" + chosenRole + "' AND salary=" + Double.parseDouble(textFieldSalary.getText()) + ";";
            workWithDB.setResultSet(workWithDB.getStatement().executeQuery(query)); // query vyfiltruje podla zadanych udajov pouzivatela
            addToObservableListAndDisplay(workWithDB.getResultSet()); // zobrazi udaje v tableView
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void filterByRole(){
        try {
            String query = "SELECT id, first_name, last_name, salary, emp_role FROM Employee WHERE hotel_id=" + hotel.getId() +" AND emp_role='"+chosenRole+"';";
            workWithDB.setResultSet(workWithDB.getStatement().executeQuery(query)); // vyfiltruje udaje podla role
            addToObservableListAndDisplay(workWithDB.getResultSet());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void filterBySalary(){
        try {
            String query = "SELECT id, first_name, last_name, salary, emp_role" +
                    " FROM Employee WHERE hotel_id=" + hotel.getId() +" AND salary=" + Double.parseDouble(textFieldSalary.getText()) +";";
            workWithDB.setResultSet(workWithDB.getStatement().executeQuery(query)); // vyfiltruje udaje podla salary
            addToObservableListAndDisplay(workWithDB.getResultSet()); // zobrazi udaje v tableView
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void filterData(){
        workWithDB.connectToDatabase(); // nadviaze spojenie s db a vytvori tabulky ak nie su vytvorene
        if ((!chosenRole.equals("none") && !chosenRole.equals("Role")) && !(textFieldSalary.getText().equals(""))) {
            if (checkIfDoubleInTextField())
                filterByRoleSalary();
            else {
                System.out.println("Nebolo mozne vyfiltrovat podla salary.");
                filterByRole();
            }
        } else if (!chosenRole.equals("none") && !chosenRole.equals("Role")) {
            filterByRole();
        } else if (!(textFieldSalary.getText().equals(""))) {
           if (checkIfDoubleInTextField())
               filterBySalary();
           else
               System.out.println("Nebolo mozne vyfiltrovat podla salary.");
        } else
            displayDataInTableView();
        workWithDB.closeConnection();
    }

    public void deleteSelectedEmployee(){
        workWithDB.connectToDatabase(); // nadviaze spojenie s db a vytvori tabulky ak nie su vytvorene

        if (!tableView.isVisible())
            displayDataInTableView();
        if (tableView.getSelectionModel().getSelectedItem() != null) { // je vybrany zamestnance, takze mozem vymazat jeho zaznam
            try {
                // treba spravit transakciu
                EmployeeTable selectedEmployee = tableView.getSelectionModel().getSelectedItem();
                hotel.setNumOfEmployees(hotel.getNumOfEmployees() - 1); // idem zmazat udaj o zamestnancovi, takze musim znizit pocet zamestnancov hotela
                workWithDB.getConn().setAutoCommit(false);

                String query = "UPDATE Hotel SET num_of_employees=? WHERE id=?;";
                PreparedStatement preparedStatement = workWithDB.getConn().prepareStatement(query);
                preparedStatement.setInt(1, hotel.getNumOfEmployees());
                preparedStatement.setInt(2, hotel.getId());
                preparedStatement.executeUpdate();

                query = "DELETE FROM Employee WHERE id=" + selectedEmployee.getId() + ";"; // tato query vymaze iba zaznam o vybranom zamestnancovi
                workWithDB.getStatement().executeUpdate(query);
                workWithDB.getConn().commit();
                workWithDB.getConn().setAutoCommit(true);
                tableView.getItems().remove(selectedEmployee); // vymaze zaznam o zamestnancovi z TableView
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("Treba vybrat zamestnanca");
        }
        workWithDB.closeConnection();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // kazdej column nadstavi hodnotu podla toho ako sa nazyva privatna variable v classe EmployeeTable
        columnId.setCellValueFactory(new PropertyValueFactory<>("id"));
        columnFirstName.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        columnLastName.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        columnSalary.setCellValueFactory(new PropertyValueFactory<>("salary"));
        columnRole.setCellValueFactory(new PropertyValueFactory<>("role"));
        // pripravi druhu tabulku, ktora sa ukaze po kliknuti na button Sum Salary by Role
        columnRoleSalary.setCellValueFactory(new PropertyValueFactory<>("empRole"));
        columnSumSalary.setCellValueFactory(new PropertyValueFactory<>("sumSalary"));
        columnTotalEmployeesSalary.setCellValueFactory(new PropertyValueFactory<>("countEmp"));

        workWithDB.connectToDatabase(); // nadviaze spojenie s db a vytvori tabulky ak nie su vytvorene
        displayDataInTableView(); // metoda zobrazi udaje z databazy do tabulky
        workWithDB.closeConnection();

        upadateMenuButtonItems(); // prida vsetky role, ktore su v databaze do MenuButton ako itemy

        backButton.setOnAction(actionEvent -> {
            try {
                switchScene.switchToManageChosenHotel(actionEvent, hotel);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        addEmployeeButton.setOnAction(actionEvent -> {
            try {
                switchScene.switchToAddEmployeesHotel(actionEvent, hotel);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        // po kliknuti na tlacitko Detail Info sa prepne scena, kde sa zobrazia vsetky udaje zamestnanca a pouzivatel ich bude moct menit
        detailInfoButton.setOnAction(actionEvent -> {
            Employee employee = chosenEmployee();
            if (employee != null) {
                try {
                    switchScene.switchToEmployeeDetailInfo(actionEvent, hotel, employee);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        // funkcia pouzije sql command GROUP BY a zisti celkovy pocet salary pre jednotlive role zamestnancov v danom hoteli
        sumSalaryByRoleButton.setOnAction(actionEvent -> {
            sumSalaryByRole();
        });

        roleFilterButton.setOnAction(actionEvent -> {
            filterData();
        });

        deleteEmployeeButton.setOnAction(actionEvent -> {
            deleteSelectedEmployee();
        });

        employeeTableButton.setOnAction(actionEvent -> {
            workWithDB.connectToDatabase();
            displayDataInTableView();
            workWithDB.closeConnection();
        });
    }
}
