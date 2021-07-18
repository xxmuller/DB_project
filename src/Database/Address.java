package Database;

public class Address {
    private int id;
    private String city;
    private String street;
    private String houseNum;
    private String postalCode;
    private String country;

    public Address(String city, String street, String houseNum, String postalCode, String country) {
        this.city = city;
        this.street = street;
        this.houseNum = houseNum;
        this.postalCode = postalCode;
        this.country = country;
    }

    public int getId() { return id; }

    public void setId(int id) {
        this.id = id;
    }

    public String getCity() {
        return city;
    }

    public String getStreet() {
        return street;
    }

    public String getHouseNum() {
        return houseNum;
    }

    public String getPostalCode(){
        return postalCode;
    }

    public String getCountry() {
        return country;
    }
}
