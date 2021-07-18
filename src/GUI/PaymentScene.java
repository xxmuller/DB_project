package GUI;

import Database.Hotel;
import ORM.*;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Optional;

public class PaymentScene {
    private CustomerORM customerORM;
    private RoomORM roomORM;
    private Hotel hotel;
    private Integer nights;
    private Stage stage = new Stage();

    @FXML
    private Label priceLabel, numOfNightsLabel;
    @FXML
    private TextField tfPaymentType, tfNumOfNights;
    @FXML
    private Button payButton, cancelButton;

    // otovri sa dialog okno a pokial ho pouzivatel neukonci jednym zo sposobov, tak nemoze ovladat hlavnu scenu
    public PaymentScene(Hotel hotel, RoomORM roomORM, CustomerORM customerORM, Integer nights) throws IOException {
        this.customerORM = customerORM;
        this.roomORM = roomORM;
        this.hotel = hotel;
        this.nights = nights;

        // klasicke zacitanie sceny, ktora patri dialogovemu oknu
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("../GUI XML/PaymentScene.fxml"));

        loader.setController(this);
        Parent root1 = (Parent) loader.load();
        stage.setTitle("Payment");

        // ak este nebol zadany pocet noci zadava ich uzivatel do textfieldu
        if(nights == 0){
            numOfNightsLabel.setVisible(false);

            // zakazdym ako sa zmeni cislo v textfielde vypise sa celkova cena
            // za dany pocet noci v danej izbe
            tfNumOfNights.textProperty().addListener(new ChangeListener<String>() {
                @Override
                public void changed(ObservableValue<? extends String> observable,
                                    String oldValue, String newValue) {

                    if(isValidInteger(newValue))
                        priceLabel.setText("Total price = " + getPrice(Integer.parseInt(newValue)));
                    else
                        priceLabel.setText("Total price = 0");
                }
            });
        }
        // ak uz bol zadany pocet noci (pri predlzovani pobytu) iba vypise tento pocet
        else{
            tfNumOfNights.setVisible(false);
            numOfNightsLabel.setText("Nights = " + nights);
            priceLabel.setText("Total price = " + getPrice(nights));
        }

