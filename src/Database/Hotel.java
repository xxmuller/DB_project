package Database;

public class Hotel {
    private int id;
    private String name;
    private String phone;
    private String mail;
    private double totalSales;
    private int numOfRooms;
    private int numOfEmployees;
    private Address address;

    public Hotel(String name, String phone, String mail, double totalSales, int numOfRooms, int numOfEmployees, Address address) {
        this.name = name;
        this.phone = phone;
        this.mail = mail;
        this.totalSales = totalSales;
        this.numOfRooms = numOfRooms;
        this.numOfEmployees = numOfEmployees;
        this.address = address;
    }

    public int getId(){
        return id;
    }

    public void setId(int id){
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public String getPhone() {
        return phone;
    }

    public String getMail() {
        return mail;
    }

    public double getTotalSales() {
        return totalSales;
    }

    public int getNumOfRooms() {
        return numOfRooms;
    }

    public void setNumOfRooms(int numOfRooms){
        this.numOfRooms = numOfRooms;
    }

    public int getNumOfEmployees() {
        return numOfEmployees;
    }

    public void setNumOfEmployees(int numOfEmployees){
        this.numOfEmployees = numOfEmployees;
    }

    public Address getAddress() {
        return address;
    }
}
