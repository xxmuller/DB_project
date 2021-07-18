package GUI;

import Database.Hotel;
import Database.WorkWithDB;
import TableModels.EmployeeTable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class AddRoomsController implements Initializable { // potrebne implementnut Initializable
    @FXML
    private SwitchScene switchScene = new SwitchScene();
    @FXML
    private TableView <EmployeeTable> tableView; // v tabulke budu iba udaje z classy EmployeeTable
    @FXML
    private TableColumn <EmployeeTable, String> columnId; // podla toho aky datovy typ je Id v classe EmployeeTable som vytvoril <EmployeeTable, String>
    @FXML
    private TableColumn <EmployeeTable, String> columnFirstName; // podla toho aky datovy typ je firstName v classe EmployeeTable som vytvoril <EmployeeTable, String>
    @FXML
    private TableColumn <EmployeeTable, String> columnLastName; // podla toho aky datovy typ je lastName v classe EmployeeTable som vytvoril <EmployeeTable, String>
    @FXML
    private TableColumn <EmployeeTable, String> columnSalary; // podla toho aky datovy typ je salary v classe EmployeeTable som vytvoril <EmployeeTable, String>
    @FXML
    private TableColumn <EmployeeTable, String> columnRole; // podla toho aky datovy typ je role v classe EmployeeTable som vytvoril <EmployeeTable, String>
    @FXML
    private Button backButton, addRoomButton, beginButton;
    @FXML
    private TextField tfRoomNumber, tfNumberOfBeds, tfPriceForOneNight;

    private Hotel hotel;
    private WorkWithDB workWithDB = new WorkWithDB();
    private ObservableList<EmployeeTable> observableList = FXCollections.observableArrayList(); // vytvorenie listu, ktory prida udaje do tabulky

    public AddRoomsController(Hotel hotel){
        this.hotel = hotel;
    }

    // zakazdym nastavi skonfiguruje scenu po pridani izby
    public void configureAfterAdd() {
        if (hotel.getNumOfRooms() > 1) {
            addRoomButton.setText("Add next (" + (hotel.getNumOfRooms() - 1)  + ")");
        } else {
            addRoomButton.setText("End");
        }
        tfRoomNumber.setText("");
        tfNumberOfBeds.setText("");
        tfPriceForOneNight.setText("");
    }

    public void displayDataInTableView(){ // v tabulke sa zobrazia udaje zamestnancov, ktory pracuju v hoteli, ktory sme pridali
        try {
            String query = "SELECT id, first_name, last_name, salary, emp_role " +
                    "FROM employee " +
                    "WHERE hotel_id=" + hotel.getId() + ";";
            workWithDB.setResultSet(workWithDB.getStatement().executeQuery(query)); // query, ktore vytiahne iba potrebne udaje do tabulky zamestnancov a vytiahne iba zamestnancov, ktory pracuju v tomto hoteli
            // pokial nie je koniec udajov, ktore boli nacitane do ResultSet pomocou query, tak bude pokracovat v cykle
            while (workWithDB.getResultSet().next()){
                // pridanie jedneho zaznamu do listu, ktory sa potom zobrazi v tableView, udaje do neho taham z vysledku, ktory som dostal pomocou predosleho query
                observableList.add(new EmployeeTable(workWithDB.getResultSet().getString("id"),
                                                     workWithDB.getResultSet().getString("first_name"),
                                                     workWithDB.getResultSet().getString("last_name"),
                                                     workWithDB.getResultSet().getString("salary"),
                                                     workWithDB.getResultSet().getString("emp_role")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        tableView.setItems(observableList); // zobrazenie v tabulke, ktora sa nachadza v scene, ktoru ovlada tento controller
    }

    public void backButtonClicked(ActionEvent actionEvent) throws IOException {
        switchScene.switchToAddEmployeesScene(actionEvent, this.hotel);
    }

    public boolean isValidFloat(String sales){
        try {
            Float.parseFloat(sales);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public boolean isValidInt(String number){
        try {
            Integer.parseInt(number);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public boolean insertRoomInfo(int selectedEmployeeId){
        workWithDB.connectToDatabase(); // nadviaze spojenie s db a vytvori tabulky ak nie su vytvorene

        // podmienka zisti, ci su vyplnene udaje, ktore su potrebne na pridanie zaznamu o izbe do databazy
        if (!(tfRoomNumber.getText().equals("")) && !(tfNumberOfBeds.getText().equals("")) && !(tfPriceForOneNight.getText().equals(""))) {
            if (isValidInt(tfNumberOfBeds.getText()) && isValidFloat(tfPriceForOneNight.getText()) && isValidInt(tfRoomNumber.getText())) {
                try {
                    String query = "INSERT INTO Room (room_num, number_of_beds, price_for_night, hotel_id, employee_id) " +
                            "VALUES (" + Integer.parseInt(tfRoomNumber.getText()) + ", " + Integer.parseInt(tfNumberOfBeds.getText()) +
                            ", " + Double.parseDouble(tfPriceForOneNight.getText()) + ", " + hotel.getId() + ", " + selectedEmployeeId + ");";
                    // query, ktore prida zaznam izby do databazy, hotel_id bude hotela, ktory sme pridali a employee_id bude id vybraneho zamestnanca, ktory pracuje pre pridany hotel
                    workWithDB.getStatement().executeUpdate(query);
                    workWithDB.closeConnection();
                    return true;
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            } else
                System.out.println("Skontroluj cisla v Number of Beds a Price for Night.");
        }
        workWithDB.closeConnection();
        return false;
    }

    public boolean checkIfRowSelected(){
        if (tableView.getSelectionModel().getSelectedItem() != null){ // podmienka, ci je selectnuty nejaky riadok v tabulke
            EmployeeTable selectedEmployee = tableView.getSelectionModel().getSelectedItem(); // nacita selectnuty udaj to objektu
            return insertRoomInfo(Integer.parseInt(selectedEmployee.getId())); // ide insertovat info o izbe do tabulky
        } else
            System.out.println("Treba vybrat zamestnanca, ktory sa bude starat o izbu.");

        return false;
    }

    public void beginButtonClicked(ActionEvent actionEvent) throws  IOException {
        switchScene.switchToHomepageScene(actionEvent);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) { // ked das implement Initializable, tak musi overridnut tuto metodu
        // kazdej column nadstavi hodnotu podla toho ako sa nazyva privatna variable v classe EmployeeTable
        columnId.setCellValueFactory(new PropertyValueFactory<>("id"));
        columnFirstName.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        columnLastName.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        columnSalary.setCellValueFactory(new PropertyValueFactory<>("salary"));
        columnRole.setCellValueFactory(new PropertyValueFactory<>("role"));

        workWithDB.connectToDatabase(); // nadviaze spojenie s db a vytvori tabulky ak nie su vytvorene
        displayDataInTableView(); // metoda zobrazi udaje z databazy do tabulky
        workWithDB.closeConnection();

        configureAfterAdd(); // skonfiguruje scenu
        // pre vsetky eventy, ktore budu prebiehat treba nadstavit EventHandler alebo lambu, pretoze v FXML nie je specifikovany fx:controler
        backButton.setOnAction(actionEvent -> { // ked kliknem na button, tak sa vratim spat na AddEmployeesScenu
            try {
                backButtonClicked(actionEvent);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        // ked kliknem na button, tak sa prida izba do tabulky v databaze a mozem pridavat dalej
        addRoomButton.setOnAction(actionEvent -> {
            // zisti, ci je selectnuty udaj z tabulky a ci su vyplnene udaje pre izbu, ktoru chcem pridat a ak to je mozne, tak to spravi
            if (checkIfRowSelected()) {
                hotel.setNumOfRooms(hotel.getNumOfRooms() - 1); // pridala sa izba, takze uz moze pouzivatel pridat v tomto hoteli iba o jednu menej

                if(hotel.getNumOfRooms() > 0)
                    configureAfterAdd();
                else {
                    try {
                        beginButtonClicked(actionEvent);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            } else
                System.out.println("Izba nebola pridana.");
        });

        // vrati na zaciatok
        beginButton.setOnAction(actionEvent -> {
            try {
                beginButtonClicked(actionEvent);
            } catch (IOException e){
                e.printStackTrace();
            }
        });
    }
}
