package domain;

import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.concurrent.Semaphore;

public class Elevator implements Runnable {

    private static final long BETWEEN_FLOOR_DELAY = 500L;
    private static final int START_FLOOR = 0;
    private static Logger logger = Logger.getLogger(Elevator.class.getName());

    private List<Floor> floors;
    private Floor currentFloor;
    private Semaphore semaphore;
    private List<Passenger> passengers;

    private Elevator(int capacity) {
        this.semaphore = new Semaphore(capacity);
        this.passengers = new ArrayList<>();
    }

    public static Elevator getElevator(int capacity) {
        if (capacity > 0) {
            return new Elevator(capacity);
        } else throw new IllegalArgumentException(String.valueOf(capacity));
    }

    Floor getCurrentFloor() {
        return currentFloor;
    }

    Semaphore getSemaphore() {
        return semaphore;
    }

    List<Passenger> getPassengers() {
        return passengers;
    }

    void setFloors(List<Floor> floors) {
        this.floors = floors;
    }

    private void moveToFloor(Floor floor) {
        currentFloor = floor;
        logger.info("Elevator ARRIVED TO " + currentFloor);
        logger.info("Passengers awaits elevator : " + currentFloor.getPassengersAwaited());
        logger.info("Passengers in elevator : " + passengers);
        while (hasPassengersOUT() || hasPassengersIN()) {
            waitForPassengers();
        }
        logger.info("Passengers delivered : " + currentFloor.getPassengersDelivered());
        sleep(BETWEEN_FLOOR_DELAY);
        logger.info("Elevator DEPARTING FROM " + currentFloor);
    }

    private void waitForPassengers() {
        currentFloor.getLock().lock();
        currentFloor.getElevatorArrived().signalAll();
        currentFloor.getLock().unlock();
    }

    private boolean hasPassengersOUT() {
        synchronized (this) {
            for (Passenger passenger : passengers) {
                if (currentFloor.equals(passenger.getEndFloor())) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean hasPassengersIN() {
        return currentFloor.getPassengersAwaited().size() != 0 && semaphore.availablePermits() > 0;
    }

    private void sleep(long miliseconds) {
        try {
            Thread.sleep(miliseconds);
        } catch (InterruptedException e) {
            throw new RuntimeException();       // *NOP*
        }
    }

    @Override
    public void run() {
        ListIterator<Floor> floorListIterator = floors.listIterator();
        Floor upperFloorBound = floors.get(floors.size() - 1);
        Floor lowerFloorBound = floors.get(START_FLOOR);
        currentFloor = lowerFloorBound;
        int numberOfAwaitedPassengers = getNumberOfAwaitedPassengers();
        logger.info(numberOfAwaitedPassengers + " passengers awaits elevator");
        logger.info("Elevator starts work...");
        boolean directionUp = false;
        while (true) {
            if (currentFloor.equals(upperFloorBound)) {
                directionUp = false;
            }
            if (currentFloor.equals(lowerFloorBound)) {
                directionUp = true;
            }
            moveToFloor(directionUp ? floorListIterator.next() : floorListIterator.previous());
            if (getNumberOfDeliveredPassengers() == numberOfAwaitedPassengers && currentFloor.equals(lowerFloorBound)) {
                logger.info("All " + getNumberOfDeliveredPassengers() + " passengers delivered. Elevator stops.");
                break;
            }
        }
    }

    private int getNumberOfAwaitedPassengers() {
        int awaited = 0;
        for (Floor floor : floors) {
            awaited += floor.getPassengersAwaited().size();
        }
        return awaited;
    }

    private int getNumberOfDeliveredPassengers() {
        int delivered = 0;
        for (Floor floor : floors) {
            delivered += floor.getPassengersDelivered().size();
        }
        return delivered;
    }
}
