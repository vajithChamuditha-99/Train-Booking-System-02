import com.mongodb.client.*;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import org.bson.Document;
import java.io.*;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.Scanner;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicBoolean;
import static javafx.geometry.Pos.CENTER;

public class TrainStation extends Application {
    //creating global variables and array lists
    static final int SEATING_CAPACITY=42;
    private static ArrayList<Passenger> waitingRoom = new ArrayList<>();
    private static ArrayList<CheckBox> boxArr = new ArrayList<>();
    private static ArrayList<Passenger> tempAdd = new ArrayList<>();
    private static ArrayList<Passenger> tempRemove = new ArrayList<>();
    private static ArrayList<Passenger> queue= new ArrayList<>();
    private static ArrayList<Passenger> boardToBe = new ArrayList<>();
    private static ArrayList<Passenger> line = new ArrayList<>();

    public static void main(String [] args){
        //importing data from the previous project
        String[][] seat = new String[2][SEATING_CAPACITY];
        MongoClient mongo = MongoClients.create("mongodb://localhost:27017");
        MongoDatabase database = mongo.getDatabase("TrainBooking");
        MongoCollection<Document> collection = database.getCollection("BookingRecords"); //accessing the collection
        //long documents = database.getCollection("BookingRecords").countDocuments();
        try {
            /*if (documents != 0) {
            }*/
            FindIterable<Document> data = collection.find();
            for(Document temp :data){
                //loading elements from the file to the data structure
                int direction = temp.getInteger("direction");
                int seatNum = temp.getInteger("seatNumber")-1;
                seat[direction][seatNum]=temp.getString("name");
            }
            System.out.println("All Records Has been Imported Successfully");
            for (int x = 0; x < SEATING_CAPACITY; x++) {  //creating passenger objects
                if (seat[0][x] != null) {
                    Passenger passenger = new Passenger();
                    passenger.setName(seat[0][x]);     //setters Set attributes
                    passenger.setSeat(x);
                    waitingRoom.add(passenger);
                }
            }
        } catch (Exception e){ //if document is empty
            System.out.println("No data Found");
            //e.printStackTrace();
        }
        String green="\u001B[32m"; //for colors in text
        String normal="\u001B[0m";
        System.out.println("\n\n¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬");
        System.out.println("^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^");
        System.out.println(green + " ++ Welcome to Denuwara Manike Seat Booking System ++ " + normal);
        System.out.println("******************************************************");
        System.out.println("¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬¬");
        Application.launch();
    }

