import domain.Building;
import domain.Elevator;
import domain.Passenger;
import org.apache.log4j.PropertyConfigurator;


public class Runner {
    private static final String LOG4J_PROPERTY_PATH = "src/main/resources/log4j.properties";
    private static final int ELEVATOR_CAPACITY = 4;
    private static final int BUILDING_HEIGHT = 10;
    private static int PASSENGERS_AMOUNT = 5;

    static {
        PropertyConfigurator.configure(LOG4J_PROPERTY_PATH);
    }

    public static void main(String[] args) throws InterruptedException {
        Elevator elevator = Elevator.getElevator(ELEVATOR_CAPACITY);
        Building building = Building.getBuildingWithElevator(BUILDING_HEIGHT, elevator);

        for (int i = 0; i < PASSENGERS_AMOUNT; i++) {
            Passenger passenger = Passenger.getPassengerForLocation(building);
            new Thread(passenger).start();
        }

        new Thread(elevator).start();

    }


}
