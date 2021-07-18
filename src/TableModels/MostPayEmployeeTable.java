package TableModels;

public class MostPayEmployeeTable {
    private String id;
    private String name;
    private String firstName;
    private String lastName;
    private String employeeRole;
    private String salary;

    public MostPayEmployeeTable(String id, String name, String firstName, String lastName, String employeeRole, String salary){
        this.id = id;
        this.name = name;
        this.firstName = firstName;
        this.lastName = lastName;
        this.employeeRole = employeeRole;
        this.salary = salary;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public String getEmployeeRole() {
        return employeeRole;
    }

    public void setEmployeeRole(String employeeRole) {
        this.employeeRole = employeeRole;
    }

    public String getSalary() {
        return salary;
    }

    public void setSalary(String salary) {
        this.salary = salary;
    }
}
