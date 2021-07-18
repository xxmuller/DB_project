package Database;

import java.sql.Date;

public class Employee {
    private int id;
    private String firstName;
    private String lastName;
    private String phone;
    private String mail;
    private Date dateFrom;
    private Date dateTo;
    private float salary;
    private String emp_role;
    private Address address;

    public Employee(String firstName, String lastName, String phone, String mail, Date dateFrom, Date dateTo, Float salary, String emp_role, Address address) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.phone = phone;
        this.mail = mail;
        this.dateFrom = dateFrom;
        this.dateTo = dateTo;
        this.salary = salary;
        this.emp_role = emp_role;
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

    public Date getDateFrom() {
        return dateFrom;
    }

    public Date getDateTo() {
        return dateTo;
    }

    public Float getSalary() {
        return salary;
    }

    public String getRole() {
        return emp_role;
    }

    public Address getAddress() {
        return address;
    }
}