    public static boolean addToQueue() {
        AtomicBoolean boolVal = new AtomicBoolean(false);
        if (PassengerQueue.isFull()){  //checking the queue is full
            System.out.println("queue is full");
            boolVal.set(true);  //if queue is full dont let add new customers
        }
        if (waitingRoom.size() != 0 || queue.size() != 0) {
            if (queue.size() == 0) {
                //creating a random number 1-6
                int randomInt = ThreadLocalRandom.current().nextInt(1, 7);
                for (int x=0;x<randomInt;x++) {
                    if (waitingRoom.size() == 0)
                        //if waiting room get empty loop ends
                        break;
                    //adding passengers to queue list and removing them from waiting room
                    queue.add(waitingRoom.remove(0));
                }
            }
            FlowPane fPane = new FlowPane(20,20);
            Stage stage = new Stage();
            //checking box
            //if (boxArr.size()==0 || boxArr.size()==(tempAdd.size()+tempRemove.size())) {
                boxArr.clear();
                tempAdd.clear();
                tempRemove.clear();
                //creating checkboxes using for loop
                for (Passenger passenger : queue) {
                    CheckBox chBox = new CheckBox(passenger.getName() + " " + (passenger.getSeat() + 1));
                    //chBox.setMouseTransparent(true);
                    boxArr.add(chBox);
                    //adding checkboxes to an array list and adding them in to flow pane
                    fPane.getChildren().add(chBox);
                }
            //}
            /*else {
                for (CheckBox checkBox : boxArr){
                    fPane.getChildren().add(checkBox);
                }
            }*/
            //creating and adding buttons
            Button setOk=new Button("Confirm");
            Button setRemove=new Button("Remove");
            fPane.getChildren().addAll(setOk,setRemove);
            //System.out.println("Line List size - "+boxArr.size());
            setOk.setOnAction(event -> {
                //if passenger queue is full dont let add new passengers
                if (PassengerQueue.isFull()) {
                    System.out.println("queue is full");
                    stage.close();
                }else {
                    for (int i = 0; i < boxArr.size(); i++) {
                        //setting selected boxes identical
                        if (boxArr.get(i).isSelected()) {
                            boxArr.get(i).setStyle("-fx-background-color:green;");
                            boxArr.get(i).setSelected(false);
                            tempAdd.add(queue.get(i));
                            boxArr.get(i).setMouseTransparent(true);
                        }
                    }
                    //this if dont let to confirm or reset with empty boxes
                    if (queue.size() == (tempAdd.size() + tempRemove.size())) {
                        for (Passenger passenger : tempAdd) {
                            //adding passengers from queue list to main array and clearing queue list
                            queue.remove(passenger);
                            PassengerQueue.addToQueue(passenger);
                        }
                        for (Passenger passenger : tempRemove) {
                            //removing absent passengers
                            queue.remove(passenger);
                        }
                        boolVal.set(true);
                        stage.close();
                    }
                }
            });
            setRemove.setOnAction(event -> {
                //setting selected boxes identical
                for(int i=0;i<boxArr.size();i++){
                    if(boxArr.get(i).isSelected()){
                        boxArr.get(i).setStyle("-fx-background-color:red;");
                        boxArr.get(i).setSelected(false);
                        tempRemove.add(queue.get(i));
                        boxArr.get(i).setMouseTransparent(true);
                    }
                }
                if (queue.size() == (tempAdd.size()+tempRemove.size())) {
                    //adding passengers from queue list to main array and clearing queue list
                    for (Passenger passenger : tempAdd){
                        queue.remove(passenger);
                        PassengerQueue.addToQueue(passenger);
                    }for (Passenger passenger : tempRemove){
                        //removing absent passengers
                        queue.remove(passenger);
                    }
                    boolVal.set(true);
                    stage.close();
                }
            });
            Scene scene = new Scene(fPane,450,200);
            stage.setTitle("Train Booking System");
            stage.setScene(scene);
            stage.setResizable(false);
            stage.showAndWait();
        } else {
            //if waiting room is empty
            System.err.println("No data in waiting room");
            return false;
        }
        return boolVal.get(); //getting the value of boolVal and return it to atomic boolean
    }

    private static void viewAllCustomers(){
        //creating labels
        Label lbl1=new Label("In Train Queue");
        Label lbl3=new Label("In the Waiting Room");
        Label newLbl=new Label("\t\tNo one in here");
        lbl1.setFont(new Font("Arial",20));
        lbl3.setFont(new Font("Arial",20));
        newLbl.setFont(new Font("Times new roman",14));
        lbl1.setLayoutX(400);
        lbl3.setLayoutX(374);
        newLbl.setLayoutX(80);
        Pane pane1= new Pane(lbl1);
        Pane pane3=new Pane(lbl3);
        AnchorPane aPane = new AnchorPane();
        FlowPane showRoom=new FlowPane(20,20);
        //adding current queue passengers to flow pane
        int x=160;  //creating buttons using for loop
        int y=10;
        for (int i=0;i<SEATING_CAPACITY;i++){ //addding buttons to the pane
            Button btn = new Button();
            btn.setLayoutX(x);
            btn.setLayoutY(y);
            aPane.getChildren().add(btn);
            //checking passengers in passenger queue and display them with their name and seat number
            if (PassengerQueue.getPassengerQueue()[i]!=null){
                Passenger passenger = PassengerQueue.getPassengerQueue()[i];
                btn.setStyle("-fx-background-color:red"); //setting booked buttons into red
                btn.setText(passenger.getName() + " " + (i+1));
            }else{
                btn.setStyle("-fx-background-color:#33cc33");
                btn.setText("Seat "+(i+1));
            }
            btn.setMouseTransparent(true);
            //adding buttons in style to the pane
            y=y+30;
            if (y==220){
                if (x==160 | x==260 | x==360 | x==520 | x==620 | x==720){
                    if(x==360){ x=x+60;}
                    x=x+100;
                }
                y=10;
            }
        }
        if(waitingRoom.size()==0){
            showRoom.getChildren().add(newLbl);
        }
        //adding waiting room passengers to flow pane
        for(Passenger passenger : waitingRoom){
            Label lbl = new Label("  "+passenger.getName()+" \n Seat "+(passenger.getSeat()+1)+"  ");
            lbl.setStyle("-fx-background-color:#e6ac00");
            showRoom.getChildren().add(lbl);
        }
        Label lbl4=new Label("W\nI\nN\nD\nO\nW");
        Label lbl5=new Label("W\nI\nN\nD\nO\nW");
        lbl4.setLayoutX(100);
        lbl4.setLayoutY(8);
        lbl5.setLayoutX(800);
        lbl5.setLayoutY(8);
        lbl5.setBorder(new Border(new BorderStroke(Color.LIGHTGRAY,BorderStrokeStyle.SOLID,null,new BorderWidths(3))));
        lbl4.setBorder(new Border(new BorderStroke(Color.LIGHTGRAY,BorderStrokeStyle.SOLID,null,new BorderWidths(3))));
        lbl4.setAlignment(CENTER);
        lbl5.setAlignment(CENTER);
        lbl4.setFont(new Font(14));
        lbl5.setFont(new Font(14));
        lbl4.setMinHeight(208);
        lbl4.setMinWidth(40);
        lbl5.setMinHeight(208);
        lbl5.setMinWidth(40);
        Button button=new Button("Back to Menu");
        button.setLayoutY(240);
        button.setLayoutX(650);
        lbl4.setStyle("-fx-background-color:#3399ff");
        lbl5.setStyle("-fx-background-color:#3399ff");
        aPane.getChildren().addAll(lbl4,lbl5,button);
        VBox vBox=new VBox(pane1,aPane,pane3,showRoom);
        vBox.setSpacing(20.0);
        Scene scene = new Scene(vBox,960,580);
        Stage stage = new Stage();
        stage.setScene(scene);
        button.addEventHandler(MouseEvent.MOUSE_CLICKED,
                event -> stage.close());
        stage.setTitle("Train Booking System");
        stage.setResizable(false);
        stage.showAndWait();
    }

