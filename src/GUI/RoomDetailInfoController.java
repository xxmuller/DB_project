package GUI;

import Database.*;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.io.IOException;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ResourceBundle;

public class RoomDetailInfoController implements Initializable {
    private SwitchScene switchScene = new SwitchScene();
    private WorkWithDB workWithDB = new WorkWithDB();
    private Room room;
    private Hotel hotel;

    @FXML
    private Label roomNumber, labelEmployeeName;
    @FXML
    private Button backButton, saveChangesButton;
    @FXML
    private TextField tfAvailability, tfNumOfBeds, tfPrice, tfEmployee, tfDateFrom, tfDateTo;

    public RoomDetailInfoController(Hotel hotel, Room room){
        this.room = room;
        this.hotel = hotel;
    }

    public String getEmployeeName(Room room) throws SQLException {
        String firstName;
        String lastName = "";

        workWithDB.connectToDatabase(); // nadviaze spojenie s db a vytvori tabulky ak nie su vytvorene

        String query = "SELECT employee_id FROM room WHERE id= '" + room.getId() + "';";
        workWithDB.setResultSet(workWithDB.getStatement().executeQuery(query));
        workWithDB.getResultSet().next();

        int employeeID = workWithDB.getResultSet().getInt("employee_id");

        query = "SELECT first_name FROM employee WHERE id= '" + employeeID + "';";
        workWithDB.setResultSet(workWithDB.getStatement().executeQuery(query));
        if(workWithDB.getResultSet().next()){
            firstName = workWithDB.getResultSet().getString("first_name");

            query = "SELECT last_name FROM employee WHERE id= '" + employeeID + "';";
            workWithDB.setResultSet(workWithDB.getStatement().executeQuery(query));
            workWithDB.getResultSet().next();

            lastName = workWithDB.getResultSet().getString("last_name");
            workWithDB.closeConnection();
        }
        else
            firstName = "No employee";

        return firstName + " " + lastName;
    }

    public void textFieldData(TextField textField, String data){
        textField.setText(data);
    }

    public void displayRoomDetailData() throws SQLException {
        DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
        roomNumber.setText("Room number " + room.getRoomNum() + ".");
        textFieldData(tfAvailability, Byte.toString(room.getAvailability()));
        textFieldData(tfNumOfBeds, Integer.toString(room.getNumOfBeds()));
        textFieldData(tfPrice, Float.toString(room.getPriceForNight()));
        labelEmployeeName.setText(getEmployeeName(room));
        // zmena date na string
        if (room.getDateFrom() != null) {
            String strDateFrom = dateFormat.format(room.getDateFrom());
            textFieldData(tfDateFrom, strDateFrom);
        } else
            textFieldData(tfDateFrom, "Unfilled");
        if (room.getDateTo() != null) {
            String strDateTo = dateFormat.format(room.getDateTo());
            textFieldData(tfDateTo, strDateTo);
        } else
            textFieldData(tfDateTo, "Unfilled");
    }

    public boolean isValidDate(SimpleDateFormat simpleDateFormat, String date){
        try {
            simpleDateFormat.parse(date);
            return true;
        } catch (ParseException e) {
            return false;
        }
    }

    public Date changeToDate(SimpleDateFormat simpleDateFormat, String date){
        Date dt = null;
        try {
            dt = simpleDateFormat.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return dt;
    }

    public boolean updateRoomByQuery(Room room){
        try {
            // treba spravit transakciu
            workWithDB.connectToDatabase(); // nadviaze spojenie s db a vytvori tabulky ak nie su vytvorene
            workWithDB.getConn().setAutoCommit(false);

            String query = "UPDATE Room SET availibility=?, number_of_beds=?, price_for_night=?, date_from=?, date_to=? WHERE id=?;";
            PreparedStatement preparedStatement = workWithDB.getConn().prepareStatement(query);
            preparedStatement.setByte(1, room.getAvailability());
            preparedStatement.setInt(2, room.getNumOfBeds());
            preparedStatement.setDouble(3, room.getPriceForNight());
            preparedStatement.setDate(4, room.getDateFrom());
            preparedStatement.setDate(5, room.getDateTo());
            preparedStatement.setInt(6, room.getId());
            preparedStatement.executeUpdate();

            workWithDB.getConn().commit();
            workWithDB.getConn().setAutoCommit(true);
            workWithDB.closeConnection();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean isValidPrice(String price){
        try{
            Float.parseFloat(price);
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

    public void  updateRoomInfo(){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MM.yyyy");
        // podmienka zisti, ci sa zadane udaje daju convertnut na potrebne datove typy
        if (!isValidDate(simpleDateFormat, tfDateFrom.getText()) || !isValidDate(simpleDateFormat, tfDateTo.getText()) || !isValidPrice(tfPrice.getText())) {
            System.out.println("Skontroluj zadane datumy a cenu");
            return;
        }

        if (!isValidInt(tfNumOfBeds.getText())){
            System.out.println("Skontroluj zadene cislo v Number of Beds");
            return;
        }

        java.sql.Date dateFrom = new java.sql.Date(changeToDate(simpleDateFormat, tfDateFrom.getText()).getTime());
        java.sql.Date dateTo = new java.sql.Date(changeToDate(simpleDateFormat, tfDateTo.getText()).getTime());

        Room newRoom = new Room(room.getId(), room.getRoomNum(), Byte.parseByte(tfAvailability.getText()), Integer.parseInt(tfNumOfBeds.getText()),
                Float.parseFloat(tfPrice.getText()), dateFrom, dateTo);
        // ak sa neupdatnu udaje v databaze, tak ich nezmenim ani objektu room

        if (!updateRoomByQuery(newRoom)) {
            System.out.println("Nepodarilo sa updatnut udaje v databaze.");
            return;
        }

        newRoom.setId(room.getId());
        room = newRoom;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            displayRoomDetailData(); // zobrazia sa vsetky udaje pre danu izbu
        } catch (SQLException e) {
            e.printStackTrace();
        }

        backButton.setOnAction(actionEvent -> {
            try {
                switchScene.switchToManageHotelRooms(actionEvent, hotel);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        saveChangesButton.setOnAction(actionEvent -> {
            updateRoomInfo();
        });
    }
}