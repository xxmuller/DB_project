package GUI;

import Database.Hotel;
import ORM.AddressORM;
import ORM.CustomerORM;
import ORM.RoomORM;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class BookingAddCustomerController implements Initializable {
    private SwitchScene switchScene = new SwitchScene();
    private RoomORM roomORM;
    private Hotel hotel;

    @FXML
    private TextField tfIdCard, tfFirstName, tfLastName, tfPhone, tfMail,
            tfCity, tfStreet, tfCountry, tfHouseNum, tfPostalCode;
    @FXML
    private Button  addCustomerButton, backButton;

    public BookingAddCustomerController(RoomORM roomORM, Hotel hotel){
        this.roomORM = roomORM;
        this.hotel = hotel;
    }

    public boolean isValidInt(String number){
        try {
            Integer.parseInt(number);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public int addAddressToDatabase(Session session){
        AddressORM addressORM = new AddressORM();
        addressORM.setCity(tfCity.getText());
        addressORM.setStreet(tfStreet.getText());
        addressORM.setCountry(tfCountry.getText());
        addressORM.setHouse_num(tfHouseNum.getText());
        addressORM.setPostal_code(tfPostalCode.getText());

        session.save(addressORM); // pridam adresu do databazy

        return addressORM.getId();
    }

    public CustomerORM addCustomerToDatabase(Session session, int customerAddressId){
        CustomerORM newCustomerORM = new CustomerORM();
        newCustomerORM.setId_card_num(Integer.parseInt(tfIdCard.getText()));
        newCustomerORM.setFirst_name(tfFirstName.getText());
        newCustomerORM.setLast_name(tfLastName.getText());
        newCustomerORM.setPhone(tfPhone.getText());
        newCustomerORM.setMail(tfMail.getText());
        newCustomerORM.setBooked_nights(0);
        newCustomerORM.setRoom_id(roomORM.getId());
        newCustomerORM.setHotel_id(hotel.getId());
        newCustomerORM.setAddress_id(customerAddressId);

        session.save(newCustomerORM); // pridam adresu do databazy
        return newCustomerORM;
    }

    public CustomerORM addNewCustomer(){
        if (isValidInt(tfIdCard.getText())){
            if (!(tfCity.getText().equals("")) && !(tfStreet.getText().equals("")) && !(tfHouseNum.getText().equals(""))
                        && !(tfPostalCode.getText().equals("")) && !(tfCountry.getText().equals(""))) { // adresa je NOT NULL, preto je treba tato podmienka
                // pridam adresu do databazy
                SessionFactory sessionFactory = new Configuration().configure().buildSessionFactory(); // nakonfiguruju sa udaje ohladom ORM z hibernate.cfg.xml
                Session session = sessionFactory.openSession(); // vytvorim session
                session.beginTransaction(); // zacnem transakciu, kde mozem vykonavat pomocou hibernate upravi nad databazou
                 // najprv sa prida adresa zakaznika do databazi a potom udaje o zakaznikovi
                CustomerORM customerORM = addCustomerToDatabase(session, addAddressToDatabase(session));
                session.close(); // skonci session
                sessionFactory.close();
                return customerORM;
            } else {
                System.out.println("Zadaj potrebne udaje do adresy!"); // neboli zadane udaje pre adresu
                return null;
            }
        } else {
            System.out.println("Skontroluj cisla v ID Card");
            return null;
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // po kliknuti na tlacitko addCustomerButton sa vytvori record zamestnancovej adresi a zamestnanca v DB, nasledne sa prepne scena na platenie
        addCustomerButton.setOnAction(actionEvent -> {
            CustomerORM customerORM = addNewCustomer();
            if (customerORM != null){
                try {
                    switchScene.switchToBookNights(actionEvent, hotel);
                    new PaymentScene(hotel, roomORM, customerORM, 0);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        backButton.setOnAction(actionEvent ->{
            try {
                switchScene.switchToChooseCustomer(actionEvent, hotel, roomORM);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }
}
