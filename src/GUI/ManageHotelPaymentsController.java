package GUI;

import Database.Address;
import Database.Customer;
import Database.Hotel;
import Database.WorkWithDB;
import TableModels.CustomerTable;
import TableModels.EmployeeTable;
import TableModels.PaymentTable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

import java.io.IOException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ResourceBundle;

public class ManageHotelPaymentsController implements Initializable {
    private ObservableList<PaymentTable> observableListPayments = FXCollections.observableArrayList();
    private SwitchScene switchScene = new SwitchScene();
    private WorkWithDB workWithDB = new WorkWithDB();
    private Hotel hotel;

    @FXML
    private TableView <PaymentTable> tableView; // v tabulke budu iba udaje z classy PaymentTable
    @FXML
    private TableColumn<PaymentTable, String> columnId;
    @FXML
    private TableColumn <PaymentTable, String> columnBillNum;
    @FXML
    private TableColumn <PaymentTable, String> columnPrice;
    @FXML
    private TableColumn <PaymentTable, String> columnPaymentTime;
    @FXML
    private TableColumn <PaymentTable, String> columnPaymentType;
    @FXML
    private TableColumn <PaymentTable, String> columnCustomerIdCard;
    @FXML
    private Button backButton, showCustomerButton, customerFilterButton;
    @FXML
    private TextField textFieldCustomerIdCard;

    public ManageHotelPaymentsController(Hotel hotel){
        this.hotel = hotel;
    }

    public void displayDataInTableView(){
        try {
            // vytiahnem z databazy zakaznikov, ktoro platili vo vybranom hoteli
            workWithDB.connectToDatabase();
            String query = "SELECT id, id_card_num FROM Customer WHERE hotel_id='" + hotel.getId() + "';";
            workWithDB.setResultSet(workWithDB.getStatement().executeQuery(query));
            // potrebujem vykonat dalsiu query, podla zaznamu, ktory bol vytiahnuty z predoslej, preto vytvorim novy ResultSet a Statement
            ResultSet resultSet = null;
            Statement statement = workWithDB.getConn().createStatement();
            // vytiahnem vsetky zaznamy o platbach, ktore sa vykonali v tomto hoteli
            while (workWithDB.getResultSet().next()) {
                // tato query vytiahne vsetky platby zakaznika vo vybranom hoteli
                query = "SELECT * FROM payment WHERE customer_id=" + workWithDB.getResultSet().getInt("id") + ";";
                resultSet = statement.executeQuery(query);
                while (resultSet.next()) {
                    // pridanie jedneho zaznamu do listu, ktory sa potom zobrazi v tableView, udaje do neho taham z vysledku, ktory som dostal pomocou predosleho query
                    observableListPayments.add(new PaymentTable(resultSet.getString("id"),
                            resultSet.getString("bill_num"),
                            resultSet.getString("price"),
                            resultSet.getString("payment_time"),
                            resultSet.getString("payment_type"),
                            workWithDB.getResultSet().getString("id_card_num"),
                            workWithDB.getResultSet().getString("id")));
                }
            }
            tableView.setItems(observableListPayments); // zobrazenie platieb v hoteli do tabulky
            workWithDB.closeConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void filterPaymentsByCardID(){
        if (textFieldCustomerIdCard.getText().equals(""))
            displayDataInTableView();
        else {
            try {
                workWithDB.connectToDatabase();
                // potrebujem zisti id zakaznika, ktory ma zadanu Card ID a platil v tomto hoteli
                String query = "SELECT id FROM Customer WHERE id_card_num='" +
                        Integer.parseInt(textFieldCustomerIdCard.getText()) + "' AND hotel_id='" + hotel.getId() + "';";
                workWithDB.setResultSet(workWithDB.getStatement().executeQuery(query));
                // ziskam ID zakaznika, ktory ma Card ID, ktory pouzivatel zadal do vyhladavania
                int customerId = 0;
                if (workWithDB.getResultSet().next())
                    customerId = workWithDB.getResultSet().getInt("id");
                // touto query nacitam vsetky platby, ktore vykonal zakaznik vyfiltrovany podla zadanej Card ID v tomto hoteli
                query = "SELECT * FROM Payment WHERE customer_id='" + customerId + "';";
                workWithDB.setResultSet(workWithDB.getStatement().executeQuery(query));
                // vycistim observableListPayments a naplnim ho udajmi o platbach, ktore nasledne zobrazim v tableView
                observableListPayments.clear();
                while (workWithDB.getResultSet().next()) {
                    observableListPayments.add(new PaymentTable(workWithDB.getResultSet().getString("id"),
                            workWithDB.getResultSet().getString("bill_num"),
                            workWithDB.getResultSet().getString("price"),
                            workWithDB.getResultSet().getString("payment_time"),
                            workWithDB.getResultSet().getString("payment_type"),
                            textFieldCustomerIdCard.getText(),
                            String.valueOf(customerId)));
                }
                tableView.setItems(observableListPayments);
                workWithDB.closeConnection();
            } catch (NumberFormatException e) {
                System.out.println("Card ID musi byt cislo.");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public Address getCustomerAddress(int customerId){
        try {
            String query = "SELECT address_id FROM Customer WHERE id=" + customerId + ";";
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
        try {
            PaymentTable selectedPayment = tableView.getSelectionModel().getSelectedItem();

            Address customerAddress = getCustomerAddress(Integer.parseInt(selectedPayment.getCustomerId())); // najprv zistim adresu zakaznika
            if (customerAddress == null) {
                System.out.println("Nepodarilo sa ziskat adresu zakaznika.");
                workWithDB.closeConnection();
                return null;
            }

            String query = "SELECT * FROM customer WHERE id=" + Integer.parseInt(selectedPayment.getCustomerId()) + ";";
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
            customer.setId(Integer.parseInt(selectedPayment.getCustomerId()));
            workWithDB.closeConnection();
            return customer;
        } catch (SQLException e) {
            System.out.println("Nepodarili sa ziskat udaje o zakaznikovi.");
            e.printStackTrace();
            workWithDB.closeConnection();
            return null;
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // musim inicializovat jednotlive column v tableview
        columnId.setCellValueFactory(new PropertyValueFactory<>("id"));
        columnBillNum.setCellValueFactory(new PropertyValueFactory<>("billNum"));
        columnPrice.setCellValueFactory(new PropertyValueFactory<>("price"));
        columnPaymentTime.setCellValueFactory(new PropertyValueFactory<>("paymentTime"));
        columnPaymentType.setCellValueFactory(new PropertyValueFactory<>("paymentType"));
        columnCustomerIdCard.setCellValueFactory(new PropertyValueFactory<>("customerIdCard"));
        // zobrazi udaje o platbach v tableview
        displayDataInTableView();
        // po kliknuti na button filtracie sa vyfiltruju udaje podla zadanej Card ID
        customerFilterButton.setOnAction(actionEvent -> {
            filterPaymentsByCardID();
        });
        // po kliknuti na button Show Customer sa zobrazia detailne udaje o zakaznikovi, ktory vykonal platbu
        showCustomerButton.setOnAction(actionEvent -> {
            if (tableView.getSelectionModel().getSelectedItem() != null){
                try {
                    Customer customer = chosenCustomer();
                    switchScene.switchToPaymentCustomer(actionEvent, customer, hotel);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else
                System.out.println("Musite vybrat platbu aby ste zobrazili detailne info o zakaznikovi.");
        });

        backButton.setOnAction(actionEvent -> {
            try {
                switchScene.switchToManageChosenHotel(actionEvent, hotel);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }
}
