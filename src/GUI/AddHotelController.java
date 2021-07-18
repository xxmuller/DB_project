package GUI;

import Database.Address;
import Database.Hotel;
import Database.WorkWithDB;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;

public class AddHotelController {
    @FXML
    private SwitchScene switchScene = new SwitchScene();
    @FXML
    private TextField tfName, tfPhone, tfMail, tfSales, tfCity, tfStreet, tfCountry,
            tfHouseNum, tfPostalCode, tfEmpNumber, tfRoomsNumber;

    private WorkWithDB workWithDB = new WorkWithDB();
    private Hotel hotel;

    public boolean isValidTotalSales(String sales){
        try {
            Double.parseDouble(sales);
            return true;
        } catch (NumberFormatException e) {
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

    public boolean insertHotelInfo(){
        try {
            workWithDB.connectToDatabase(); // nadviaze spojenie s db a vytvori tabulky ak nie su vytvorene

            String query = "SELECT id FROM Address WHERE city='" +
                    hotel.getAddress().getCity() + "' AND street='" +
                    hotel.getAddress().getStreet() + "' AND house_num='" +
                    hotel.getAddress().getHouseNum() + "' AND postal_code='" +
                    hotel.getAddress().getPostalCode() + "' AND country='" +
                    hotel.getAddress().getCountry() + "';";

            workWithDB.setResultSet(workWithDB.getStatement().executeQuery(query)); // vykona vytvorenu query a jej vysledok ulozi do ResultSet

            // ak je ResultSet prazdny, tak to znamena, ze adresa este neexistuje, tym padom pri hodnoty do tabulky Address a prida aj hotel do tabulky Hotel
            if (!(workWithDB.getResultSet().next())){
                query = "INSERT INTO Address (city, street, house_num, postal_code, country) " +
                        "VALUES ('" + hotel.getAddress().getCity() + "', '" + hotel.getAddress().getStreet() + "', '" +
                        hotel.getAddress().getHouseNum() + "', '" + hotel.getAddress().getPostalCode() + "', '" + hotel.getAddress().getCountry() + "');";
                workWithDB.getStatement().executeUpdate(query); // query pridalo novy riadok do tabulky Address

                query = "SELECT id FROM Address ORDER BY ID DESC LIMIT 1;"; // query, ktore mi vrati hodnotu id posledneho pridaneho riadku
                workWithDB.setResultSet(workWithDB.getStatement().executeQuery(query));
                workWithDB.getResultSet().next(); // toto je potrebne predtym ako pouzije workWithDB.getResultSet().getInt("id") v nasledujucom query

                query = "INSERT INTO Hotel (name, phone, mail, total_sales, num_of_rooms, num_of_employees, address_id)" +
                        "VALUES ('" + hotel.getName() + "', '" + hotel.getPhone() + "', '" + hotel.getMail() + "', " + hotel.getTotalSales() + ", " +
                         hotel.getNumOfRooms() + ", " + hotel.getNumOfEmployees() + ", " + workWithDB.getResultSet().getInt("id") + ");";
                workWithDB.getStatement().executeUpdate(query); // pridanie noveho hotela do tabulky Hotel

                query = "SELECT id FROM Hotel ORDER BY ID DESC LIMIT 1;";
                workWithDB.setResultSet(workWithDB.getStatement().executeQuery(query));
                workWithDB.getResultSet().next(); // toto je potrebne predtym ako pouzije workWithDB.getResultSet().getInt("id")
                hotel.setId(workWithDB.getResultSet().getInt("id")); // nadstavim objektu, ktory som predtym vytvoril jeho id, ktore budem dalej potrebovat

                workWithDB.closeConnection();
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        workWithDB.closeConnection();
        // ked ResultSet nie je prazdny, tak nemoze vytvorit instanciu entity Address, pretoze uz existuje, tym padom sa neprida hotel do databazy, pokial sa nezmenia udaje
        System.out.println("Na adrese stoji ina budova.");
        return false;
    }

    public void addEmployeesButtonClicked(ActionEvent actionEvent) throws IOException {
        if (!(tfCity.getText().equals("")) && !(tfStreet.getText().equals("")) && !(tfHouseNum.getText().equals(""))
                && !(tfPostalCode.getText().equals("")) && !(tfCountry.getText().equals(""))){ // adresa je NOT NULL, preto je treba tato podmienka
            if (isValidTotalSales(tfSales.getText()) && isValidInt(tfEmpNumber.getText()) && isValidInt(tfRoomsNumber.getText())) {
                hotel = new Hotel(tfName.getText(), tfPhone.getText(), tfMail.getText(), Double.parseDouble(tfSales.getText()),
                        Integer.parseInt(tfRoomsNumber.getText()), Integer.parseInt(tfEmpNumber.getText()),
                        new Address(tfCity.getText(), tfStreet.getText(), tfHouseNum.getText(), tfPostalCode.getText(), tfCountry.getText()));

                if (insertHotelInfo())// zavolanie funkcie na insertovanie udajov do tabulky hotel
                    switchScene.switchToAddEmployeesScene(actionEvent, hotel); // iba ak sa hotel uspesne prida, tak sa zmeni scena
                else
                    System.out.println("Nepodarilo sa pridat hotel.");
            } else
                System.out.println("Piste cisla do Total Sales, Number of Employees a Number of Rooms.");
        } else
            System.out.println("Zadaj potrebne udaje do adresy!"); // neboli zadane udaje pre adresu
    }

    public void backButtonClicked(ActionEvent actionEvent) throws IOException {
        switchScene.switchToHomepageScene(actionEvent);
    }
}
