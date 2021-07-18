package GUI;

import Database.Hotel;
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

//import java.awt.event.ActionEvent;
import javafx.event.ActionEvent;
import java.io.IOException;
import java.net.URL;
import java.sql.Date;
import java.util.List;
import java.util.ResourceBundle;

public class BookNightsController implements Initializable {
    private ObservableList<RoomORM> observableList = null;
    private SwitchScene switchScene = new SwitchScene();
    private Hotel hotel;

    @FXML
    private TableView<RoomORM> tableView; // v tabulke budu iba udaje z classy RoomORM
    @FXML
    private TableColumn<RoomORM, Integer> columnId; // podla toho aky datovy typ je Id v classe RoomORM som vytvoril <RoomORM, Integer>
    @FXML
    private TableColumn <RoomORM, Integer> columnRoomNum; // podla toho aky datovy typ je roomNum v classe RoomORM som vytvoril <RoomORM, Integer>
    @FXML
    private TableColumn <RoomORM, Integer> columnNumOfBeds; // podla toho aky datovy typ je numOfBeds v classe RoomORM som vytvoril <RoomORM, Integer>
    @FXML
    private TableColumn <RoomORM, Float> columnPriceForNight; // podla toho aky datovy typ je priceForNight v classe RoomORM som vytvoril <RoomORM, Float>
    @FXML
    private TableColumn <RoomORM, Byte> columnAvailibility; // podla toho aky datovy typ je availability v classe RoomORM som vytvoril <RoomORM, Byte>
    @FXML
    private TableColumn <RoomORM, Date> columnDateFrom; // podla toho aky datovy typ je priceForNight v classe RoomORM som vytvoril <RoomORM, Date>
    @FXML
    private TableColumn <RoomORM, Date> columnDateTo; // podla toho aky datovy typ je priceForNight v classe RoomORM som vytvoril <RoomORM, Date>
    @FXML
    private Button bedsFilterButton, bookChosenRoomButton, backButton, refreshButton;
    @FXML
    private TextField textFieldNumOfBeds;

    public BookNightsController(Hotel hotel){
        this.hotel = hotel;
    }

    public void displayDataInTableView() {
        SessionFactory serviceFactory = new Configuration().configure().buildSessionFactory();
        Session session = serviceFactory.openSession();
        try {
            session.beginTransaction(); // vykonanie transakcie
            // dostanem vsetky udaje o izbach vybraneho hotela a ulozim ich do listu
            List<RoomORM> list = session.createQuery("from room R where R.hotel_id ='" + hotel.getId() + "'").getResultList();
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

    public void filterByNumberOfBeds(){
        if (textFieldNumOfBeds.getText().equals(""))
            displayDataInTableView();
        else {
            if (checkIfInteger(textFieldNumOfBeds.getText())) // ak zadani pocet izieb nebude cislo, tak sa nevykona vyhladavanie
                return;
            try {
                SessionFactory serviceFactory = new Configuration().configure().buildSessionFactory(); // nakonfiguruju sa udaje ohladom ORM z hibernate.cfg.xml
                Session session = serviceFactory.openSession(); // vytvorim session
                session.beginTransaction(); // zacnem transakciu, kde mozem vykonavat pomocou hibernate upravi nad databazou
                // do listu nacitam izby, ktore su vo vybranom hoteli a maju zadany pocet posteli
                List<RoomORM> list = session.createQuery("from room R where R.hotel_id='" + hotel.getId() +
                                                            "' and number_of_beds='" + Integer.parseInt(textFieldNumOfBeds.getText()) + "'").getResultList();
                observableList = FXCollections.observableArrayList(list); // inicializujem observableList, aby som mohol jeho udaje zobrazit v tableView
                tableView.setItems(observableList); // zobrazim udaje v tableView
                session.close(); // skonci session
                serviceFactory.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public RoomORM getSelectedRoom(){
        if (tableView.getSelectionModel().getSelectedItem() != null){ // zisti, ci pouzivatel vybral izbu na zabookovanie
            RoomORM selectedRoom = tableView.getSelectionModel().getSelectedItem();
            if (selectedRoom.getAvailibility() != 0){ // izba sa neprenajima, takze je mozne ju zabookovat pre zakaznika
                return selectedRoom;
            } else {
                System.out.println("Izba je momentalne prenajimana.");
                return null;
            }
        } else {
            System.out.println("Treba vybrat izbu, ktoru chcete zabookovat.");
            return null;
        }
    }

    public void extendStayButtonClicked(ActionEvent actionEvent){
        try {
            switchScene.switchToExtendStayCustomersScene(actionEvent, hotel);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // kazdej column nadstavi hodnotu podla toho ako sa nazyva privatna variable v classe RoomORM
        columnId.setCellValueFactory(new PropertyValueFactory<>("id"));
        columnRoomNum.setCellValueFactory(new PropertyValueFactory<>("room_num"));
        columnNumOfBeds.setCellValueFactory(new PropertyValueFactory<>("number_of_beds"));
        columnPriceForNight.setCellValueFactory(new PropertyValueFactory<>("price_for_night"));
        columnAvailibility.setCellValueFactory(new PropertyValueFactory<>("availibility"));
        columnDateFrom.setCellValueFactory(new PropertyValueFactory<>("date_from"));
        columnDateTo.setCellValueFactory(new PropertyValueFactory<>("date_to"));
        // metoda zobrazi udaje o izbach hotela v tabulke
        displayDataInTableView();
        // po kliknuti na tlacitko sa vyfiltruju izby podla poctu posteli
        bedsFilterButton.setOnAction(actionEvent ->{
            filterByNumberOfBeds();
        });
        // ked pouzivatel klikne na tlacitko Book Chosen Room, tak program zisti, ci vybral izbu, ktoru chce zabookovat a ci je volna
        // ak je izba volna, tak sa prepne scena, kde pouzivatel vyberie zakaznika z databazy alebo vytvori novy zaznam pre pouzivatela
        bookChosenRoomButton.setOnAction(actionEvent ->{
            RoomORM selectedRoom = getSelectedRoom();
            if (selectedRoom != null){
                try {
                    switchScene.switchToChooseCustomer(actionEvent, hotel, selectedRoom);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        // po kliknuti na refreshButton sa obnovi tato scena
        refreshButton.setOnAction(actionEvent -> {
            try {
                switchScene.switchToBookNights(actionEvent, hotel);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        // po kliknuti na backButton sa pouzivatel vrati na predoslu scenu
        backButton.setOnAction(actionEvent -> {
            try {
                switchScene.switchToManageHotels(actionEvent);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }
}
