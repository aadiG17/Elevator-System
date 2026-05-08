import java.util.*;
import java.util.concurrent.ConcurrentSkipListSet;

public final class ElevatorController implements Runnable {

    private boolean stopController;

    private static Map<Integer, Elevator> upMovingMap = new HashMap<Integer, Elevator>();

    private static Map<Integer, Elevator> downMovingMap = new HashMap<Integer, Elevator>();

    private static List<Elevator> elevatorList = new ArrayList<Elevator>(16);

    private static final ElevatorController instance = new ElevatorController();

    private ElevatorController() {
        if (instance != null) {
            throw new IllegalStateException("Already instantiated");
        }
        setStopController(false);
        initializeElevators();
    }

    public static ElevatorController getInstance() {
        return instance;
    }


    public synchronized Elevator selectElevator(ElevatorRequest elevatorRequest) {

        Elevator elevator = null;

        ElevatorState elevatorState = getRequestedElevatorDirection(elevatorRequest);
        int requestedFloor = elevatorRequest.getRequestFloor();
        int targetFloor = elevatorRequest.getTargetFloor();

        elevator = findElevator(elevatorState, requestedFloor, targetFloor);

        // So that elevators can start moving again.
        notifyAll();
        return elevator;


    }

    private static void initializeElevators() {
        for (int i = 0; i < 16; i++) {
            Elevator elevator = new Elevator(i);
            Thread t = new Thread(elevator);
            t.start();

            elevatorList.add(elevator);
        }
    }

    private static ElevatorState getRequestedElevatorDirection(ElevatorRequest elevatorRequest) {
        ElevatorState elevatorState = null;
        int requestedFloor = elevatorRequest.getRequestFloor();
        int targetFloor = elevatorRequest.getTargetFloor();

        if (targetFloor - requestedFloor > 0) {
            elevatorState = ElevatorState.UP;
        } else {
            elevatorState = ElevatorState.DOWN;
        }

        return elevatorState;
    }

    private static Elevator findElevator(ElevatorState elevatorState, int requestedFloor, int targetFloor) {
        Elevator elevator = null;
        TreeMap<Integer, Integer> sortedKeyMap = new TreeMap<Integer, Integer>();

        if (elevatorState.equals(ElevatorState.UP)) {
            for (Map.Entry<Integer, Elevator> elvMap : upMovingMap.entrySet()) {
                Elevator elv = elvMap.getValue();
                Integer distance = requestedFloor - elv.getCurrentFloor();
                if (distance < 0 && elv.getElevatorState().equals(ElevatorState.UP)) {

                    continue;
                } else {
                    sortedKeyMap.put(Math.abs(distance), elv.getId());
                }
            }

            // TODO - potential NullPointerException
            Integer selectedElevatorId = sortedKeyMap.firstEntry().getValue();
            elevator = upMovingMap.get(selectedElevatorId);


        } else if (elevatorState.equals(ElevatorState.DOWN)) {
            for (Map.Entry<Integer, Elevator> elvMap : downMovingMap.entrySet()) {
                Elevator elv = elvMap.getValue();
                Integer distance = elv.getCurrentFloor() - requestedFloor;
                if (distance < 0 && elv.getElevatorState().equals(ElevatorState.DOWN)) {
                    continue;
                } else {
                    sortedKeyMap.put(Math.abs(distance), elv.getId());
                }
            }
            // TODO - potential NullPointerException
            Integer selectedElevatorId = sortedKeyMap.firstEntry().getValue();
            elevator = downMovingMap.get(selectedElevatorId);

        }

        // Instructing the selected elevator to stop/pass by relavent floors
        ElevatorRequest newRequest = new ElevatorRequest(elevator.getCurrentFloor(), requestedFloor);
        ElevatorState elevatorDirection = getRequestedElevatorDirection(newRequest);

        // helpful if we are moving in opposite direction to than that of request
        ElevatorRequest newRequest2 = new ElevatorRequest(requestedFloor, targetFloor);
        ElevatorState elevatorDirection2 = getRequestedElevatorDirection(newRequest2);

        NavigableSet<Integer> floorSet = elevator.floorStopsMap.get(elevatorDirection);
        if (floorSet == null) {
            floorSet = new ConcurrentSkipListSet<Integer>();
        }

        floorSet.add(elevator.getCurrentFloor());
        floorSet.add(requestedFloor);
        elevator.floorStopsMap.put(elevatorDirection, floorSet);

        NavigableSet<Integer> floorSet2 = elevator.floorStopsMap.get(elevatorDirection2);
        if (floorSet2 == null) {
            floorSet2 = new ConcurrentSkipListSet<Integer>();
        }

        floorSet2.add(requestedFloor);
        floorSet2.add(targetFloor);
        elevator.floorStopsMap.put(elevatorDirection2, floorSet2);

        return elevator;
    }


    public static synchronized void updateElevatorLists(Elevator elevator) {
        if (elevator.getElevatorState().equals(ElevatorState.UP)) {
            upMovingMap.put(elevator.getId(), elevator);
            downMovingMap.remove(elevator.getId());
        } else if (elevator.getElevatorState().equals(ElevatorState.DOWN)) {
            downMovingMap.put(elevator.getId(), elevator);
            upMovingMap.remove(elevator.getId());
        } else if (elevator.getElevatorState().equals(ElevatorState.STATIONARY)) {
            upMovingMap.put(elevator.getId(), elevator);
            downMovingMap.put(elevator.getId(), elevator);
        } else if (elevator.getElevatorState().equals(ElevatorState.MAINTAINANCE)) {
            upMovingMap.remove(elevator.getId());
            downMovingMap.remove(elevator.getId());
        }
    }

    @Override
    public void run() {
        stopController = false;
        while (true) {
            try {
                Thread.sleep(100);
                if (stopController) {
                    break;
                }
            } catch (InterruptedException e) {
                System.out.println(e.getStackTrace());
            }
        }
    }

    public void setStopController(boolean stop) {
        this.stopController = stop;

    }

    public synchronized List<Elevator> getElevatorList() {
        return elevatorList;
    }

    public boolean isStopController() {
        return stopController;
    }
}