    public static void deleteCustomerFromQueue (){
        if (PassengerQueue.isEmpty()) { //checking the passenger queue is empty
            System.out.println("no data to process");
            return;
        }
        Boolean flag =Boolean.TRUE;
        while (flag){
            int seatNum;
            Scanner sc=new Scanner(System.in);
            //getting the seat number from the user
            System.out.println("Enter seat number to delete(1-42):");
            try {
                seatNum = sc.nextInt();
            } catch (InputMismatchException e) {
                System.out.println("Invalid Input");
                continue;
            }
            if( seatNum<1 || seatNum>SEATING_CAPACITY){
                //checking the input in the range of 1-42
                System.out.println("Enter a seat number between 1-42");
                continue;
            }
            //getting the passenger relevent to that input
            Passenger passenger = PassengerQueue.getPassengerQueue()[seatNum-1];
            if (passenger==null) {
                //if there is no passenger
                System.out.println("** No Records to Delete **");
            } else{ //if (PassengerQueue.getPassengerQueue()[seatNum]!=null) {
                // checking elements which match to the input
                System.out.print("Enter the name given when booking that Seat : ");
                String seatName=sc.next();
                if (seatName.equalsIgnoreCase(passenger.getName())){
                    //if entered name is equals to booked name removing the passenger
                    PassengerQueue.remove(seatNum-1);
                    System.out.println(passenger.getName()+" in seat "+(passenger.getSeat()+1)+" is Removed :)");
                }else {
                    System.out.println("Access Denied");
                }
            }
            flag=false;
        }
    }

