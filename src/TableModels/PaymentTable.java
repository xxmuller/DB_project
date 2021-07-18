package TableModels;

public class PaymentTable {
    private String id;
    private String billNum;
    private String price;
    private String paymentTime;
    private String paymentType;
    private String customerIdCard;
    private String customerId;

    public PaymentTable(String id, String billNum, String price, String paymentTime, String paymentType, String customerIdCard, String customerId){
        this.id = id;
        this.billNum = billNum;
        this.price = price;
        this.paymentTime = paymentTime;
        this.paymentType = paymentType;
        this.customerIdCard = customerIdCard;
        this.customerId = customerId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getBillNum() {
        return billNum;
    }

    public void setBillNum(String billNum) {
        this.billNum = billNum;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getPaymentTime() {
        return paymentTime;
    }

    public void setPaymentTime(String paymentTime) {
        this.paymentTime = paymentTime;
    }

    public String getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(String paymentType) {
        this.paymentType = paymentType;
    }

    public String getCustomerIdCard() {
        return customerIdCard;
    }

    public void setCustomerIdCard(String customerIdCard) {
        this.customerIdCard = customerIdCard;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }
}
