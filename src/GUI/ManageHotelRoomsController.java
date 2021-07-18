package GUI;

import Database.*;
import TableModels.CustomerTable;
import TableModels.EmployeeTable;
import TableModels.RoomsTable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class ManageHotelRoomsController implements Initializable {
    private ObservableList<RoomsTable> observableList = FXCollections.observableArrayList(); // vytvorenie listu, ktory prida udaje do tabulky
    private SwitchScene switchScene = new SwitchScene();
    private WorkWithDB workWithDB = new WorkWithDB();
    private Hotel hotel;

    @FXML
    private TableView<RoomsTable> tableView; // v tabulke budu iba udaje z classy RoomsTable
    @FXML
    private TableColumn<RoomsTable, Integer> columnId; // podla toho aky datovy typ je Id v classe RoomsTable som vytvoril <RoomsTable, Integer>
    @FXML
    private TableColumn <RoomsTable, Integer> columnRoomNum; // podla toho aky datovy typ je roomNum v classe RoomsTable som vytvoril <RoomsTable, Integer>
    @FXML
    private TableColumn <RoomsTable, Byte> columnAvailability; // podla toho aky datovy typ je availability v classe RoomsTable som vytvoril <RoomsTable, Byte>
    @FXML
    private TableColumn <RoomsTable, Integer> columnNumOfBeds; // podla toho aky datovy typ je numOfBeds v classe RoomsTable som vytvoril <RoomsTable, Integer>
    @FXML
    private TableColumn <RoomsTable, Float> columnPriceForNight; // podla toho aky datovy typ je priceForNight v classe RoomsTable som vytvoril <RoomsTable, Float>
    @FXML
    private Button backButton, detailInfoButton;
    @FXML
    private TextField textFieldNumOfBeds;

    public ManageHotelRoomsController(Hotel hotel){
        this.hotel = hotel;
    }

    public Room chosenRoom() {
        workWithDB.connectToDatabase(); // nadviaze spojenie s db a vytvori tabulky ak nie su vytvorene

        if (!tableView.isVisible()) {
            displayDataInTableView();
            workWithDB.closeConnection();
            return null;
        }

        if (tableView.getSelectionModel().getSelectedItem() != null) {
            try {
                RoomsTable selectedRoom = tableView.getSelectionModel().getSelectedItem();

                String query = "SELECT * FROM room WHERE id=" + selectedRoom.getId() + ";";
                workWithDB.setResultSet(workWithDB.getStatement().executeQuery(query));
                workWithDB.getResultSet().next();
                Room room = new Room(workWithDB.getResultSet().getInt("id"),
                        workWithDB.getResultSet().getInt("room_num"),
                        workWithDB.getResultSet().getByte("availibility"),
                        workWithDB.getResultSet().getInt("number_of_beds"),
                        workWithDB.getResultSet().getFloat("price_for_night"),
                        workWithDB.getResultSet().getDate("date_from"),
                        workWithDB.getResultSet().getDate("date_to"));
                room.setId(selectedRoom.getId());
                workWithDB.closeConnection();
                return room;
            } catch (SQLException e) {
                System.out.println("Nepodarili sa ziskat udaje o izbe");
                e.printStackTrace();
                workWithDB.closeConnection();
                return null;
            }
        }
        workWithDB.closeConnection();
        return null;
    }

    public void displayDataInTableView(){ // v tabulke sa zobrazia udaje izieb, ktore su v hoteli, ktory sme pridali
        try {
            String query = "SELECT id, room_num, availibility, number_of_beds, price_for_night " +
                    "FROM room " +
                    "WHERE hotel_id=" + hotel.getId() + ";";
            workWithDB.setResultSet(workWithDB.getStatement().executeQuery(query)); // query, ktore vytiahne iba potrebne udaje do tabulky izieb a vytiahne iba izby, ktore su v tomto hoteli
            // pokial nie je koniec udajov, ktore boli nacitane do ResultSet pomocou query, tak bude pokracovat v cykle
            while (workWithDB.getResultSet().next()){
                // pridanie jedneho zaznamu do listu, ktory sa potom zobrazi v tableView, udaje do neho taham z vysledku, ktory som dostal pomocou predosleho query
                observableList.add(new RoomsTable(workWithDB.getResultSet().getInt("id"),
                        workWithDB.getResultSet().getInt("room_num"),
                        workWithDB.getResultSet().getByte("availibility"),
                        workWithDB.getResultSet().getInt("number_of_beds"),
                        workWithDB.getResultSet().getFloat("price_for_night")));

            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        tableView.setItems(observableList); // zobrazenie v tabulke, ktora sa nachadza v scene, ktoru ovlada tento controller
    }

    public void detailInfoButtonClicked(ActionEvent actionEvent) throws IOException {
        Room room = chosenRoom();
        if (room != null) {
            try {
                switchScene.switchToRoomDetailInfo(actionEvent, hotel, room);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else
            System.out.println("Treba vybrat izbu!");
    }

    public void bedsFilterButtonClicked(){
        try {
            workWithDB.connectToDatabase(); // nadviaze spojenie s db a vytvori tabulky ak nie su vytvorene
            observableList = FXCollections.observableArrayList();
            String query = "SELECT id, room_num, availibility, number_of_beds, price_for_night " +
                    "FROM room " +
                    "WHERE number_of_beds = '"+Integer.parseInt(textFieldNumOfBeds.getText())+"'";
            workWithDB.setResultSet(workWithDB.getStatement().executeQuery(query)); // query, ktore vytiahne iba potrebne udaje do tabulky izieb a vytiahne iba izby, ktore su v tomto hoteli
            // pokial nie je koniec udajov, ktore boli nacitane do ResultSet pomocou query, tak bude pokracovat v cykle
            while (workWithDB.getResultSet().next()){
                // pridanie jedneho zaznamu do listu, ktory sa potom zobrazi v tableView, udaje do neho taham z vysledku, ktory som dostal pomocou predosleho query
                observableList.add(new RoomsTable(workWithDB.getResultSet().getInt("id"),
                        workWithDB.getResultSet().getInt("room_num"),
                        workWithDB.getResultSet().getByte("availibility"),
                        workWithDB.getResultSet().getInt("number_of_beds"),
                        workWithDB.getResultSet().getFloat("price_for_night")));
            }
            workWithDB.closeConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (NumberFormatException e) {
            System.out.println("Number of Beds musi byt cele cislo.");
        }
        tableView.setItems(observableList); // zobrazenie v tabulke, ktora sa nachadza v scene, ktoru ovlada tento controller
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // kazdej column nadstavi hodnotu podla toho ako sa nazyva privatna variable v classe RoomsTable
        columnId.setCellValueFactory(new PropertyValueFactory<>("id"));
        columnRoomNum.setCellValueFactory(new PropertyValueFactory<>("roomNum"));
        columnAvailability.setCellValueFactory(new PropertyValueFactory<>("availability"));
        columnNumOfBeds.setCellValueFactory(new PropertyValueFactory<>("numOfBeds"));
        columnPriceForNight.setCellValueFactory(new PropertyValueFactory<>("priceForNight"));

        workWithDB.connectToDatabase(); // nadviaze spojenie s db a vytvori tabulky ak nie su vytvorene
        displayDataInTableView(); // metoda zobrazi udaje z databazy do tabulky
        workWithDB.closeConnection();

        backButton.setOnAction(actionEvent -> {
            try {
                switchScene.switchToManageChosenHotel(actionEvent, hotel);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

    }
}

