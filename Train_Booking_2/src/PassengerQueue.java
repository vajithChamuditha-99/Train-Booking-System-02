public class PassengerQueue {
    //creating variables
    private static Passenger[] QueueArray = new Passenger[42];
    private int first;
    private int last;
    private int maxStayInQueue;
    private int length;

    public static void addToQueue(Passenger passenger){
        QueueArray[passenger.getSeat()]=passenger;
    }

    public static Passenger[] getPassengerQueue() {
        return QueueArray;
    }

    public static void remove(int element) {
        //setting the element ro null
        QueueArray[element]=null;
    }

    public static boolean isFull(){
        for (int i=0;i<42;i++){
            if (QueueArray[i]==null){
                //checking array is empty
                return false;
            }
        }return true;
    }

    public static boolean isEmpty(){
        for (int i=0;i<42;i++ ){
            if (QueueArray[i]!=null){
                //checking array is not empty
                return false;
            }
        }
        return true;
    }

    public void display(){
    }
    //creating getters and setters
    public void setLength(int length){
        this.length=length;
    }
    public int getLength() {
        return length;
    }
    public void setMaxStayInQueue(int maxStayInQueue){
        this.maxStayInQueue=maxStayInQueue;
    }

    public int getMaxStayInQueue() {
        return maxStayInQueue;
    }

    public int getFirst() {
        return first;
    }
    public void setFirst(int first){
        this.first=first;
    }
    public int getLast() {
        return last;
    }
    public void setLast(int last){
        this.last=last;
    }
}
