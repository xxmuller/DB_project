package GUI;

import Database.Address;
import Database.Hotel;
import Database.WorkWithDB;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import java.io.IOException;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class ManageChosenHotelController implements Initializable {
    @FXML
    private SwitchScene switchScene = new SwitchScene();
    @FXML
    private Label nameOfHotel;
    @FXML
    private Button backButton, paymentsButton, manageRoomsRecordsButton, manageEmployeesRecordsButton, manageCustomersRecordsButton;
    @FXML
    private TextField tfPhone, tfMail, tfCity, tfStreet, tfHouseNum, tfPostalCode, tfCountry, tfSales, tfEmpNumber, tfRoomsNumber;

    private Hotel hotel;
    private WorkWithDB workWithDB = new WorkWithDB();

    public ManageChosenHotelController(Hotel hotel){
        this.hotel = hotel;
    }

    // vsetky udaje o hoteli sa zobrazia v textfieldoch alebo labely sceny
    public void initInfoAboutHotel(){
        nameOfHotel.setText(hotel.getName());
        tfPhone.setText(hotel.getPhone());
        tfMail.setText(hotel.getMail());
        tfCity.setText(hotel.getAddress().getCity());
        tfStreet.setText(hotel.getAddress().getStreet());
        tfHouseNum.setText(hotel.getAddress().getHouseNum());
        tfPostalCode.setText(hotel.getAddress().getPostalCode());
        tfCountry.setText(hotel.getAddress().getCountry());
        tfSales.setText(Double.toString(hotel.getTotalSales()));
        tfEmpNumber.setText(Integer.toString(hotel.getNumOfEmployees()));
        tfRoomsNumber.setText(Integer.toString(hotel.getNumOfRooms()));
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initInfoAboutHotel(); // pred zobrazenim sceny sa nadstavia udaje o hoteli do prislusnych textfieldov alebo labelu
    }

    public void updateHotelInfo(Hotel h2, boolean changingAddress) throws SQLException {
        String query;
        workWithDB.connectToDatabase(); // nadviaze spojenie s db a vytvori tabulky ak nie su vytvorene

        //iba ak menim adresu, tak musim pozerat ci uz ju nemam
        //ak nie, mozem updatenut danu adresu hotela
        if(changingAddress){
            query = "SELECT id FROM Address WHERE city='" +
                    h2.getAddress().getCity() + "' AND street='" +
                    h2.getAddress().getStreet() + "' AND house_num='" +
                    h2.getAddress().getHouseNum() + "' AND postal_code='" +
                    h2.getAddress().getPostalCode() + "' AND country='" +
                    h2.getAddress().getCountry() + "';";

            workWithDB.setResultSet(workWithDB.getStatement().executeQuery(query)); // vykona vytvorenu query a jej vysledok ulozi do ResultSet

            //ak este nemam tuto adresu, mozem ju updatenut v DB
            if (!(workWithDB.getResultSet().next())){

                //ziskavam id adresy, ktoru idem updatenut
                query = "SELECT id FROM Address WHERE city='" +
                        hotel.getAddress().getCity() + "' AND street='" +
                        hotel.getAddress().getStreet() + "' AND house_num='" +
                        hotel.getAddress().getHouseNum() + "' AND postal_code='" +
                        hotel.getAddress().getPostalCode() + "' AND country='" +
                        hotel.getAddress().getCountry() + "';";

                workWithDB.setResultSet(workWithDB.getStatement().executeQuery(query)); // vykona vytvorenu query a jej vysledok ulozi do ResultSet
                workWithDB.getResultSet().next();
                workWithDB.getConn().setAutoCommit(false);
                query = "UPDATE Address SET city =?, street =?, house_num =?, postal_code =?, country =? WHERE id =?;";
                PreparedStatement preparedStatement = workWithDB.getConn().prepareStatement(query);
                preparedStatement.setString(1, h2.getAddress().getCity());
                preparedStatement.setString(2, h2.getAddress().getStreet());
                preparedStatement.setString(3, h2.getAddress().getHouseNum());
                preparedStatement.setString(4, h2.getAddress().getPostalCode());
                preparedStatement.setString(5, h2.getAddress().getCountry());
                preparedStatement.setInt(6, workWithDB.getResultSet().getInt("id"));
                preparedStatement.executeUpdate();
                workWithDB.getConn().commit();
                workWithDB.getConn().setAutoCommit(true);
            }
            else
                System.out.println("Na adrese stoji ina budova.");
        }
        // treba spravit transakciu
        //updateujem vsetky udaje o hoteli v databaze

        query = "UPDATE Hotel SET phone =?, mail =?,total_sales =? WHERE id =?;";
        PreparedStatement preparedStatement = workWithDB.getConn().prepareStatement(query);
        preparedStatement.setString(1, h2.getPhone());
        preparedStatement.setString(2, h2.getMail());
        preparedStatement.setDouble(3, h2.getTotalSales());
        preparedStatement.setInt(4, hotel.getId());
        preparedStatement.executeUpdate();

        workWithDB.closeConnection();
    }

    public boolean isValidDouble(String number){
        try {
            Double.parseDouble(number);
            return true;
        } catch (NumberFormatException e){
            return false;
        }
    }

    public boolean isDifferentAddress(Hotel h2){
        if (!h2.getAddress().getCity().equals(hotel.getAddress().getCity()) ||
                !h2.getAddress().getStreet().equals(hotel.getAddress().getStreet()) ||
                !h2.getAddress().getHouseNum().equals(hotel.getAddress().getHouseNum()) ||
                !h2.getAddress().getPostalCode().equals(hotel.getAddress().getPostalCode()) ||
                !h2.getAddress().getCountry().equals(hotel.getAddress().getCountry())
        )   return true;

        return false;
    }

    public Address getHotelAddress(int address_id){
        try {
            String query = "SELECT city, street, house_num, postal_code, country FROM address WHERE id=" + Integer.toString(address_id) + ";";
            workWithDB.setResultSet(workWithDB.getStatement().executeQuery(query)); // query vyberie udaje pre dany hotel
            workWithDB.getResultSet().next();
            // vytvori sa objekt pre adresu, ktory metoda vrati
            return new Address(workWithDB.getResultSet().getString("city"),
                    workWithDB.getResultSet().getString("street"),
                    workWithDB.getResultSet().getString("house_num"),
                    workWithDB.getResultSet().getString("postal_code"),
                    workWithDB.getResultSet().getString("country"));
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public Hotel getNewHotel(Integer id) throws SQLException {

        String query = "SELECT address_id FROM Hotel WHERE id='"+id+"'";
        workWithDB.setResultSet(workWithDB.getStatement().executeQuery(query)); // vykona vytvorenu query a jej vysledok ulozi do ResultSet
        workWithDB.getResultSet().next();

        Address address = getHotelAddress(workWithDB.getResultSet().getInt("address_id"));

        query = "SELECT * FROM Hotel WHERE id='"+id+"'";
        workWithDB.setResultSet(workWithDB.getStatement().executeQuery(query)); // vykona vytvorenu query a jej vysledok ulozi do ResultSet
        workWithDB.getResultSet().next();

        Hotel newHotel = new Hotel(workWithDB.getResultSet().getString("name"), workWithDB.getResultSet().getString("phone"),
                workWithDB.getResultSet().getString("mail"), workWithDB.getResultSet().getFloat("total_sales"),
                workWithDB.getResultSet().getInt("num_of_rooms"), workWithDB.getResultSet().getInt("num_of_employees"),
                address);

        newHotel.setId(hotel.getId());

        return newHotel;
    }

    public void saveChangesButtonClicked(ActionEvent actionEvent) throws SQLException, IOException {
        Hotel h2;
        boolean changingAddress = false;

        if (isValidDouble(tfSales.getText())) {

            h2 = new Hotel(hotel.getName(), tfPhone.getText(), tfMail.getText(), Double.parseDouble(tfSales.getText()),
                    Integer.parseInt(tfRoomsNumber.getText()), Integer.parseInt(tfEmpNumber.getText()),
                    new Address(tfCity.getText(), tfStreet.getText(), tfHouseNum.getText(), tfPostalCode.getText(), tfCountry.getText()));

            //ak menim adresu
            if (isDifferentAddress(h2))
                changingAddress = true;

            if (h2.getNumOfEmployees() != hotel.getNumOfEmployees() || h2.getNumOfRooms() != hotel.getNumOfRooms())
                System.out.println("Nemozes menit pocet zamestnancov alebo izieb!");


            //pozeram ci sa zmenili nejake udaje v textfieldoch
            //ak ano ulozim tieto zmeny v databaze
            if (!h2.getPhone().equals(hotel.getPhone()) || !h2.getMail().equals(hotel.getMail()) ||
                    h2.getTotalSales() != hotel.getTotalSales() || isDifferentAddress(h2)) {
                updateHotelInfo(h2, changingAddress);

                workWithDB.connectToDatabase(); // pripajam sa k db
                //musim nastavit novy hotel, lebo nastali zmeny
                hotel = getNewHotel(hotel.getId());
                workWithDB.closeConnection();

                switchScene.switchToManageChosenHotel(actionEvent, hotel);
            }
        } else
            System.out.println("Skontroluj udaj v Total sales.");
    }

    public void backButtonClicked(ActionEvent actionEvent) throws IOException {
        switchScene.switchToManageHotels(actionEvent);
    }
    // zmeni sa cena, kde budu zobrazene vsetky platby, ktore prebehli v hoteli.
    public void paymentsButtonClicked(ActionEvent actionEvent) throws IOException {
        switchScene.switchToManageHotelPayments(actionEvent, hotel);
    }

    // zmeni sa scena, kde budem pouzivatel moct manazovat zamestnancov hotela
    public void manageEmployeesRecordsButtonClicked(ActionEvent actionEvent) throws IOException {
        switchScene.switchToManageHotelEmployees(actionEvent, hotel);
    }

    // zmeni sa scena, kde budem pouzivatel moct manazovat izby hotela
    public void manageRoomsButtonClicked(ActionEvent actionEvent) throws IOException {
        switchScene.switchToManageHotelRooms(actionEvent, hotel);
    }

    // zmeni sa scena, kde budem pouzivatel moct manazovat zakaznikov hotela
    public void manageCustomerButtonClicked(ActionEvent actionEvent) throws IOException {
        switchScene.switchToManageHotelCustomers(actionEvent, hotel);
    }
}
