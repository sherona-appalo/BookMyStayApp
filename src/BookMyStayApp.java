import java.io.*;
import java.util.*;

class RoomInventory {

    private Map<String,Integer> availability;

    public RoomInventory() {
        availability = new HashMap<>();
        availability.put("Single",5);
        availability.put("Double",3);
        availability.put("Suite",2);
    }

    public Map<String,Integer> getInventory() {
        return availability;
    }

    public void setRoomCount(String type,int count) {
        availability.put(type,count);
    }

    public void showInventory() {

        System.out.println("Current Inventory:");

        for(String type : availability.keySet()) {
            System.out.println(type + ": " + availability.get(type));
        }
    }
}

class FilePersistenceService {


    public void saveInventory(RoomInventory inventory,String filePath) {

        try {

            FileWriter writer = new FileWriter(filePath);

            for(Map.Entry<String,Integer> entry :
                    inventory.getInventory().entrySet()) {

                writer.write(entry.getKey() + "=" + entry.getValue() + "\n");
            }

            writer.close();

            System.out.println("Inventory saved to file.");

        }
        catch(IOException e) {
            System.out.println("Error saving inventory.");
        }
    }

    public void loadInventory(RoomInventory inventory,String filePath) {

        try {

            BufferedReader reader =
                    new BufferedReader(new FileReader(filePath));

            String line;

            while((line = reader.readLine()) != null) {

                String[] parts = line.split("=");

                String roomType = parts[0];
                int count = Integer.parseInt(parts[1]);

                inventory.setRoomCount(roomType,count);
            }

            reader.close();

            System.out.println("Inventory loaded from file.");

        }
        catch(IOException e) {
            System.out.println("Error loading inventory.");
        }
    }
}

public class BookMyStayApp {

    public static void main(String[] args) {

        System.out.println("Data Persistence & Recovery\n");

        RoomInventory inventory = new RoomInventory();

        FilePersistenceService persistence =
                new FilePersistenceService();

        String filePath = "inventory.txt";

        // Save inventory
        persistence.saveInventory(inventory,filePath);

        // Simulate system restart
        RoomInventory newInventory = new RoomInventory();

        // Load inventory
        persistence.loadInventory(newInventory,filePath);

        newInventory.showInventory();
    }
}