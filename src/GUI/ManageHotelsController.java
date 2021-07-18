package GUI;

import Database.Address;
import Database.Hotel;
import Database.WorkWithDB;
import TableModels.HotelTable;
import TableModels.MostPayEmployeeTable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;

import java.io.IOException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ResourceBundle;

public class ManageHotelsController implements Initializable {
    @FXML
    private SwitchScene switchScene = new SwitchScene();
    @FXML
    private TableView<HotelTable> tableView; // v tabulke budu iba udaje z classy EmployeeTable
    @FXML
    private TableView<MostPayEmployeeTable> tableViewMostPayEmployee;
    @FXML
    private TableColumn <MostPayEmployeeTable, String> columnEmpId, columnHotelName, columnEmpFirstName, columnEmpLastName, columnEmpRole, columnEmpSalary;
    @FXML
    private TableColumn <HotelTable, String> columnId, columnName, columnCity, columnStreet, columnHouseNum, columnPostalCode, columnCountry, columnPhone;
    @FXML
    private Button hotelNameFilterButton, showMostPayButton, manageHotelButton, deleteHotelButton, bookNightsButton, backButton;
    @FXML
    private TextField textFieldHotelName;
    @FXML
    private ImageView backImageView;

    private WorkWithDB workWithDB = new WorkWithDB();
    private ObservableList<MostPayEmployeeTable> observableListMostEmp = FXCollections.observableArrayList();
    private ObservableList<HotelTable> observableList = FXCollections.observableArrayList(); // vytvorenie listu, ktory prida udaje do tabulky

