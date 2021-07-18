package ORM;

import javax.persistence.*;

@Entity(name="hotel")
public class HotelORM {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Integer id;
    private String name;
    private String phone;
    private String mail;
    private Double total_sales;
    private Integer num_of_rooms;
    private Integer num_of_employees;
    private Integer address_id;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public Double getTotal_sales() {
        return total_sales;
    }

    public void setTotal_sales(Double total_sales) {
        this.total_sales = total_sales;
    }

    public Integer getNum_of_rooms() {
        return num_of_rooms;
    }

    public void setNum_of_rooms(Integer num_of_rooms) {
        this.num_of_rooms = num_of_rooms;
    }

    public Integer getNum_of_employees() {
        return num_of_employees;
    }

    public void setNum_of_employees(Integer num_of_employees) {
        this.num_of_employees = num_of_employees;
    }

    public Integer getAddress_id() {
        return address_id;
    }

    public void setAddress_id(Integer address_id) {
        this.address_id = address_id;
    }
}