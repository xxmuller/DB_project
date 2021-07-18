package Database;

import java.sql.*;

public class WorkWithDB {
    private String database = "projektDBS"; // nazov databasy
    //private String url = "jdbc:mysql://localhost?useSSL=false";
    private String url = "jdbc:mysql://localhost?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC";
    private Connection conn = null; // vytvaram connection
    private Statement statement = null; // potrebne na vykonanie niektorych queries
    private ResultSet resultSet = null; // potrebne, ked budeme vypisovat udaje z tabulky
    private ResultSetMetaData resultSetMetaData = null; // tiez potrebne na vypisovanie udajov z tabulky

    public String getDatabase() {
        return database;
    }

    public Connection getConn() {
        return conn;
    }

    public void setStatement(Statement statement) {
        this.statement = statement;
    }

    public Statement getStatement() {
        return statement;
    }

    public void setResultSet(ResultSet resultSet) {
        this.resultSet = resultSet;
    }

    public ResultSet getResultSet() {
        return resultSet;
    }

    public void setResultSetMetaData(ResultSetMetaData resultSetMetaData) {
        this.resultSetMetaData = resultSetMetaData;
    }

    public ResultSetMetaData getResultSetMetaData() {
        return resultSetMetaData;
    }

    private void createTableAddress(){
        try {
            String sql = "CREATE TABLE IF NOT EXISTS Address"
                    + "  (id             INT NOT NULL AUTO_INCREMENT,"
                    + "   city           VARCHAR(512),"
                    + "   street         VARCHAR(512),"
                    + "   house_num      VARCHAR(10),"
                    + "   postal_code    VARCHAR(10),"
                    + "   country        VARCHAR(512),"
                    + "   PRIMARY KEY(id));";
            statement.executeUpdate(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void createTableHotel(){
        try {
            String sql = "CREATE TABLE IF NOT EXISTS Hotel"
                    + "  (id               INT NOT NULL AUTO_INCREMENT,"
                    + "   name             VARCHAR(50),"
                    + "   phone            VARCHAR(20),"
                    + "   mail             VARCHAR(254),"
                    + "   total_sales      DECIMAL(12, 2),"
                    + "   num_of_rooms     INT,"
                    + "   num_of_employees INT,"
                    + "   address_id       INT NOT NULL,"
                    + "   PRIMARY KEY(id),"
                    + "   FOREIGN KEY (address_id) REFERENCES Address(id));";
            statement.executeUpdate(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void createTableRole(){
        try {
            String sql = "CREATE TABLE IF NOT EXISTS Role"
                    + "  (role_name        VARCHAR(25) NOT NULL,"
                    + "   PRIMARY KEY(role_name));";
            statement.executeUpdate(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void createTableEmployee(){
        try {
            String sql = "CREATE TABLE IF NOT EXISTS Employee"
                    + "  (id               INT NOT NULL AUTO_INCREMENT,"
                    + "   first_name       VARCHAR(50),"
                    + "   last_name        VARCHAR(50),"
                    + "   phone            VARCHAR(20),"
                    + "   mail             VARCHAR(254),"
                    + "   date_from        DATE,"
                    + "   date_to          DATE,"
                    + "   salary           DECIMAL(7,2),"
                    + "   hotel_id         INT NOT NULL,"
                    + "   emp_role         VARCHAR(25) NOT NULL,"
                    + "   address_id       INT NOT NULL,"
                    + "   PRIMARY KEY(id),"
                    + "   FOREIGN KEY (hotel_id) REFERENCES Hotel(id),"
                    + "   FOREIGN KEY (emp_role) REFERENCES Role(role_name),"
                    + "   FOREIGN KEY (address_id) REFERENCES Address(id));";
            statement.executeUpdate(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void createTableRoom(){
        try {
            String sql = "CREATE TABLE IF NOT EXISTS Room"
                    + "  (id         INT NOT NULL AUTO_INCREMENT,"
                    + "   room_num         INT NOT NULL,"
                    + "   availibility     TINYINT(1),"
                    + "   number_of_beds   INT NOT NULL,"
                    + "   price_for_night  DECIMAL(7,2) NOT NULL,"
                    + "   date_from        DATE,"
                    + "   date_to          DATE,"
                    + "   hotel_id         INT NOT NULL,"
                    + "   employee_id      INT,"
                    + "   PRIMARY KEY(id),"
                    + "   FOREIGN KEY (hotel_id) REFERENCES Hotel(id),"
                    + "   FOREIGN KEY (employee_id) REFERENCES Employee(id) ON DELETE SET NULL);";
            statement.executeUpdate(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void createTableCustomer(){
        try {
            String sql = "CREATE TABLE IF NOT EXISTS Customer"
                    + "  (id               INT NOT NULL AUTO_INCREMENT,"
                    + "   id_card_num      INT NOT NULL,"
                    + "   first_name       VARCHAR(50),"
                    + "   last_name        VARCHAR(50),"
                    + "   phone            VARCHAR(20),"
                    + "   mail             VARCHAR(254),"
                    + "   booked_nights    INT NOT NULL,"
                    + "   room_id         INT NOT NULL,"
                    + "   hotel_id         INT NOT NULL,"
                    + "   address_id       INT NOT NULL,"
                    + "   PRIMARY KEY(id),"
                    + "   FOREIGN KEY (room_id) REFERENCES Room(id),"
                    + "   FOREIGN KEY (hotel_id) REFERENCES Hotel(id),"
                    + "   FOREIGN KEY (address_id) REFERENCES Address(id));";
            statement.executeUpdate(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void createTablePayment(){
        try {
            String sql = "CREATE TABLE IF NOT EXISTS Payment"
                    + "   (id              INT NOT NULL AUTO_INCREMENT,"
                    + "   bill_num         INT NOT NULL,"
                    + "   price            DECIMAL(8,2) NOT NULL,"
                    + "   payment_time     TIMESTAMP NOT NULL,"
                    + "   payment_type     VARCHAR(255),"
                    + "   customer_id      INT,"
                    + "   PRIMARY KEY(id),"
                    + "   FOREIGN KEY (customer_id) REFERENCES Customer(id) ON DELETE SET NULL);";
            statement.executeUpdate(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void createTables(){
        createTableAddress();
        createTableHotel();
        createTableRole();
        createTableEmployee();
        createTableRoom();
        createTableCustomer();
        createTablePayment();
    }

    public void connectToDatabase(){
        String username = "root";
        String password = "moro258";
        //String password = "ferosvec131198";
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection(url, username, password);
            PreparedStatement stmt = conn.prepareStatement("CREATE DATABASE IF NOT EXISTS " + database + ";");
            stmt.execute(); // vytvori databazu ak nie je vytvorena
            url = "jdbc:mysql://localhost:3306/" + database + "?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC";
            conn = DriverManager.getConnection(url, username, password); // pripojenie k databaze projektdbs
            statement = conn.createStatement();
            createTables(); // vytvori tabulky, ak este neexistuju
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void closeConnection(){
        try {
            if (conn != null)
                conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
