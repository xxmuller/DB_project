package TableModels;

public class CustomerTable {
    private Integer id;
    private String firstName;
    private String lastName;
    private String phone;
    private String mail;
    private Integer bookedNights;

    public CustomerTable(Integer id, String firstName, String lastName, String phone, String mail, Integer bookedNights){
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phone = phone;
        this.mail = mail;
        this.bookedNights = bookedNights;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public Integer getBookedNights() {
        return bookedNights;
    }

    public void setBookedNights(Integer bookedNights) {
        this.bookedNights = bookedNights;
    }
}
