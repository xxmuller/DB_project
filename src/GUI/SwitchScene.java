package GUI;

import Database.*;
import ORM.CustomerORM;
import ORM.RoomORM;
import javafx.event.ActionEvent;
import java.io.IOException;
import java.util.Optional;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class SwitchScene {

    public void switchToAddHotelScene(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("../GUI XML/AddHotelScene.fxml"));
        Parent parent = loader.load();
        Scene scene = new Scene(parent);
        Stage window = (Stage)((Node)event.getSource()).getScene().getWindow();

        window.setScene(scene);
        window.show();
    }

    public void switchToHomepageScene(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("../GUI XML/HomepageScene.fxml"));
        Parent parent = loader.load();
        Scene scene = new Scene(parent);
        Stage window = (Stage)((Node)event.getSource()).getScene().getWindow();

        window.setScene(scene);
        window.show();
    }

    public void switchToAddEmployeesScene(ActionEvent event, Hotel hotel) throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("../GUI XML/AddEmployeesScene.fxml"));
        Parent parent = loader.load();
        Scene scene = new Scene(parent);
        Stage window = (Stage)((Node)event.getSource()).getScene().getWindow();

        AddEmployeesController controller = loader.getController();
        controller.setHotel(hotel); //nadstavi sa hotel a su v nom vsetky potrebne udaje pre pridavanie zamestnancov a izieb
        controller.setButtonText();

        window.setScene(scene);
        window.show();
    }
    /*
     * Vyuzil som tu novy sposob nacitania udajov do controllera, ktory budeme potrebovat, ked budeme robit s tabulkami
     */
    public void switchToAddRoomsScene(ActionEvent event, Hotel hotel) throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("../GUI XML/AddRoomsScene.fxml"));
        Stage window = (Stage)((Node)event.getSource()).getScene().getWindow();
        // najprv vytvorim controller AddRoomsController pomocou konstruktura, kde mu predam udaje s ktorymi budem pracovat
        AddRoomsController controller = new AddRoomsController(hotel);
        // tuto nadstavim vytvoreny controller pre scenu, do ktorej sa idem prepnut
        // je potrebne v FXML dokumente sceny vymazat cely fx:controller
        // tabulka a jej columns musia mat fx:id v FXML dokumente
        // je potrebne implementnut Initializable v classe controllera, ktory bude ovladat danu scenu s tabulkou
        // metodu initialize overridnes, tato metoda sa samostatne zavola, ked je prideleny controller k FXML dokumentu
        loader.setController(controller);
        Parent parent = loader.load();
        Scene scene = new Scene(parent);

        window.setScene(scene);
        window.show();
    }

    public void switchToManageHotels(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("../GUI XML/ManageHotelsScene.fxml"));
        Stage window = (Stage)((Node)event.getSource()).getScene().getWindow();

        Parent parent = loader.load();
        Scene scene = new Scene(parent);

        window.setScene(scene);
        window.show();
    }

    public void switchToManageChosenHotel(ActionEvent event, Hotel hotel) throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("../GUI XML/ManageChosenHotelScene.fxml"));
        Stage window = (Stage)((Node)event.getSource()).getScene().getWindow();
        // najprv vytvorim controller AddRoomsController pomocou konstruktura, kde mu predam udaje s ktorymi budem pracovat
        ManageChosenHotelController controller = new ManageChosenHotelController(hotel);
        // tuto nadstavim vytvoreny controller pre scenu, do ktorej sa idem prepnut
        // je potrebne v FXML dokumente sceny vymazat cely fx:controller
        // tabulka a jej columns musia mat fx:id v FXML dokumente
        // je potrebne implementnut Initializable v classe controllera, ktory bude ovladat danu scenu s tabulkou
        // metodu initialize overridnes, tato metoda sa samostatne zavola, ked je prideleny controller k FXML dokumentu
        loader.setController(controller);

        Parent parent = loader.load();
        Scene scene = new Scene(parent);

        window.setScene(scene);
        window.show();
    }

    public void switchToBookNights(ActionEvent event, Hotel hotel) throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("../GUI XML/BookNightsScene.fxml"));
        Stage window = (Stage)((Node)event.getSource()).getScene().getWindow();
        // najprv vytvorim controller AddRoomsController pomocou konstruktura, kde mu predam udaje s ktorymi budem pracovat
        BookNightsController controller = new BookNightsController(hotel);
        // tuto nadstavim vytvoreny controller pre scenu, do ktorej sa idem prepnut
        // je potrebne v FXML dokumente sceny vymazat cely fx:controller
        // tabulka a jej columns musia mat fx:id v FXML dokumente
        // je potrebne implementnut Initializable v classe controllera, ktory bude ovladat danu scenu s tabulkou
        // metodu initialize overridnes, tato metoda sa samostatne zavola, ked je prideleny controller k FXML dokumentu
        loader.setController(controller);

        Parent parent = loader.load();
        Scene scene = new Scene(parent);

        window.setScene(scene);
        window.show();
    }

    public void switchToChooseCustomer(ActionEvent event, Hotel hotel, RoomORM roomORM) throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("../GUI XML/ChooseCustomerScene.fxml"));
        Stage window = (Stage)((Node)event.getSource()).getScene().getWindow();
        // najprv vytvorim controller AddRoomsController pomocou konstruktura, kde mu predam udaje s ktorymi budem pracovat
        ChooseCustomerController controller = new ChooseCustomerController(roomORM, hotel);
        // tuto nadstavim vytvoreny controller pre scenu, do ktorej sa idem prepnut
        // je potrebne v FXML dokumente sceny vymazat cely fx:controller
        // tabulka a jej columns musia mat fx:id v FXML dokumente
        // je potrebne implementnut Initializable v classe controllera, ktory bude ovladat danu scenu s tabulkou
        // metodu initialize overridnes, tato metoda sa samostatne zavola, ked je prideleny controller k FXML dokumentu
        loader.setController(controller);

        Parent parent = loader.load();
        Scene scene = new Scene(parent);

        window.setScene(scene);
        window.show();
    }

    public void switchToBookingAddCustomer(ActionEvent event, Hotel hotel, RoomORM roomORM) throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("../GUI XML/BookingAddCustomerScene.fxml"));
        Stage window = (Stage)((Node)event.getSource()).getScene().getWindow();
        // najprv vytvorim controller AddRoomsController pomocou konstruktura, kde mu predam udaje s ktorymi budem pracovat
        BookingAddCustomerController controller = new BookingAddCustomerController(roomORM, hotel);
        // tuto nadstavim vytvoreny controller pre scenu, do ktorej sa idem prepnut
        // je potrebne v FXML dokumente sceny vymazat cely fx:controller
        // tabulka a jej columns musia mat fx:id v FXML dokumente
        // je potrebne implementnut Initializable v classe controllera, ktory bude ovladat danu scenu s tabulkou
        // metodu initialize overridnes, tato metoda sa samostatne zavola, ked je prideleny controller k FXML dokumentu
        loader.setController(controller);

        Parent parent = loader.load();
        Scene scene = new Scene(parent);

        window.setScene(scene);
        window.show();
    }

    public void switchToManageHotelPayments(ActionEvent event, Hotel hotel) throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("../GUI XML/ManageHotelPaymentsScene.fxml"));
        Stage window = (Stage)((Node)event.getSource()).getScene().getWindow();
        // najprv vytvorim controller AddRoomsController pomocou konstruktura, kde mu predam udaje s ktorymi budem pracovat
        ManageHotelPaymentsController controller = new ManageHotelPaymentsController(hotel);
        // tuto nadstavim vytvoreny controller pre scenu, do ktorej sa idem prepnut
        // je potrebne v FXML dokumente sceny vymazat cely fx:controller
        // tabulka a jej columns musia mat fx:id v FXML dokumente
        // je potrebne implementnut Initializable v classe controllera, ktory bude ovladat danu scenu s tabulkou
        // metodu initialize overridnes, tato metoda sa samostatne zavola, ked je prideleny controller k FXML dokumentu
        loader.setController(controller);

        Parent parent = loader.load();
        Scene scene = new Scene(parent);

        window.setScene(scene);
        window.show();
    }

    public void switchToPaymentCustomer(ActionEvent event , Customer customer, Hotel hotel) throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("../GUI XML/PaymentCustomerScene.fxml"));
        Stage window = (Stage)((Node)event.getSource()).getScene().getWindow();
        // najprv vytvorim controller AddRoomsController pomocou konstruktura, kde mu predam udaje s ktorymi budem pracovat
        PaymentCustomerController controller = new PaymentCustomerController(customer, hotel);
        // tuto nadstavim vytvoreny controller pre scenu, do ktorej sa idem prepnut
        // je potrebne v FXML dokumente sceny vymazat cely fx:controller
        // tabulka a jej columns musia mat fx:id v FXML dokumente
        // je potrebne implementnut Initializable v classe controllera, ktory bude ovladat danu scenu s tabulkou
        // metodu initialize overridnes, tato metoda sa samostatne zavola, ked je prideleny controller k FXML dokumentu
        loader.setController(controller);

        Parent parent = loader.load();
        Scene scene = new Scene(parent);

        window.setScene(scene);
        window.show();
    }

    public void switchToManageHotelEmployees(ActionEvent event, Hotel hotel) throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("../GUI XML/ManageHotelEmployeesScene.fxml"));
        Stage window = (Stage)((Node)event.getSource()).getScene().getWindow();
        // najprv vytvorim controller AddRoomsController pomocou konstruktura, kde mu predam udaje s ktorymi budem pracovat
        ManageHotelEmployeesController controller = new ManageHotelEmployeesController(hotel);
        // tuto nadstavim vytvoreny controller pre scenu, do ktorej sa idem prepnut
        // je potrebne v FXML dokumente sceny vymazat cely fx:controller
        // tabulka a jej columns musia mat fx:id v FXML dokumente
        // je potrebne implementnut Initializable v classe controllera, ktory bude ovladat danu scenu s tabulkou
        // metodu initialize overridnes, tato metoda sa samostatne zavola, ked je prideleny controller k FXML dokumentu
        loader.setController(controller);

        Parent parent = loader.load();
        Scene scene = new Scene(parent);

        window.setScene(scene);
        window.show();
    }

    public void switchToAddEmployeesHotel(ActionEvent event, Hotel hotel) throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("../GUI XML/AddEmployeesHotelScene.fxml"));
        Stage window = (Stage)((Node)event.getSource()).getScene().getWindow();
        // najprv vytvorim controller AddRoomsController pomocou konstruktura, kde mu predam udaje s ktorymi budem pracovat
        AddEmployeesHotelController controller = new AddEmployeesHotelController(hotel);
        // tuto nadstavim vytvoreny controller pre scenu, do ktorej sa idem prepnut
        // je potrebne v FXML dokumente sceny vymazat cely fx:controller
        // tabulka a jej columns musia mat fx:id v FXML dokumente
        // je potrebne implementnut Initializable v classe controllera, ktory bude ovladat danu scenu s tabulkou
        // metodu initialize overridnes, tato metoda sa samostatne zavola, ked je prideleny controller k FXML dokumentu
        loader.setController(controller);

        Parent parent = loader.load();
        Scene scene = new Scene(parent);

        window.setScene(scene);
        window.show();
    }

    public void switchToEmployeeDetailInfo(ActionEvent event, Hotel hotel, Employee employee) throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("../GUI XML/EmployeeDetailInfoScene.fxml"));
        Stage window = (Stage)((Node)event.getSource()).getScene().getWindow();
        // najprv vytvorim controller AddRoomsController pomocou konstruktura, kde mu predam udaje s ktorymi budem pracovat
        EmployeeDetailInfoController controller = new EmployeeDetailInfoController(hotel, employee);
        // tuto nadstavim vytvoreny controller pre scenu, do ktorej sa idem prepnut
        // je potrebne v FXML dokumente sceny vymazat cely fx:controller
        // tabulka a jej columns musia mat fx:id v FXML dokumente
        // je potrebne implementnut Initializable v classe controllera, ktory bude ovladat danu scenu s tabulkou
        // metodu initialize overridnes, tato metoda sa samostatne zavola, ked je prideleny controller k FXML dokumentu
        loader.setController(controller);

        Parent parent = loader.load();
        Scene scene = new Scene(parent);

        window.setScene(scene);
        window.show();
    }

    public void switchToRoomDetailInfo(ActionEvent event, Hotel hotel, Room room) throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("../GUI XML/RoomDetailInfoScene.fxml"));
        Stage window = (Stage)((Node)event.getSource()).getScene().getWindow();
        // najprv vytvorim controller AddRoomsController pomocou konstruktura, kde mu predam udaje s ktorymi budem pracovat
        RoomDetailInfoController controller = new RoomDetailInfoController(hotel, room);
        loader.setController(controller);
        // po tomto musis v controllery vsetky operacie pre button implementovat ako EventHandlere alebo pomocou lambdy
        // dalej to uz poznas
        Parent parent = loader.load();
        Scene scene = new Scene(parent);

        window.setScene(scene);
        window.show();
    }

    public void switchToExtendStayScene(ActionEvent event, Hotel hotel, CustomerORM customerORM) throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("../GUI XML/ExtendStayScene.fxml"));
        Stage window = (Stage)((Node)event.getSource()).getScene().getWindow();
        // najprv vytvorim controller ExtendStayController pomocou konstruktura, kde mu predam udaje s ktorymi budem pracovat
        ExtendStayController controller = new ExtendStayController(hotel, customerORM);
        loader.setController(controller);

        Parent parent = loader.load();
        Scene scene = new Scene(parent);

        window.setScene(scene);
        window.show();
    }

    public void switchToExtendStayCustomersScene(ActionEvent event, Hotel hotel) throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("../GUI XML/ExtendStayCustomersScene.fxml"));
        Stage window = (Stage)((Node)event.getSource()).getScene().getWindow();
        // najprv vytvorim controller ExtendStayController pomocou konstruktura, kde mu predam udaje s ktorymi budem pracovat
        ExtendStayCustomersController controller = new ExtendStayCustomersController(hotel);
        loader.setController(controller);

        Parent parent = loader.load();
        Scene scene = new Scene(parent);

        window.setScene(scene);
        window.show();
    }

    public void switchToCustomerDetailInfo(ActionEvent event, Hotel hotel, Customer customer) throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("../GUI XML/CustomerDetailInfoScene.fxml"));
        Stage window = (Stage)((Node)event.getSource()).getScene().getWindow();
        // najprv vytvorim controller AddRoomsController pomocou konstruktura, kde mu predam udaje s ktorymi budem pracovat
        CustomerDetailInfoController controller = new CustomerDetailInfoController(hotel, customer);
        loader.setController(controller);

        Parent parent = loader.load();
        Scene scene = new Scene(parent);

        window.setScene(scene);
        window.show();
    }

    public void switchToManageHotelRooms(ActionEvent event, Hotel hotel) throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("../GUI XML/ManageHotelRoomsScene.fxml"));
        Stage window = (Stage)((Node)event.getSource()).getScene().getWindow();
        // najprv vytvorim controller AddRoomsController pomocou konstruktura, kde mu predam udaje s ktorymi budem pracovat
        ManageHotelRoomsController controller = new ManageHotelRoomsController(hotel);
        // tuto nadstavim vytvoreny controller pre scenu, do ktorej sa idem prepnut
        // je potrebne v FXML dokumente sceny vymazat cely fx:controller
        // tabulka a jej columns musia mat fx:id v FXML dokumente
        // je potrebne implementnut Initializable v classe controllera, ktory bude ovladat danu scenu s tabulkou
        // metodu initialize overridnes, tato metoda sa samostatne zavola, ked je prideleny controller k FXML dokumentu
        loader.setController(controller);

        Parent parent = loader.load();
        Scene scene = new Scene(parent);

        window.setScene(scene);
        window.show();
    }

    public void switchToManageHotelCustomers(ActionEvent event, Hotel hotel) throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("../GUI XML/ManageHotelCustomersScene.fxml"));
        Stage window = (Stage)((Node)event.getSource()).getScene().getWindow();
        // najprv vytvorim controller AddRoomsController pomocou konstruktura, kde mu predam udaje s ktorymi budem pracovat
        ManageHotelCustomersController controller = new ManageHotelCustomersController(hotel);
        // tuto nadstavim vytvoreny controller pre scenu, do ktorej sa idem prepnut
        // je potrebne v FXML dokumente sceny vymazat cely fx:controller
        // tabulka a jej columns musia mat fx:id v FXML dokumente
        // je potrebne implementnut Initializable v classe controllera, ktory bude ovladat danu scenu s tabulkou
        // metodu initialize overridnes, tato metoda sa samostatne zavola, ked je prideleny controller k FXML dokumentu
        loader.setController(controller);

        Parent parent = loader.load();
        Scene scene = new Scene(parent);

        window.setScene(scene);
        window.show();
    }
}
