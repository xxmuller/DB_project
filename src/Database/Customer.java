package Database;

public class Customer {
    private int id;
    private String firstName;
    private String lastName;
    private String phone;
    private String mail;
    private Integer bookedNights;
    private Integer roomNumber;
    private Address address;

    public Customer(Integer id, String firstName, String lastName, String phone, String mail, Integer bookedNights, Integer roomNumber, Address address) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phone = phone;
        this.mail = mail;
        this.bookedNights = bookedNights;
        this.roomNumber = roomNumber;
        this.address = address;
    }

    public int getId() { return id; }

    public void setId(int id) { this.id = id; }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getPhone() {
        return phone;
    }

    public String getMail() {
        return mail;
    }

    public Address getAddress() {
        return address;
    }

    public Integer getBookedNights() {
        return bookedNights;
    }

    public void setBookedNights(Integer bookedNights) {
        this.bookedNights = bookedNights;
    }

    public Integer getRoomNumber() {
        return roomNumber;
    }

    public void setRoomNumber(Integer roomNumber) {
        this.roomNumber = roomNumber;
    }
}
