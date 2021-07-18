package GUI;

import Database.Hotel;
import ORM.CustomerORM;
import ORM.RoomORM;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class ChooseCustomerController implements Initializable {
    private ObservableList<CustomerORM> observableList = null;
    private SwitchScene switchScene = new SwitchScene();
    private RoomORM roomORM;
    private Hotel hotel;

    @FXML
    private TableView<CustomerORM> tableView; // v tabulke budu iba udaje z classy RoomORM
    @FXML
    private TableColumn<CustomerORM, Integer> columnId; // podla toho aky datovy typ je id v classe CustomerORM som vytvoril <CustomerORM, Integer>
    @FXML
    private TableColumn <CustomerORM, Integer> columnIdCard; // podla toho aky datovy typ je id_card_num v classe CustomerORM som vytvoril <CustomerORM, Integer>
    @FXML
    private TableColumn <CustomerORM, String> columnFirstName; // podla toho aky datovy typ je first_name v classe CustomerORM som vytvoril <CustomerORM, String>
    @FXML
    private TableColumn <CustomerORM, String> columnLastName; // podla toho aky datovy typ je last_name v classe CustomerORM som vytvoril <CustomerORM, String>
    @FXML
    private TableColumn <CustomerORM, String> columnPhone; // podla toho aky datovy typ je phone v classe CustomerORM som vytvoril <CustomerORM, String>
    @FXML
    private TableColumn <CustomerORM, String> columnMail; // podla toho aky datovy typ je mail v classe CustomerORM som vytvoril <CustomerORM, String>
    @FXML
    private Button addCustomerButton, payForBookingButton, idCardFilterButton, backButton;
    @FXML
    private TextField textFieldIdCard;

    public ChooseCustomerController(RoomORM roomORM, Hotel hotel){
        this.roomORM = roomORM;
        this.hotel = hotel;
    }

    public void displayCustomersData(){
        SessionFactory serviceFactory = new Configuration().configure().buildSessionFactory();
        Session session = serviceFactory.openSession();
        try {
            session.beginTransaction(); // vykonanie transakcie
            // dostanem vsetky udaje o zakaznikoch vybraneho hotela, ktore su v databaze
            List<CustomerORM> list = session.createQuery("from customer C where hotel_id='" + hotel.getId() + "'").getResultList();
            observableList = FXCollections.observableArrayList(list); // vytvorenie listu, ktory prida udaje do tabulky
            tableView.setItems(observableList); // zobrazenie v tabulke
        } catch (Exception e) {
            e.printStackTrace();
        }

        session.close();
        serviceFactory.close();
    }

    public boolean checkIfInteger(String number){
        try {
            Integer.parseInt(number);
            return false;
        } catch (NumberFormatException e){
            System.out.println("Pocet izieb musi byt cele cislo.");
            return true;
        }
    }

    public void filterByIdCard(){
        if (textFieldIdCard.getText().equals(""))
            displayCustomersData();
        else {
            if (checkIfInteger(textFieldIdCard.getText())) // ak zadani pocet izieb nebude cislo, tak sa nevykona vyhladavanie
                return;
            try {
                SessionFactory serviceFactory = new Configuration().configure().buildSessionFactory(); // nakonfiguruju sa udaje ohladom ORM z hibernate.cfg.xml
                Session session = serviceFactory.openSession(); // vytvorim session
                session.beginTransaction(); // zacnem transakciu, kde mozem vykonavat pomocou hibernate upravi nad databazou
                // do listu nacitam izby zakaznika s danou ID
                List<CustomerORM> list = session.createQuery("from customer C where C.id_card_num='" +
                                                         Integer.parseInt(textFieldIdCard.getText()) + "'").getResultList();
                observableList = FXCollections.observableArrayList(list); // inicializujem observableList, aby som mohol jeho udaje zobrazit v tableView
                tableView.setItems(observableList); // zobrazim udaje v tableView
                session.close(); // skonci session
                serviceFactory.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public CustomerORM getSelectedCustomer(){
        if (tableView.getSelectionModel().getSelectedItem() != null) // zisti, ci pouzivatel vybral zakaznika, ktory chce zabookovat izbu
            return tableView.getSelectionModel().getSelectedItem();
        else {
            System.out.println("Treba vybrat zakaznika, ktory chce zabookovat izbu.");
            return null;
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // kazdej column nadstavi hodnotu podla toho ako sa nazyva privatna variable v classe CustomerORM
        columnId.setCellValueFactory(new PropertyValueFactory<>("id"));
        columnIdCard.setCellValueFactory(new PropertyValueFactory<>("id_card_num"));
        columnFirstName.setCellValueFactory(new PropertyValueFactory<>("first_name"));
        columnLastName.setCellValueFactory(new PropertyValueFactory<>("last_name"));
        columnPhone.setCellValueFactory(new PropertyValueFactory<>("phone"));
        columnMail.setCellValueFactory(new PropertyValueFactory<>("mail"));
        // metoda zobrazi udaje o zakaznikoch
        displayCustomersData();
        // po kliknuti na filter button sa zobrazi v tableView zakaznik, podla zadanej ID Card
        idCardFilterButton.setOnAction(actionEvent ->{
            filterByIdCard();
        });
        // po kliknuti na Pay for Booking, sa prekne scena, v ktorej zakaznik vykona platbu a jej zaznam sa ulozi do databazy
        payForBookingButton.setOnAction(actionEvent ->{
            CustomerORM customerORM = getSelectedCustomer();
            if (customerORM != null) {
                try {
                    new PaymentScene(hotel, roomORM, customerORM, 0);
                    switchScene.switchToBookNights(actionEvent, hotel);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        // ak zakaznik este nevyuzil sluzby databazy, tak nema record v databaze, tym padom ho musime vytvorit
        addCustomerButton.setOnAction(actionEvent -> {
            try {
                switchScene.switchToBookingAddCustomer(actionEvent, hotel, roomORM);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        // po kliknuti na sipku spat sa vrati na predoslu scenu
        backButton.setOnAction(actionEvent -> {
            try {
                switchScene.switchToBookNights(actionEvent, hotel);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }
}