    private static void runSimulationDetails() {
        //getting the variable in the PassengerQueue class in to objects
        PassengerQueue trainQueueMaxStay=new PassengerQueue();
        PassengerQueue trainQueueMaxTime=new PassengerQueue();
        PassengerQueue trainQueueMinTime=new PassengerQueue();
        PassengerQueue trainQueueLength=new PassengerQueue();
        //System.out.println(trainQueue.getMaxStayInQueue());
        //creating a random time
        int time = ThreadLocalRandom.current().nextInt(3,19);
        if (PassengerQueue.isEmpty()) {
            //checking passenger queue is empty
            System.out.println("no data to process");
            return;
        }
        for (Passenger passenger : PassengerQueue.getPassengerQueue()) {
            //getting passengers from train queue and board them to the train
            if (passenger!=null) {
                boardToBe.add(passenger);
            }
        }
        for (int i=0;i<SEATING_CAPACITY;i++){
            PassengerQueue.remove(i); //removing passengers from train queue
        }
        int totalTime = 0;
        while (!boardToBe.isEmpty() || !line.isEmpty()) {
            //if line list is empty,adding passengers to that list
            if (line.isEmpty()) {
                //generate random number
                int random = ThreadLocalRandom.current().nextInt(1, 7);
                //if boardToBe list size is smaller than random num then random num becomes list size
                if (boardToBe.size() < random) random = boardToBe.size();
                for (int i = 0; i < random; i++) {
                    //adding passengers from board list to line list before boarding to train
                    line.add(boardToBe.remove(0));
                    //System.out.println(line.size());
                }
                if (trainQueueMaxStay.getMaxStayInQueue() < line.size())
                    //setting up the variable
                    trainQueueMaxStay.setMaxStayInQueue(line.size());
            }
            for (Passenger passenger : line) {
                //setting up the variable
                passenger.setSecondsInQueue(passenger.getSecondsInQueue() + time);
            }
            int tempTime = line.remove(0).getSecondsInQueue();
            trainQueueLength.setLength((trainQueueLength.getLength() + 1));
            totalTime += tempTime;
            //setting up the variable
            if (trainQueueMaxTime.getFirst() > tempTime) trainQueueMaxTime.setFirst(tempTime);
            //setting up the variable
            if (trainQueueMinTime.getLast() < tempTime) trainQueueMinTime.setLast(tempTime);
        }
        //writing the details to a file
        try (PrintWriter out = new PrintWriter(new FileWriter("./SimulationDetails.txt"))) {
            out.println("*****The Simulation Details*****\n\n\n");
            out.println("Maximum length in the Queue: "+(trainQueueMaxStay.getMaxStayInQueue())+" Passengers");
            out.println("Average Time in the Queue: "+(totalTime/trainQueueLength.getLength())+" Seconds");
            out.println("Total passengers: "+trainQueueLength.getLength());
            out.println("Maximum Waiting time: "+trainQueueMinTime.getLast()+" Seconds");
        }catch (FileNotFoundException e ){
            System.out.println("File not found!!");
        }catch (Exception e){
            System.out.println("Something went wrong!!");
        }
        Label lbl1 = new Label("Maximum length in the Queue: "+(trainQueueMaxStay.getMaxStayInQueue())+" Passengers");
        Label lbl2 = new Label("Average Time in the Queue: "+(totalTime/trainQueueLength.getLength())+" Seconds");
        Label lbl3 = new Label("Total passengers: "+trainQueueLength.getLength());
        Label lbl4 = new Label("Maximum Waiting time: "+trainQueueMinTime.getLast()+" Seconds");
        Label lbl5 = new Label("--The Simulation Details--");
        Label lbl7=new Label("In the Waiting Room");
        Label newLbl=new Label("\t\tNo one in here");
        lbl1.setFont(new Font(13));
        lbl2.setFont(new Font(13));
        lbl3.setFont(new Font(13));
        lbl4.setFont(new Font(13));
        lbl5.setFont(new Font(18));
        lbl7.setFont(new Font("Arial",15));
        newLbl.setFont(new Font("Times new roman",14));
        lbl1.setLayoutX(150);
        lbl1.setLayoutY(70);
        lbl2.setLayoutX(150);
        lbl2.setLayoutY(120);
        lbl3.setLayoutX(150);
        lbl3.setLayoutY(170);
        lbl4.setLayoutX(150);
        lbl4.setLayoutY(220);
        lbl5.setLayoutX(290);
        lbl5.setLayoutY(20);
        lbl7.setLayoutX(120);
        Pane pane2=new Pane(lbl7);
        Button button = new Button("Back to Menu");
        button.setLayoutX(500);
        button.setLayoutY(280);
        FlowPane paneWroom = new FlowPane(15, 15);
        AnchorPane paneSimulationDet = new AnchorPane();
        paneWroom.getChildren().addAll(pane2);
        paneSimulationDet.getChildren().addAll(lbl1, lbl2, lbl3, lbl4, lbl5,button);
        if(waitingRoom.size()==0){
            paneWroom.getChildren().add(newLbl);
        }
        for (Passenger passenger : waitingRoom) {
            Label lbl = new Label("  " + passenger.getName() + " \n Seat " + (passenger.getSeat()+1) + "  ");
            lbl.setStyle("-fx-background-color:#e6ac00");
            paneWroom.getChildren().add(lbl);
        }
        VBox vBox = new VBox(paneSimulationDet,pane2,paneWroom);
        vBox.setSpacing(20.0);
        Scene scene = new Scene(vBox, 750, 500);
        Stage stage = new Stage();
        stage.setScene(scene);
        button.addEventHandler(MouseEvent.MOUSE_CLICKED,
                event -> stage.close());
        stage.setTitle("Train Booking System");
        stage.setResizable(false);
        stage.showAndWait();
        //System.out.println(Arrays.deepToString(PassengerQueue.getPassengerQueue()));
    }

