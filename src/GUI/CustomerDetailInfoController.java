package GUI;

import Database.*;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.io.IOException;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class CustomerDetailInfoController implements Initializable {
    private SwitchScene switchScene = new SwitchScene();
    private WorkWithDB workWithDB = new WorkWithDB();
    private Customer customer;
    private Hotel hotel;

    @FXML
    private Label nameOfCustomer;
    @FXML
    private Button backButton, saveChangesButton;
    @FXML
    private TextField tfLastName, tfPhone, tfMail, tfBookedNights, tfRoomNum, tfCountry, tfStreet, tfHouseNum, tfPostalCode, tfCity;

    public CustomerDetailInfoController(Hotel hotel, Customer customer){
        this.customer = customer;
        this.hotel = hotel;
    }

    public void textFieldData(TextField textField, String data){
        textField.setText(data);
    }

    public void displayCustomerDetailData(){
        nameOfCustomer.setText(customer.getFirstName() + " " + customer.getLastName());
        textFieldData(tfLastName, customer.getLastName());
        textFieldData(tfPhone, customer.getPhone());
        textFieldData(tfMail, customer.getMail());
        textFieldData(tfBookedNights, Integer.toString(customer.getBookedNights()));
        textFieldData(tfRoomNum, Integer.toString(customer.getRoomNumber()));

        textFieldData(tfCountry, customer.getAddress().getCountry());
        textFieldData(tfStreet, customer.getAddress().getStreet());
        textFieldData(tfHouseNum, customer.getAddress().getHouseNum());
        textFieldData(tfPostalCode, customer.getAddress().getPostalCode());
        textFieldData(tfCity, customer.getAddress().getCity());
    }

    public boolean updateCustomerByQuery(Customer customer1){
        // treba spravit transakcie
        try {
            workWithDB.connectToDatabase(); // nadviaze spojenie s db a vytvori tabulky ak nie su vytvorene
            workWithDB.getConn().setAutoCommit(false);

            String query = "UPDATE Address SET city =?, street = ?, house_num =?, postal_code =?, country =? WHERE id =?;";
            PreparedStatement preparedStatement = workWithDB.getConn().prepareStatement(query);
            preparedStatement.setString(1, customer1.getAddress().getCity());
            preparedStatement.setString(2, customer1.getAddress().getStreet());
            preparedStatement.setString(3, customer1.getAddress().getHouseNum());
            preparedStatement.setString(4, customer1.getAddress().getPostalCode());
            preparedStatement.setString(5, customer1.getAddress().getCountry());
            preparedStatement.setInt(6, customer1.getAddress().getId());

            preparedStatement.executeUpdate();
            workWithDB.getConn().commit();

            query = "UPDATE customer SET last_name=?, phone=?, mail=?, booked_nights=? WHERE id_card_num=?;";
            PreparedStatement nextPrepStatement = workWithDB.getConn().prepareStatement(query);
            nextPrepStatement.setString(1, customer1.getLastName());
            nextPrepStatement.setString(2, customer1.getPhone());
            nextPrepStatement.setString(3, customer1.getMail());
            nextPrepStatement.setInt(4, customer1.getBookedNights());
            nextPrepStatement.setInt(5, customer1.getId());
            nextPrepStatement.executeUpdate();

            workWithDB.getConn().commit();
            workWithDB.getConn().setAutoCommit(true);
            workWithDB.closeConnection();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean isValidInt(String number){
        try {
            Integer.parseInt(number);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public void  updateCustomerInfo(){
        // adresa je NOT NULL, preto je treba tato podmienka
        if (isValidInt(tfBookedNights.getText()) && isValidInt(tfRoomNum.getText())) {
            if (tfCity.getText().equals("") || tfStreet.getText().equals("") || tfHouseNum.getText().equals("")
                    || tfPostalCode.getText().equals("") || tfCountry.getText().equals("")) {
                System.out.println("Zadajte potrebne udaje do adresy.");
                return;
            }

            Customer customer1 = new Customer(customer.getId(), customer.getFirstName(), tfLastName.getText(), tfPhone.getText(), tfMail.getText(),
                    Integer.parseInt(tfBookedNights.getText()), customer.getRoomNumber(),
                    new Address(tfCity.getText(), tfStreet.getText(), tfHouseNum.getText(), tfPostalCode.getText(), tfCountry.getText()));
            // ak sa neupdatnu udaje v databaze, tak ich nezmenim ani objektu customer
            if (!updateCustomerByQuery(customer1)) {
                System.out.println("Nepodarilo sa updatnut udaje v databaze.");
                return;
            }

            nameOfCustomer.setText(customer1.getFirstName() + " " + customer1.getLastName());
            customer1.getAddress().setId(customer.getAddress().getId());
            customer1.setId(customer.getId());
            customer = customer1;
        } else
            System.out.println("Skontroluj cisla v Booked nights a Room Number.");
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        displayCustomerDetailData(); // zobrazia sa vsetky udaje pre daneho zakaznika

        backButton.setOnAction(actionEvent -> {
            try {
                switchScene.switchToManageHotelCustomers(actionEvent, hotel);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        saveChangesButton.setOnAction(actionEvent -> {
            updateCustomerInfo();
        });
    }
}
