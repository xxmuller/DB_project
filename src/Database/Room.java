package Database;

import java.sql.Date;

public class Room {
    private Integer id;
    private Integer roomNum;
    private Byte availability;
    private Integer numOfBeds;
    private Float priceForNight;
    private Date dateFrom;
    private Date dateTo;

    public Room(Integer id, Integer roomNum, Byte availability, Integer numOfBeds, Float priceForNight, Date dateFrom, Date dateTo){
        this.id = id;
        this.roomNum = roomNum;
        this.availability = availability;
        this.numOfBeds = numOfBeds;
        this.priceForNight = priceForNight;
        this.dateFrom = dateFrom;
        this.dateTo = dateTo;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getRoomNum() {
        return roomNum;
    }

    public void setRoomNum(Integer roomNum) {
        this.roomNum = roomNum;
    }

    public Byte getAvailability() {
        return availability;
    }

    public void setAvailability(Byte availability) {
        this.availability = availability;
    }

    public Integer getNumOfBeds() {
        return numOfBeds;
    }

    public void setNumOfBeds(Integer numOfBeds) {
        this.numOfBeds = numOfBeds;
    }

    public Float getPriceForNight() {
        return priceForNight;
    }

    public void setPriceForNight(Float priceForNight) {
        this.priceForNight = priceForNight;
    }

    public Date getDateFrom() {
        return dateFrom;
    }

    public void setDateFrom(Date dateFrom) {
        this.dateFrom = dateFrom;
    }

    public Date getDateTo() {
        return dateTo;
    }

    public void setDateTo(Date dateTo) {
        this.dateTo = dateTo;
    }

}
