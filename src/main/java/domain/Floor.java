package domain;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Floor {

    private int level;
    private List<Passenger> passengersAwaited;
    private List<Passenger> passengersDelivered;
    private Lock lock = new ReentrantLock();
    private Condition elevatorArrived = lock.newCondition();

    public Floor(int level) {
        this.level = level;
        this.passengersAwaited = new ArrayList<>();
        this.passengersDelivered = new ArrayList<>();
    }

    List<Passenger> getPassengersAwaited() {
        return passengersAwaited;
    }

    Lock getLock() {
        return lock;
    }

    Condition getElevatorArrived() {
        return elevatorArrived;
    }

    List<Passenger> getPassengersDelivered() {
        return passengersDelivered;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Floor floor = (Floor) o;

        return level == floor.level;
    }

    @Override
    public int hashCode() {
        return level;
    }

    @Override
    public String toString() {
        return "Floor #" + level;
    }
}
