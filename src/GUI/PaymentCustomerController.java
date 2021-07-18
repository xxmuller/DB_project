package GUI;

import Database.Customer;
import Database.Hotel;
import Database.WorkWithDB;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class PaymentCustomerController implements Initializable {
    private SwitchScene switchScene = new SwitchScene();
    private Customer customer;
    private Hotel hotel;

    @FXML
    private Label nameOfCustomer;
    @FXML
    private Button backButton;
    @FXML
    private TextField tfLastName, tfPhone, tfMail, tfBookedNights, tfRoomNum, tfCountry, tfStreet, tfHouseNum, tfPostalCode, tfCity;

    public PaymentCustomerController(Customer customer, Hotel hotel){
        this.customer = customer;
        this.hotel = hotel;
    }

    public void displayCustomerData(){
        nameOfCustomer.setText(customer.getFirstName() + " " + customer.getLastName());
        tfLastName.setText(customer.getLastName());
        tfPhone.setText(customer.getPhone());
        tfMail.setText(customer.getMail());
        tfBookedNights.setText(Integer.toString(customer.getBookedNights()));
        tfRoomNum.setText(Integer.toString(customer.getRoomNumber()));

        tfCountry.setText(customer.getAddress().getCountry());
        tfStreet.setText(customer.getAddress().getStreet());
        tfHouseNum.setText(customer.getAddress().getHouseNum());
        tfPostalCode.setText(customer.getAddress().getPostalCode());
        tfCity.setText(customer.getAddress().getCity());
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        displayCustomerData(); // zobrazia sa vsetky udaje pre daneho zakaznika
        
        backButton.setOnAction(actionEvent -> {
            try {
                switchScene.switchToManageHotelPayments(actionEvent, hotel);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }
}