        stage.setScene(new Scene(root1));
        stage.show();
    }

    public float getPrice(Integer numOfNights){
        return numOfNights * roomORM.getPrice_for_night();
    }

    public void payButtonClicked(){
        // ak pouzivatel klikne na pay button, tak sa vytvori entita payment
        // aktualizuje sa atribut booked_nights pre vybraneho customera
        // izba uz nebude volna, takze sa zmeni availability
        // zvysi sa total_sales v hoteli kde bola zabookovana izba
        // v pripade, ze klikne CANCEL, tak sa nevykona platba a pouzivatel sa vrati do hlavnej stage
        if(nights == 0 && !tfNumOfNights.getText().equals("") && isValidInteger(tfNumOfNights.getText())){
            updateDataInDatabase();
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setContentText("Successful payment.");
            alert.show();
            stage.close();
        }
        else if (nights == 0){
            // zobrazi sa alert, ktory informuje pouzivatela o nevyplnenych alebo nespravne zadanych hodnotach v TextFieldoch
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("Wrong data in Booked Nights or Payment Type.");
            alert.show();
        }
        // ak uz bol zadany pocet noci na zaplatenie v predoslej scene
        else{
            updateDataInDatabase();
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setContentText("Successful payment.");
            alert.show();
            stage.close();
        }
    }

    public void cancelButtonClicked(){
        stage.close();
    }
    // je potrebne spravit transakcie
    public void updateDataInDatabase(){
        int totalNights;

        if(nights == 0)
            totalNights = Integer.parseInt(tfNumOfNights.getText());
        else
            totalNights = nights;

        SessionFactory sessionFactory = new Configuration().configure().buildSessionFactory(); // nakonfiguruju sa udaje ohladom ORM z hibernate.cfg.xml
        Session session = sessionFactory.openSession(); // vytvorim session
        session.beginTransaction(); // zacnem transakciu, kde mozem vykonavat pomocou hibernate upravi nad databazou
        // pridam do databazy instanciu entity payment
        addPaymentToDatabase(session, totalNights, tfPaymentType.getText());

        // ak uz bol zadany pocet noci, teda idem predlzovat pobyt
        // pripocitam k zabookovanym nociam nove noci
        if(nights != 0)
            totalNights = customerORM.getBooked_nights() + nights;

        // aktualizujem atribut booked_nights u zakaznika, ktory si zabookoval izbu
        customerORM.setBooked_nights(totalNights);
        session.update(customerORM);

        // aktualizujem atribut availibility na 0 pre izbu, ktoru si zakaznik zabookoval a nadstavi datum odkedy dokedy tam je
        updateRoomInDatabase(session, totalNights);
        // aktualizujem atribut total_sales pre hotel v ktorom bola zabookovana izba
        HotelORM hotelORM = changeHotelToHotelORM(totalNights);
        session.update(hotelORM);

        session.getTransaction().commit();
        session.close();
        sessionFactory.close();
    }
    // v metode vytvorim instanciu Classy HotelORM, ktory bude mat rovnake udaje ako objekt this.hotel, len mu updatnem total_sales
    public HotelORM changeHotelToHotelORM(int nights){
        HotelORM hotelORM = new HotelORM();

        hotelORM.setId(hotel.getId());
        hotelORM.setName(hotel.getName());
        hotelORM.setPhone(hotel.getPhone());
        hotelORM.setMail(hotel.getMail());
        // zvysim total_sales v hoteli, pretoze sa budu updatovat
        hotelORM.setTotal_sales(hotel.getTotalSales() + (nights * roomORM.getPrice_for_night()));
        hotelORM.setNum_of_rooms(hotel.getNumOfRooms());
        hotelORM.setNum_of_employees(hotel.getNumOfEmployees());
        hotelORM.setAddress_id(hotel.getAddress().getId());

        return hotelORM;
    }

    public java.sql.Date calculateDateTo(java.sql.Date date_from, int nights){
        Calendar cal = Calendar.getInstance();
        cal.setTime(date_from);
        cal.add(Calendar.DATE, nights);

        return new java.sql.Date(cal.getTimeInMillis());
    }

    public void updateRoomInDatabase(Session session, int nights){
        roomORM.setAvailibility((byte) 0); // aktualizuje hodnotu availability, pretoze izba uz nie je volna

        // ak este nie je nastaveny datum odkedy je zabookovana izba
        if(roomORM.getDate_from() == null)
            roomORM.setDate_from(new java.sql.Date(Calendar.getInstance().getTimeInMillis())); // nadstavim datum odkedy je zabrana

        roomORM.setDate_to(calculateDateTo(roomORM.getDate_from(), nights));

        session.update(roomORM);
    }

    public void addPaymentToDatabase(Session session, int nights, String paymentType){
        PaymentORM paymentORM = new PaymentORM();

        paymentORM.setBill_num(1);
        paymentORM.setPrice(roomORM.getPrice_for_night() * nights);
        paymentORM.setPayment_time(new Timestamp(System.currentTimeMillis()));

        System.out.println(paymentORM.getPayment_time());

        paymentORM.setPayment_type(paymentType);
        paymentORM.setCustomer_id(this.customerORM.getId());
        // ulozim vytvorenu payment do databazy
        session.save(paymentORM);
        // bill num bude vzdy o 100000 vacsi ako id, preto je potrebne znova setnut bill_num a updatnut v databaze
        paymentORM.setBill_num(paymentORM.getId() + 100000);
        session.update(paymentORM);
    }

    public boolean isValidInteger(String num){
        try {
            Integer.parseInt(num);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
