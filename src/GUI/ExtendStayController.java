package GUI;

import Database.Hotel;
import ORM.CustomerORM;
import ORM.RoomORM;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class ExtendStayController implements Initializable {
    private CustomerORM customerORM;
    private RoomORM roomORM;
    private Hotel hotel;
    private SwitchScene switchScene = new SwitchScene();
    private ObservableList<CustomerORM> observableList = null;
    private boolean paid = false;

    @FXML
    private Label firstNameLabel, lastNameLabel, phoneLabel, mailLabel, currBookedNightsLabel, paidLabel, roomNumLabel;
    @FXML
    private Button backButton, payButton, extendStayButton;
    @FXML
    private TextField tfNewNights;

    public ExtendStayController(Hotel hotel, CustomerORM customerORM){
        this.customerORM = customerORM;
        this.hotel = hotel;
    }

    public void setRoomORM(){
        SessionFactory serviceFactory = new Configuration().configure().buildSessionFactory(); // nakonfiguruju sa udaje ohladom ORM z hibernate.cfg.xml
        Session session = serviceFactory.openSession(); // vytvorim session

        session.beginTransaction(); // zacnem transakciu, kde mozem vykonavat pomocou hibernate upravi nad databazou

        // nacitam izbu zakaznika s danou ID
        RoomORM room = (RoomORM)session.createQuery("from room R where R.id='" +
                customerORM.getRoom_id() + "' and R.hotel_id='" + customerORM.getHotel_id() + "'").getSingleResult();

        session.close(); // skonci session
        serviceFactory.close();

        this.roomORM = room;
    }

    public void displayRoomDetailData(){
        firstNameLabel.setText(customerORM.getFirst_name());
        lastNameLabel.setText(customerORM.getLast_name());
        phoneLabel.setText(customerORM.getPhone());
        mailLabel.setText(customerORM.getMail());
        roomNumLabel.setText(String.valueOf(roomORM.getRoom_num()));
        currBookedNightsLabel.setText("Current number of booked nights:  " + customerORM.getBooked_nights());
    }

    public boolean isValidInteger(String num){
        try {
            Integer.parseInt(num);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        setRoomORM(); // nastavim izbu podla daneho zakaznika
        displayRoomDetailData(); // zobrazia sa vsetky udaje pre danu izbu

        // po kliknuti na payButton sa zobrazi obrazovka na zaplatenie dodatocnych noci
        payButton.setOnAction(actionEvent -> {
            try {
                if(!tfNewNights.getText().equals("") && isValidInteger(tfNewNights.getText())){
                    new PaymentScene(hotel, roomORM, customerORM, Integer.parseInt(tfNewNights.getText()));
                    paid = true;
                }
                else
                    System.out.println("Zadaj spravny pocet noci!");
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        // po kliknuti na extendStayButton sa pouzivatel vrati na homepage
        extendStayButton.setOnAction(actionEvent -> {
            try {
                if(paid)
                    switchScene.switchToBookNights(actionEvent, hotel);
                else
                    System.out.println("Najprv treba zaplatit!");
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        // po kliknuti na backButton sa pouzivatel vrati na predoslu scenu
        backButton.setOnAction(actionEvent -> {
            try {
                switchScene.switchToExtendStayCustomersScene(actionEvent, hotel);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }
}
