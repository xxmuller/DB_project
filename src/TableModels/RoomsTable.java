package TableModels;

public class RoomsTable {
    private Integer id;
    private Integer roomNum;
    private Byte availability;
    private Integer numOfBeds;
    private Float priceForNight;

    public RoomsTable(Integer id, Integer roomNum, Byte availability, Integer numOfBeds, Float priceForNight){
        this.id = id;
        this.roomNum = roomNum;
        this.availability = availability;
        this.numOfBeds = numOfBeds;
        this.priceForNight = priceForNight;
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

}
