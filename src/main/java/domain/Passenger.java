package domain;

import org.apache.log4j.Logger;

import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

public class Passenger implements Runnable {

    private static Logger logger = Logger.getLogger(Passenger.class.getName());
    private static AtomicInteger passengersAmount = new AtomicInteger(0);

    private int id;
    private Floor startFloor;
    private Floor endFloor;
    private Building location;

    public Passenger(Floor startFloor, Floor endFloor, Building location) {
        this.id = passengersAmount.incrementAndGet();
        this.startFloor = startFloor;
        this.endFloor = endFloor;
        this.location = location;
    }

    public static Passenger getPassengerForLocation(Building location) {
        Floor startFloor;
        Floor endFloor;
        do {
            startFloor = location.getFloors().get(new Random().nextInt(location.getFloors().size()));
            endFloor = location.getFloors().get(new Random().nextInt(location.getFloors().size()));
        } while (startFloor.equals(endFloor));
        Passenger passenger = new Passenger(startFloor, endFloor, location);
        startFloor.getPassengersAwaited().add(passenger);
        return passenger;
    }

    Floor getEndFloor() {
        return endFloor;
    }

    private boolean getIn(Elevator elevator) throws InterruptedException {
        try {

            startFloor.getLock().lock();
            startFloor.getElevatorArrived().await();
            if (elevator.getSemaphore().tryAcquire()) {
                startFloor.getPassengersAwaited().remove(this);
                synchronized (elevator) {
                    elevator.getPassengers().add(this);
                }
                logger.info(this + " ENTERS the elevator at " + elevator.getCurrentFloor());
                return true;
            } else {
                return false;
            }
        } finally {
            startFloor.getLock().unlock();
        }
    }

    private boolean getOut(Elevator elevator) throws InterruptedException {
        try {
            endFloor.getLock().lock();
            endFloor.getElevatorArrived().await();
            if (endFloor.equals(elevator.getCurrentFloor())) {
                elevator.getSemaphore().release();
                logger.info(this + " LEAVES the elevator at " + elevator.getCurrentFloor());
                synchronized (elevator) {
                    elevator.getPassengers().remove(this);
                }
                endFloor.getPassengersDelivered().add(this);
                return true;
            } else return false;
        } finally {
            endFloor.getLock().unlock();
        }

    }

    @Override
    public void run() {
        Elevator elevator = location.getElevator();
        try {
            while (true) {
                if (getIn(elevator)) {
                    if (getOut(elevator)) {
                        break;
                    }
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String toString() {
        return "Passenger #" + id;
    }
}
