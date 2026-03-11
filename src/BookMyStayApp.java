import java.util.*;

class Reservation {

    private String guestName;
    private String roomType;

    public Reservation(String guestName, String roomType) {
        this.guestName = guestName;
        this.roomType = roomType;
    }

    public String getGuestName() {
        return guestName;
    }

    public String getRoomType() {
        return roomType;
    }
}

class BookingRequestQueue {

    private Queue<Reservation> requestQueue = new LinkedList<>();

    public void addRequest(Reservation r) {
        requestQueue.offer(r);
    }

    public Reservation getNextRequest() {
        return requestQueue.poll();
    }

    public boolean hasRequests() {
        return !requestQueue.isEmpty();
    }
}

class RoomInventory {

    private Map<String,Integer> availability = new HashMap<>();

    public RoomInventory() {
        availability.put("Single",3);
        availability.put("Double",2);
        availability.put("Suite",1);
    }

    public boolean hasRoom(String type) {
        return availability.get(type) > 0;
    }

    public void allocateRoom(String type) {
        availability.put(type, availability.get(type) - 1);
    }

    public void showInventory() {
        System.out.println("\nRemaining Inventory:");
        for(String k : availability.keySet()) {
            System.out.println(k + ": " + availability.get(k));
        }
    }
}

class RoomAllocationService {

    private Map<String,Integer> counters = new HashMap<>();

    public void allocateRoom(Reservation r, RoomInventory inventory) {

        String type = r.getRoomType();

        if(inventory.hasRoom(type)) {

            int count = counters.getOrDefault(type,0)+1;
            counters.put(type,count);

            inventory.allocateRoom(type);

            System.out.println(
                    "Booking confirmed for Guest: "
                            + r.getGuestName()
                            + ", Room ID: "
                            + type + "-" + count
            );

        } else {
            System.out.println("No rooms available for " + r.getGuestName());
        }
    }
}

class ConcurrentBookingProcessor implements Runnable {

    private BookingRequestQueue queue;
    private RoomInventory inventory;
    private RoomAllocationService allocationService;

    public ConcurrentBookingProcessor(
            BookingRequestQueue queue,
            RoomInventory inventory,
            RoomAllocationService allocationService) {

        this.queue = queue;
        this.inventory = inventory;
        this.allocationService = allocationService;
    }

    @Override
    public void run() {

        while(true) {

            Reservation reservation;

            synchronized(queue) {
                if(!queue.hasRequests()) break;
                reservation = queue.getNextRequest();
            }

            synchronized(inventory) {
                allocationService.allocateRoom(reservation,inventory);
            }
        }
    }
}

public class BookMyStayApp {

    public static void main(String[] args) {

        System.out.println("Concurrent Booking Simulation\n");

        BookingRequestQueue bookingQueue = new BookingRequestQueue();
        RoomInventory inventory = new RoomInventory();
        RoomAllocationService allocationService = new RoomAllocationService();

        bookingQueue.addRequest(new Reservation("Abhi","Single"));
        bookingQueue.addRequest(new Reservation("Vanmathi","Double"));
        bookingQueue.addRequest(new Reservation("Kural","Suite"));
        bookingQueue.addRequest(new Reservation("Subha","Single"));

        Thread t1 = new Thread(
                new ConcurrentBookingProcessor(
                        bookingQueue,inventory,allocationService));

        Thread t2 = new Thread(
                new ConcurrentBookingProcessor(
                        bookingQueue,inventory,allocationService));

        t1.start();
        t2.start();

        try {
            t1.join();
            t2.join();
        }
        catch(InterruptedException e) {
            System.out.println("Thread execution interrupted.");
        }

        inventory.showInventory();
    }
}