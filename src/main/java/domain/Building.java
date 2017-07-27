package domain;

import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

public class Building {

    private static Logger logger = Logger.getLogger(Building.class.getName());

    private List<Floor> floors;
    private Elevator elevator;

    private Building() {
    }

    List<Floor> getFloors() {
        return floors;
    }

    private void setFloors(List<Floor> floors) {
        this.floors = floors;
    }

    Elevator getElevator() {
        return elevator;
    }

    public static Building getBuildingWithElevator(int height, Elevator elevator) {
        if (height > 0) {
            Building building = new Building();
            List<Floor> floors = new ArrayList<>();
            for (int i = 0; i < height; i++) {
                floors.add(new Floor(i));
            }
            building.setFloors(floors);
            if (elevator != null) {
                building.elevator = elevator;
                elevator.setFloors(floors);
                logger.info("Building created with " + building.getFloors().size() + " floors");
                return building;
            } else throw new IllegalArgumentException("elevator is null");
        } else throw new IllegalArgumentException(String.valueOf(height));
    }
}
