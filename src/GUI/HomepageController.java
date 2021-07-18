package GUI;

import javafx.fxml.FXML;

import javafx.event.ActionEvent;
import java.io.IOException;

public class HomepageController {
    @FXML
    private SwitchScene switchScene = new SwitchScene();

    public void addHotelButtonClicked(ActionEvent actionEvent) throws IOException {
        switchScene.switchToAddHotelScene(actionEvent);
    }

    public void manageHotelButtonClicked(ActionEvent actionEvent) throws IOException {
        switchScene.switchToManageHotels(actionEvent);
    }
}
