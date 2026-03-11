import java.util.*;

class RoomInventory {

    private Map<String, Integer> availability;

    public RoomInventory() {
        availability = new HashMap<>();
        availability.put("Single", 5);
        availability.put("Double", 3);
        availability.put("Suite", 2);
    }

    public void increaseRoom(String type) {
        availability.put(type, availability.get(type) + 1);
    }

    public int getAvailability(String type) {
        return availability.get(type);
    }
}

class CancellationService {

    private Stack<String> releasedRoomIds;

    private Map<String, String> reservationRoomTypeMap;

    public CancellationService() {

        releasedRoomIds = new Stack<>();
        reservationRoomTypeMap = new HashMap<>();
    }

    public void registerBooking(String reservationId, String roomType) {

        reservationRoomTypeMap.put(reservationId, roomType);
    }
    public void cancelBooking(String reservationId, RoomInventory inventory) {

        if (!reservationRoomTypeMap.containsKey(reservationId)) {
            System.out.println("Invalid reservation ID.");
            return;
        }

        String roomType = reservationRoomTypeMap.get(reservationId);

        inventory.increaseRoom(roomType);

        releasedRoomIds.push(reservationId);

        System.out.println("Booking cancelled successfully. Inventory restored for room type: " + roomType);
    }

    public void showRollbackHistory() {

        System.out.println("\nRollback History (Most Recent First):");

        while (!releasedRoomIds.isEmpty()) {
            System.out.println("Released Reservation ID: " + releasedRoomIds.pop());
        }
    }
}

public class BookMyStayApp {

    public static void main(String[] args) {

        System.out.println("Booking Cancellation\n");

        RoomInventory inventory = new RoomInventory();

        CancellationService cancelService = new CancellationService();

        cancelService.registerBooking("Single-1", "Single");

        cancelService.cancelBooking("Single-1", inventory);

        cancelService.showRollbackHistory();

        System.out.println("\nUpdated Single Room Availability: "
                + inventory.getAvailability("Single"));
    }
}