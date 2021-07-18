package ORM;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.sql.Date;

@Entity (name="room")
public class RoomORM {
    @Id
    private Integer id;
    private Integer room_num;
    private Integer number_of_beds;
    private Float price_for_night;
    private Byte availibility;
    private Date date_from;
    private Date date_to;
    private String hotel_id;
    private String employee_id;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getRoom_num() {
        return room_num;
    }

    public void setRoom_num(Integer room_um) {
        this.room_num = room_um;
    }

    public Integer getNumber_of_beds() {
        return number_of_beds;
    }

    public void setNumber_of_beds(Integer number_of_beds) {
        this.number_of_beds = number_of_beds;
    }

    public Byte getAvailibility() {
        return availibility;
    }

    public void setAvailibility(Byte availibility) {
        this.availibility = availibility;
    }

    public Float getPrice_for_night() {
        return price_for_night;
    }

    public void setPrice_for_night(Float price_for_night) {
        this.price_for_night = price_for_night;
    }

    public Date getDate_from() {
        return date_from;
    }

    public void setDate_from(Date date_from) {
        this.date_from = date_from;
    }

    public Date getDate_to() {
        return date_to;
    }

    public void setDate_to(Date date_to) {
        this.date_to = date_to;
    }

    public String getHotel_id() {
        return hotel_id;
    }

    public void setHotel_id(String hotel_id) {
        this.hotel_id = hotel_id;
    }

    public String getEmployee_id() {
        return employee_id;
    }

    public void setEmployee_id(String employee_id) {
        this.employee_id = employee_id;
    }
}