    public void displayDataInTableView(){
        tableView.setVisible(true);
        observableList.clear();
        tableViewMostPayEmployee.setVisible(false);
        try {
            workWithDB.connectToDatabase();
            String query = "SELECT hotel.id, name, phone, address_id, city, street, house_num, postal_code, country " +
                           "FROM hotel LEFT JOIN address ON hotel.address_id = address.id";
            workWithDB.setResultSet(workWithDB.getStatement().executeQuery(query));
            while (workWithDB.getResultSet().next()) {
                HotelTable hotelTable = new HotelTable(workWithDB.getResultSet().getString("hotel.id"),
                        workWithDB.getResultSet().getString("name"),
                        workWithDB.getResultSet().getString("phone"));
                hotelTable.setCity(workWithDB.getResultSet().getString("city"));
                hotelTable.setStreet(workWithDB.getResultSet().getString("street"));
                hotelTable.setHouseNumber(workWithDB.getResultSet().getString("house_num"));
                hotelTable.setPostalCode(workWithDB.getResultSet().getString("postal_code"));
                hotelTable.setCountry(workWithDB.getResultSet().getString("country"));
                observableList.add(hotelTable);
            }
            workWithDB.closeConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public Address getHotelAddress(int address_id){
        try {
            String query = "SELECT city, street, house_num, postal_code, country FROM address WHERE id=" + Integer.toString(address_id) + ";";
            workWithDB.setResultSet(workWithDB.getStatement().executeQuery(query)); // query vyberie udaje pre dany hotel
            workWithDB.getResultSet().next();
            // vytvori sa objekt pre adresu, ktory metoda vrati
            Address address = new Address(workWithDB.getResultSet().getString("city"),
                                          workWithDB.getResultSet().getString("street"),
                                          workWithDB.getResultSet().getString("house_num"),
                                          workWithDB.getResultSet().getString("postal_code"),
                                          workWithDB.getResultSet().getString("country"));
            address.setId(address_id);
            return address;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public Hotel getSelectedHotel(){
        if (!tableView.isVisible())
            displayDataInTableView();
        if (tableView.getSelectionModel().getSelectedItem() != null) { // podmienka, ci je selectnuty nejaky riadok v tabulke
            try {
                workWithDB.connectToDatabase(); // nadviaze spojenie s db a vytvori tabulky ak nie su vytvorene
                HotelTable selectedHotel = tableView.getSelectionModel().getSelectedItem(); // nacita selectnuty udaj do objektu

                String query = "SELECT * FROM hotel WHERE id=" + selectedHotel.getId() + ";"; // vytiahnem vsetky udaje pre selectnuty hotel v tabulke

                workWithDB.setResultSet(workWithDB.getStatement().executeQuery(query));
                workWithDB.getResultSet().next();

                Hotel hotel = new Hotel(workWithDB.getResultSet().getString("name"),
                                  workWithDB.getResultSet().getString("phone"),
                                  workWithDB.getResultSet().getString("mail"),
                                  workWithDB.getResultSet().getDouble("total_sales"),
                                  workWithDB.getResultSet().getInt("num_of_rooms"),
                                  workWithDB.getResultSet().getInt("num_of_employees"),
                                  getHotelAddress(workWithDB.getResultSet().getInt("address_id")));
                hotel.setId(Integer.parseInt(selectedHotel.getId()));
                workWithDB.closeConnection();
                return hotel;
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        System.out.println("Treba vybrat hotel alebo doslo k inej chybe.");
        return null;
    }
    //
    public void filterHotelsByName() {
        observableList.clear();

        if (!tableView.isVisible())
            displayDataInTableView();

        if (textFieldHotelName.getText().equals(""))
            displayDataInTableView();
        else {
            try {
                workWithDB.connectToDatabase(); // nadviaze spojenie s db a vytvori tabulky ak nie su vytvorene
                String query = "SELECT id, name, phone, address_id FROM hotel WHERE name='" + textFieldHotelName.getText() + "';";
                workWithDB.setResultSet(workWithDB.getStatement().executeQuery(query)); // query, ktore vytiahne iba potrebne udaje pre hotely z databazy podla vybraneho mena
                // pokial nie je koniec udajov, ktore boli nacitane do ResultSet pomocou query, tak bude pokracovat v cykle
                Statement statement = workWithDB.getConn().createStatement(); // vytvorenie dalsieho statementu, ktory bude treba na vytiahnutie udajov o adrese hotela z databazy
                ResultSet resultSet = null; // budem potrebovat dalsi result set, aby som si neprepisoval data, ktore boli vytiahnute


                while (workWithDB.getResultSet().next()) {
                    // vytvorim objekt pre classu HotelTable, ktoru budem vkladat do listu, ktoreho udaje zobrazim v TableView
                    HotelTable hotelTable = new HotelTable(workWithDB.getResultSet().getString("id"),
                            workWithDB.getResultSet().getString("name"),
                            workWithDB.getResultSet().getString("phone"));
                    // toto query vytiahne adresne udaje o aktualnom hoteli, vdaka address_id
                    String addressquery = "SELECT city, street, house_num, postal_code, country FROM address WHERE id=" +
                            workWithDB.getResultSet().getString("address_id") + ";";

                    resultSet = statement.executeQuery(addressquery); // vykonanie query, ktore vytiahne z databazy adresu hotela vdaka jeho address_id
                    resultSet.next(); // potrebne vykonat predtym ako stade zoberiem tej jeden nacitany zaznam
                    hotelTable.setCity(resultSet.getString("city"));
                    hotelTable.setStreet(resultSet.getString("street"));
                    hotelTable.setHouseNumber(resultSet.getString("house_num"));
                    hotelTable.setPostalCode(resultSet.getString("postal_code"));
                    hotelTable.setCountry(resultSet.getString("country"));
                    observableList.add(hotelTable); // pridanie zaznamu o hoteli do list, ktory jeho udaje zobrazi v TableView
                }
                workWithDB.closeConnection();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public String getAddressId(String hotelId){
        try {
            String query = "SELECT address_id FROM Hotel WHERE id=" + hotelId + ";";
            workWithDB.setResultSet(workWithDB.getStatement().executeQuery(query));
            workWithDB.getResultSet().next();
            return workWithDB.getResultSet().getString("address_id");
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return "0";
    }

    public void deleteSelectedHotel(){
        if (!tableView.isVisible())
            displayDataInTableView();
        if (tableView.getSelectionModel().getSelectedItem() != null) { // podmienka, ci je selectnuty nejaky riadok v tabulke
            try {
                workWithDB.connectToDatabase(); // nadviaze spojenie s db a vytvori tabulky ak nie su vytvorene

                HotelTable selectedHotel = tableView.getSelectionModel().getSelectedItem(); // nacita selectnuty udaj do objektu

                String query = "DELETE FROM Room WHERE hotel_id=" + selectedHotel.getId() + ";"; // vymaze zaznamy o izbach pre dany hotel
                workWithDB.getStatement().executeUpdate(query);
                query = "DELETE FROM Customer WHERE hotel_id=" + selectedHotel.getId() + ";"; // vymaze zaznamy o zakaznikoch pre dany hotel
                workWithDB.getStatement().executeUpdate(query);
                query = "DELETE FROM Employee WHERE hotel_id=" + selectedHotel.getId() + ";"; // vymaze zaznamy o zamestnancoch pre dany hotel
                workWithDB.getStatement().executeUpdate(query);

                String addressIdFromHotel = getAddressId(selectedHotel.getId()); // potrebujem dostat ID pre adresu, na ktorej sa nachadza hotel a az potom ho mozem vymazat
                query = "DELETE FROM Hotel WHERE id=" + selectedHotel.getId() + ";"; // najprv musim vymazat zaznam o hoteli, pretoze atribut address_id je NOT NULL
                workWithDB.getStatement().executeUpdate(query);
                query = "DELETE FROM Address WHERE id=" + addressIdFromHotel + ";"; // ked som vymazal zaznam o hoteli, tak mozem vymazat adress na ktorej bol postaveny
                workWithDB.getStatement().executeUpdate(query);
                tableView.getItems().remove(selectedHotel); // vymaze zaznam o hoteli z TableView

                workWithDB.closeConnection();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else
            System.out.println("Treba vybrat hotel.");
    }

    public void showMostPayEmployees(){
        if (!tableViewMostPayEmployee.isVisible()){
            tableView.setVisible(false);
            observableListMostEmp.clear();
            tableViewMostPayEmployee.setVisible(true);
            try {
                workWithDB.connectToDatabase();

                String query = "SELECT distinct * FROM (" +
                                 " SELECT h.id, h.name, e.first_name, e.last_name, e.emp_role, e.salary, dense_rank() " +
                                 " OVER ( partition by h.name order by e.salary desc ) as 'rank' " +
                                 " FROM hotel h " +
                                 " JOIN employee e ON e.hotel_id = h.id " +
                                ") tmp where tmp.rank = 1 ORDER BY 2";

                workWithDB.setResultSet(workWithDB.getStatement().executeQuery(query));

                while (workWithDB.getResultSet().next()) {
                    MostPayEmployeeTable mostPayEmployeeTable = new MostPayEmployeeTable(
                            workWithDB.getResultSet().getString("tmp.id"),
                            workWithDB.getResultSet().getString("tmp.name"),
                            workWithDB.getResultSet().getString("tmp.first_name"),
                            workWithDB.getResultSet().getString("tmp.last_name"),
                            workWithDB.getResultSet().getString("tmp.emp_role"),
                            workWithDB.getResultSet().getString("tmp.salary"));
                    observableListMostEmp.add(mostPayEmployeeTable);
                }
                workWithDB.closeConnection();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        columnId.setCellValueFactory(new PropertyValueFactory<>("id"));
        columnName.setCellValueFactory(new PropertyValueFactory<>("name"));
        columnCity.setCellValueFactory(new PropertyValueFactory<>("city"));
        columnStreet.setCellValueFactory(new PropertyValueFactory<>("street"));
        columnHouseNum.setCellValueFactory(new PropertyValueFactory<>("houseNumber"));
        columnPostalCode.setCellValueFactory(new PropertyValueFactory<>("postalCode"));
        columnCountry.setCellValueFactory(new PropertyValueFactory<>("country"));
        columnPhone.setCellValueFactory(new PropertyValueFactory<>("phone"));
        tableView.setItems(observableList); // zobrazenie v tabulke, ktora sa nachadza v scene, ktoru ovlada tento controllers

        columnEmpId.setCellValueFactory(new PropertyValueFactory<>("id"));
        columnHotelName.setCellValueFactory(new PropertyValueFactory<>("name"));
        columnEmpFirstName.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        columnEmpLastName.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        columnEmpRole.setCellValueFactory(new PropertyValueFactory<>("employeeRole"));
        columnEmpSalary.setCellValueFactory(new PropertyValueFactory<>("salary"));
        tableViewMostPayEmployee.setItems(observableListMostEmp);

        displayDataInTableView(); // metoda zobrazi udaje z databazy do tabulky

        // ked som klikol na tu sipku, tak mi neslo prepnut na minulu scenu, treba to vyriesit takto
        backImageView.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            backButton.fire(); // SHOTS SHOTS SHOTS, invoke backButton operation
        });
        // ked klikne button, tak sa vrati na Homepage
        backButton.setOnAction(actionEvent -> {
            try {
                switchScene.switchToHomepageScene(actionEvent);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        // pre vybrany hotel z tabulky sa budu v dalsich scenach zobrazovat udaje, ktore mu patria
        manageHotelButton.setOnAction(actionEvent -> {
            Hotel hotel = getSelectedHotel();
            if (hotel != null) {
                try {
                    switchScene.switchToManageChosenHotel(actionEvent, hotel);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        // po kliknuti na button Book Nights sa zmeni scena, kde bude moct pouzivatel zabookovat izby pre zakaznikov
        bookNightsButton.setOnAction(actionEvent ->{
            Hotel hotel = getSelectedHotel();
            if (hotel != null){
                try {
                    switchScene.switchToBookNights(actionEvent, hotel);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        showMostPayButton.setOnAction(actionEvent ->{
            showMostPayEmployees();
        });
        // po kliknuti na button filtrovania sa zobrazia udaje v tabulke podla zadaneho mena v texfielde textFieldHotelName
        hotelNameFilterButton.setOnAction(actionEvent -> {
            filterHotelsByName();
        });
        // vymaze udaje o vybranom hoteli z DB, vratane udajov o zamestnancoch, izbach a zakaznikoch
        deleteHotelButton.setOnAction(actionEvent -> {
            deleteSelectedHotel();
        });
    }
}
