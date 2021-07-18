package GUI;

import Database.*;
import TableModels.CustomerTable;
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

public class ManageHotelCustomersController implements Initializable {
    public static class PriceSum {
        String customerName;
        Integer roomNum;
        Float priceForNight;
        Integer numOfNights;
        Float priceSum;

        public PriceSum(String customerName, Integer roomNum, Float priceForNight, Integer numOfNights, Float priceSum){
            this.customerName = customerName;
            this.roomNum = roomNum;
            this.priceForNight = priceForNight;
            this.numOfNights = numOfNights;
            this.priceSum = priceSum;
        }

        public String getCustomerName() {
            return customerName;
        }

        public Integer getRoomNum() { return roomNum; }

        public Float getPriceForNight() { return priceForNight; }

        public Integer getNumOfNights() { return numOfNights; }

        public Float getPriceSum() { return priceSum; }
    }

    private ObservableList<CustomerTable> observableList = FXCollections.observableArrayList(); // vytvorenie listu, ktory prida udaje do tabulky
    private ObservableList<PriceSum> observableListSum = FXCollections.observableArrayList(); // vytvorenie listu, ktory prida udaje do tabulky pre spocitavanie cien izieb
    private SwitchScene switchScene = new SwitchScene();
    private WorkWithDB workWithDB = new WorkWithDB();
    private Hotel hotel;

    @FXML
    private TableView<CustomerTable> tableView; // v tabulke budu iba udaje z classy CustomerTable
    @FXML
    private TableColumn<CustomerTable, Integer> columnId; // podla toho aky datovy typ je Id v classe CustomerTable som vytvoril <CustomerTable, Integer>
    @FXML
    private TableColumn <CustomerTable, String> columnFirstName; // podla toho aky datovy typ je firstName v classe CustomerTable som vytvoril <CustomerTable, String>
    @FXML
    private TableColumn <CustomerTable, String> columnLastName; // podla toho aky datovy typ je lastName v classe CustomerTable som vytvoril <CustomerTable, String>
    @FXML
    private TableColumn <CustomerTable, String> columnPhone; // podla toho aky datovy typ je phone v classe CustomerTable som vytvoril <CustomerTable, String>
    @FXML
    private TableColumn <CustomerTable, String> columnMail; // podla toho aky datovy typ je mail v classe CustomerTable som vytvoril <CustomerTable, String>
    @FXML
    private TableColumn <CustomerTable, Integer> columnBookedNights; // podla toho aky datovy typ je bookedNights v classe CustomerTable som vytvoril <CustomerTable, Integer>
    @FXML
    private TableView <PriceSum> tablePriceSumByRoom;
    @FXML
    private TableColumn <PriceSum, String> columnCustomerName;
    @FXML
    private TableColumn <PriceSum, Integer> columnRoomNum;
    @FXML
    private TableColumn <PriceSum, Float> columnPriceForNight;
    @FXML
    private TableColumn <PriceSum, Integer> columnNumOfNights;
    @FXML
    private TableColumn <PriceSum, Float> columnPriceSum;
    @FXML
    private Button backButton, deleteCustomerButton, customerTableButton, detailInfoButton, addCustomerButton, bookedNightsFilterButton;
    @FXML
    private TextField textFieldBookedNights;

    public ManageHotelCustomersController(Hotel hotel){
        this.hotel = hotel;
    }