    private static void saveToDatabase () {
        try {
            //creating a file output stream
            FileOutputStream fileOutputStream=new FileOutputStream("./DataInTheArray.txt");
            ObjectOutputStream objectOutputStream=new ObjectOutputStream(fileOutputStream);
            //getting passenger objects from the main array using getters
            for(Passenger passenger : PassengerQueue.getPassengerQueue()){
                //PassengerQueue.getPassengerQueue();
                if (passenger!= null){
                    objectOutputStream.writeObject(passenger);
                    //writing the passenger objects to the file
                }
            }
            objectOutputStream.close();
            //closing the stream
        //if any error occured
        } catch (FileNotFoundException e) {
            System.out.println("File not Found");
            //e.printStackTrace();
        }catch(Exception e){
            System.out.println("Something went Wrong");
        }finally {
            System.out.println("Data saved to the file Successfully!!");
        }
    }

    private static void getDataFromDatabase()  {
        try{
            File file=new File("./DataInTheArray.txt");
            //creating the object input stream
            FileInputStream fileInputStream=new FileInputStream(file);
            ObjectInputStream objectInputStream=new ObjectInputStream(fileInputStream);
            for (int i=0;i<PassengerQueue.getPassengerQueue().length-1;i++){
                //getting the data from file to the main array
                Passenger passenger=(Passenger) objectInputStream.readObject();
                //setting the passenger objects using setters
                PassengerQueue.addToQueue(passenger);
            }
            objectInputStream.close();
            //closing the stream
        } catch (IOException | ClassNotFoundException e) {
            //e.printStackTrace();
            System.out.println("Something went Wrong");
        }finally {
            //removing the passenger from the waiting room who are already in the queue
            for (int i=0;i<SEATING_CAPACITY;i++){
                if(PassengerQueue.getPassengerQueue()[i]!=null){
                    waitingRoom.remove(0);
                }
            }
            System.out.println("Data imported from the file Successfully!!");
        }
    }

    static void menu() {
        Scanner scan = new Scanner(System.in);
        while (true) {
            System.out.println("\n\nEnter \"A\" for Add Customers to the Queue"+
                    "\nEnter \"V\" for View all Customers"+
                    "\nEnter \"D\" for Delete Customer from the Queue"+
                    "\nEnter \"R\" for get Simulation Details and Report"
                    +"\nEnter \"S\" for Save data to the Database"
                    +"\nEnter \"L\" for Get data from the Database"
                    +"\nEnter \"Q\" for Quit from the Programme\n\n");
            System.out.print("Please select an option: ");
            String option = scan.nextLine();
            option = option.toUpperCase(); //convert user input to upper case
            switch (option) {
                case "A":
                    addToQueue();
                    break;
                case "V":
                    viewAllCustomers();
                    break;
                case "D":
                    deleteCustomerFromQueue();
                    break;
                case "R":
                    runSimulationDetails();
                    break;
                case "S":
                    saveToDatabase();
                    break;
                case "L":
                    getDataFromDatabase();
                    break;
                case "Q":
                    System.out.println("*************** Thank you for using!! ****************");
                    System.out.println("^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^");
                    System.exit(0);
                default: //if input is not in the case menu
                    System.err.println("Invalid Selection!! Please enter the relevant letter");
                    break;
            }
        }
    }

    @Override
    public void start(Stage primaryStage) {
        /*for (Passenger passenger1 : waitingRoom){           //For printing waitingRoom
            System.out.print(passenger1.getName());    //getters Get attributes
            System.out.println(passenger1.getSeat());
        }*/
        menu();
    }
}
