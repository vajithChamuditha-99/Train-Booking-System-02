import java.io.Serializable;

public class Passenger implements Serializable {
    private String name;
    private int seat;           //Attributes of Passengers
    private int secondsInQueue;

    public void setName(String name) {        //setter methods to set attributes
        this.name = name;
        //  System.out.println("add name class method ");
    }

    //creating getter and setters
    public void setSeat(int seat) {
        this.seat = seat;
    }

    public String getName() {
        return name;
    }

    public int getSeat() {
        return seat;
    }

    public void setSecondsInQueue(int secondsInQueue){
        this.secondsInQueue=secondsInQueue;
    }

    public int getSecondsInQueue() {
        return secondsInQueue;
    }
}