    public void displayDataInTableView(){ // v tabulke sa zobrazia udaje zakaznikov, ktori su ubytovani v hoteli, ktory sme pridali
        try {
            String query = "SELECT id_card_num, first_name, last_name, phone, mail, booked_nights " +
                    "FROM customer " +
                    "WHERE hotel_id=" + hotel.getId() + ";";
            workWithDB.setResultSet(workWithDB.getStatement().executeQuery(query)); // query, ktore vytiahne iba potrebne udaje do tabulky zakaznikov a vytiahne iba zakaznikov, ktori su ubytovani v tomto hoteli
            // pokial nie je koniec udajov, ktore boli nacitane do ResultSet pomocou query, tak bude pokracovat v cykle
            while (workWithDB.getResultSet().next()){
                // pridanie jedneho zaznamu do listu, ktory sa potom zobrazi v tableView, udaje do neho taham z vysledku, ktory som dostal pomocou predosleho query
                observableList.add(new CustomerTable(workWithDB.getResultSet().getInt("id_card_num"),
                        workWithDB.getResultSet().getString("first_name"),
                        workWithDB.getResultSet().getString("last_name"),
                        workWithDB.getResultSet().getString("phone"),
                        workWithDB.getResultSet().getString("mail"),
                        workWithDB.getResultSet().getInt("booked_nights")));

            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        tableView.setItems(observableList); // zobrazenie v tabulke, ktora sa nachadza v scene, ktoru ovlada tento controller
    }

    public boolean isValidInt(String number){
        try {
            Integer.parseInt(number);
            return true;
        } catch (NumberFormatException e){
            return false;
        }
    }

    public void bookedNightsFilterButtonClicked(){
        workWithDB.connectToDatabase(); // nadviaze spojenie s db a vytvori tabulky ak nie su vytvorene
        if(!textFieldBookedNights.getText().equals("")){ //pokial je zadany pocet rezervovanych noci
            try {
                observableList = FXCollections.observableArrayList();
                String query = "SELECT id_card_num, first_name, last_name, phone, mail, booked_nights " +
                        "FROM customer " +
                        "WHERE booked_nights = '" + Integer.parseInt(textFieldBookedNights.getText()) + "'";
                workWithDB.setResultSet(workWithDB.getStatement().executeQuery(query)); // query, ktore vytiahne iba potrebne udaje do tabulky zakaznikov a vytiahne iba zakaznikov, ktori su ubytovani v tomto hoteli
                // pokial nie je koniec udajov, ktore boli nacitane do ResultSet pomocou query, tak bude pokracovat v cykle
                while (workWithDB.getResultSet().next()) {
                    // pridanie jedneho zaznamu do listu, ktory sa potom zobrazi v tableView, udaje do neho taham z vysledku, ktory som dostal pomocou predosleho query
                    observableList.add(new CustomerTable(workWithDB.getResultSet().getInt("id_card_num"),
                            workWithDB.getResultSet().getString("first_name"),
                            workWithDB.getResultSet().getString("last_name"),
                            workWithDB.getResultSet().getString("phone"),
                            workWithDB.getResultSet().getString("mail"),
                            workWithDB.getResultSet().getInt("booked_nights")));
                }
            } catch (NumberFormatException e) {
                System.out.println("Booked nights musi byt cislo.");
            } catch (SQLException e) {
                e.printStackTrace();
            }
            tableView.setItems(observableList); // zobrazenie v tabulke, ktora sa nachadza v scene, ktoru ovlada tento controller
        } else { // ak nie je zadany pocet noci na filtrovanie, zobrazi vsetkych zakaznikov
            observableList = FXCollections.observableArrayList();
            displayDataInTableView();
        }
        workWithDB.closeConnection();
    }

    public void deleteSelectedCustomer(){
        workWithDB.connectToDatabase(); // nadviaze spojenie s db a vytvori tabulky ak nie su vytvorene
        if (tableView.getSelectionModel().getSelectedItem() != null) { // je vybrany zakaznik, takze mozem vymazat jeho zaznam
            try {
                CustomerTable selectedCustomer = tableView.getSelectionModel().getSelectedItem();
                String query = "DELETE FROM Customer WHERE id_card_num =" + selectedCustomer.getId() + ";"; // tato query vymaze iba zaznam o vybranom zakaznikovi

                workWithDB.getStatement().executeUpdate(query);
                tableView.getItems().remove(selectedCustomer); // vymaze zaznam o zakaznikovi z TableView
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("Treba vybrat zakaznika");
        }
        workWithDB.closeConnection();
    }

    public void priceSumButtonClicked(){
        try {
            workWithDB.connectToDatabase(); // nadviaze spojenie s db a vytvori tabulky ak nie su vytvorene
            // netrivialny dopyt, ktory zobrazi vsetky izby, ktore su rezervovane aspon na dve noci
            // a zobrazi meno jedneho zakaznika, ktory je v tejto izbe ubytovany
            // tiez sa tu pocita celkova cena ubytovania, teda pocet noci * cena za noc
            String query = "SELECT Customer.last_name, Room.room_num, Room.price_for_night, Customer.booked_nights," +
                    "Customer.booked_nights * Room.price_for_night AS price_sum" +
                    " FROM Customer INNER JOIN Room ON Customer.room_id=Room.room_num AND Customer.hotel_id=Room.hotel_id " +
                    "AND Customer.hotel_id='"+hotel.getId()+"' " +
                    "GROUP BY Customer.room_id HAVING Customer.booked_nights >= 2";

            workWithDB.setResultSet(workWithDB.getStatement().executeQuery(query));
            observableListSum.clear();

            while (workWithDB.getResultSet().next()) {
                // pridanie jedneho zaznamu do listu, ktory sa potom zobrazi v tableView, udaje do neho taham z vysledku, ktory som dostal pomocou predosleho query
                observableListSum.add(new PriceSum(workWithDB.getResultSet().getString("last_name"),
                        workWithDB.getResultSet().getInt("room_num"),
                        workWithDB.getResultSet().getFloat("price_for_night"),
                        workWithDB.getResultSet().getInt("booked_nights"),
                        workWithDB.getResultSet().getFloat("price_sum")));
            }

            tablePriceSumByRoom.setItems(observableListSum);
            tablePriceSumByRoom.setVisible(true);
            customerTableButton.setVisible(true);
            deleteCustomerButton.setVisible(false);
            addCustomerButton.setVisible(false);
            detailInfoButton.setVisible(false);
            tableView.setVisible(false);
            workWithDB.closeConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Address getCustomerAddress(int customerId){
        try {
            String query = "SELECT address_id FROM Customer WHERE id_card_num=" + customerId + ";";
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

    public Integer getRoomNumber(Integer roomId) throws SQLException {
        String query = "SELECT room_num FROM room WHERE id=" + roomId + ";";
        workWithDB.setResultSet(workWithDB.getStatement().executeQuery(query));
        workWithDB.getResultSet().next();

        return workWithDB.getResultSet().getInt("room_num");
    }

    public Customer chosenCustomer(){
        workWithDB.connectToDatabase(); // nadviaze spojenie s db a vytvori tabulky ak nie su vytvorene

        if (!tableView.isVisible()) {
            displayDataInTableView();
            workWithDB.closeConnection();
            return null;
        }

        if (tableView.getSelectionModel().getSelectedItem() != null) { // je vybrany zakaznik
            try {
                CustomerTable selectedCustomer = tableView.getSelectionModel().getSelectedItem();

                Address customerAddress = getCustomerAddress(selectedCustomer.getId()); // najprv zistim adresu zakaznika
                if (customerAddress == null) {
                    System.out.println("Nepodarilo sa ziskat adresu zakaznika.");
                    workWithDB.closeConnection();
                    return null;
                }

                String query = "SELECT * FROM customer WHERE id_card_num=" + selectedCustomer.getId() + ";";
                workWithDB.setResultSet(workWithDB.getStatement().executeQuery(query));
                workWithDB.getResultSet().next();
                Customer customer = new Customer(workWithDB.getResultSet().getInt("id_card_num"),
                        workWithDB.getResultSet().getString("first_name"),
                        workWithDB.getResultSet().getString("last_name"),
                        workWithDB.getResultSet().getString("phone"),
                        workWithDB.getResultSet().getString("mail"),
                        workWithDB.getResultSet().getInt("booked_nights"),
                        getRoomNumber(workWithDB.getResultSet().getInt("room_id")),
                        customerAddress);
                customer.setId(selectedCustomer.getId());
                workWithDB.closeConnection();
                return customer;
            } catch (SQLException e) {
                System.out.println("Nepodarili sa ziskat udaje o zakaznikovi.");
                e.printStackTrace();
                workWithDB.closeConnection();
                return null;
            }
        }

        workWithDB.closeConnection();
        System.out.println("Treba vybrat zakaznika");
        return null;
    }

    public void detailInfoButtonClicked(ActionEvent actionEvent){
        Customer customer = chosenCustomer();
        if (customer != null) {
            try {
                switchScene.switchToCustomerDetailInfo(actionEvent, hotel, customer);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else
            System.out.println("Treba vybrat zakaznika!");
    }

    public void deleteCustomerButtonClicked(){
        workWithDB.connectToDatabase(); // nadviaze spojenie s db a vytvori tabulky ak nie su vytvorene
        if (!tableView.isVisible())
            displayDataInTableView();
        if (tableView.getSelectionModel().getSelectedItem() != null) { // je vybrany zakaznik, takze mozem vymazat jeho zaznam
            try {
                CustomerTable selectedCustomer = tableView.getSelectionModel().getSelectedItem();
                String query = "DELETE FROM Customer WHERE id_card_num=" + selectedCustomer.getId() + ";"; // tato query vymaze iba zaznam o vybranom zakaznikovi
                workWithDB.getStatement().executeUpdate(query);

                tableView.getItems().remove(selectedCustomer); // vymaze zaznam o zamestnancovi z TableView
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("Treba vybrat zakaznika.");
        }
        workWithDB.closeConnection();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // kazdej column nadstavi hodnotu podla toho ako sa nazyva privatna variable v classe CustomerTable
        columnId.setCellValueFactory(new PropertyValueFactory<>("id"));
        columnFirstName.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        columnLastName.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        columnPhone.setCellValueFactory(new PropertyValueFactory<>("phone"));
        columnMail.setCellValueFactory(new PropertyValueFactory<>("mail"));
        columnBookedNights.setCellValueFactory(new PropertyValueFactory<>("bookedNights"));

        columnCustomerName.setCellValueFactory(new PropertyValueFactory<>("customerName"));
        columnRoomNum.setCellValueFactory(new PropertyValueFactory<>("roomNum"));
        columnPriceForNight.setCellValueFactory(new PropertyValueFactory<>("priceForNight"));
        columnNumOfNights.setCellValueFactory(new PropertyValueFactory<>("numOfNights"));
        columnPriceSum.setCellValueFactory(new PropertyValueFactory<>("priceSum"));

        workWithDB.connectToDatabase(); // nadviaze spojenie s db a vytvori tabulky ak nie su vytvorene
        displayDataInTableView(); // metoda zobrazi udaje z databazy do tabulky
        workWithDB.closeConnection();
        customerTableButton.setVisible(false);
        tablePriceSumByRoom.setVisible(false);

        backButton.setOnAction(actionEvent -> {
            try {
                switchScene.switchToManageChosenHotel(actionEvent, hotel);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        deleteCustomerButton.setOnAction(actionEvent -> {
            deleteSelectedCustomer();
        });

        customerTableButton.setOnAction(actionEvent -> {

            tablePriceSumByRoom.setVisible(false);
            customerTableButton.setVisible(false);
            deleteCustomerButton.setVisible(true);
            addCustomerButton.setVisible(true);
            detailInfoButton.setVisible(true);
            tableView.setVisible(true);
            observableList.clear();

            workWithDB.connectToDatabase(); // nadviaze spojenie s db a vytvori tabulky ak nie su vytvorene
            displayDataInTableView();
            workWithDB.closeConnection();
        });

    }
}