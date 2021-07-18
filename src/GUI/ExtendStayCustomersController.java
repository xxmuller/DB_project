package GUI;

import Database.Hotel;
import ORM.CustomerORM;
import ORM.RoomORM;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class ExtendStayCustomersController implements Initializable {
    private ObservableList<CustomerORM> observableList = null;
    private SwitchScene switchScene = new SwitchScene();
    private Hotel hotel;

    @FXML
    private TableView<CustomerORM> tableView; // v tabulke budu iba udaje z classy CustomerORM
    @FXML
    private TableColumn<CustomerORM, Integer> columnIdCard;
    @FXML
    private TableColumn <CustomerORM, String> columnFirstName;
    @FXML
    private TableColumn <CustomerORM, String> columnLastName;
    @FXML
    private TableColumn <CustomerORM, String> columnPhone;
    @FXML
    private TableColumn <CustomerORM, String> columnMail;
    @FXML
    private TableColumn<CustomerORM, Integer> columnNumOfNights;
    @FXML
    private Button nightsFilterButton, extendStayButton, backButton;
    @FXML
    private TextField tfNumOfNights;

    public ExtendStayCustomersController(Hotel hotel){
        this.hotel = hotel;
    }

    public void displayDataInTableView() {
        SessionFactory serviceFactory = new Configuration().configure().buildSessionFactory();
        Session session = serviceFactory.openSession();
        try {
            session.beginTransaction(); // vykonanie transakcie
            // dostanem vsetky udaje o izbach vybraneho hotela a ulozim ich do listu
            List<CustomerORM> list = session.createQuery("from customer C where C.hotel_id ='" + hotel.getId() + "'").getResultList();
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
            System.out.println("Pocet noci musi byt cele cislo.");
            return true;
        }
    }

    public void filterByNumberOfNights(){
        if (tfNumOfNights.getText().equals(""))
            displayDataInTableView();
        else {
            if (checkIfInteger(tfNumOfNights.getText())) // ak zadani pocet noci nebude cislo, tak sa nevykona vyhladavanie
                return;
            try {
                SessionFactory serviceFactory = new Configuration().configure().buildSessionFactory(); // nakonfiguruju sa udaje ohladom ORM z hibernate.cfg.xml
                Session session = serviceFactory.openSession(); // vytvorim session
                session.beginTransaction(); // zacnem transakciu, kde mozem vykonavat pomocou hibernate upravi nad databazou

                // do listu nacitam zakaznikov, ktori su vo vybranom hoteli a maju zadany pocet rezervovanych noci
                List<CustomerORM> list = session.createQuery("from customer C where C.hotel_id='" + hotel.getId() +
                        "' and booked_nights='" + Integer.parseInt(tfNumOfNights.getText()) + "'").getResultList();

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
        if (tableView.getSelectionModel().getSelectedItem() != null){ // zisti, ci pouzivatel vybral zakaznika
            return tableView.getSelectionModel().getSelectedItem(); // funkcia vrati vybraneho zakaznika
        } else {
            System.out.println("Treba vybrat zakaznika, ktoremu chcete predlzit pobyt.");
            return null;
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // kazdej column nastavi hodnotu podla toho ako sa nazyva privatna variable v classe CustomerORM
        columnIdCard.setCellValueFactory(new PropertyValueFactory<>("id_card_num"));
        columnFirstName.setCellValueFactory(new PropertyValueFactory<>("first_name"));
        columnLastName.setCellValueFactory(new PropertyValueFactory<>("last_name"));
        columnPhone.setCellValueFactory(new PropertyValueFactory<>("phone"));
        columnMail.setCellValueFactory(new PropertyValueFactory<>("mail"));
        columnNumOfNights.setCellValueFactory(new PropertyValueFactory<>("booked_nights"));
        // metoda zobrazi udaje o zakaznikoch hotela v tabulke
        displayDataInTableView();
        // po kliknuti na tlacitko sa vyfiltruju zakaznici podla poctu rezervovanych noci
        nightsFilterButton.setOnAction(actionEvent ->{
            filterByNumberOfNights();
        });
        // ked pouzivatel klikne na tlacitko ExtendStay, tak program zisti, ci vybral zakaznika, ktoremu chce predlzit pobyt
        // ak vybral prepne sa scena
        extendStayButton.setOnAction(actionEvent ->{
            CustomerORM selectedCustomer = getSelectedCustomer();
            if (selectedCustomer != null){
                try {
                    switchScene.switchToExtendStayScene(actionEvent, hotel, selectedCustomer);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        // po kliknuti na backButton sa pouzivatel vrati na predoslu scenu
        backButton.setOnAction(actionEvent -> {
            try {
                switchScene.switchToBookNights(actionEvent, hotel);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }
}
