package GUI;

import javafx.fxml.FXML;
import javafx.scene.control.*;


public class PayForBookingController {
    @FXML
    private TextField tfBookedNights, tfPaymentType;

    public TextField getTfBookedNights(){ return tfBookedNights; }

    public TextField getTfPaymentType(){ return tfPaymentType; }
}
