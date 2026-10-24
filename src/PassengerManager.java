import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Stores passengers in a dynamic array (ArrayList) and supports
 * add, delete, update, search and sort (via Merge Sort).
 */
public class PassengerManager {
    private final List<Passenger> passengers = new ArrayList<>();

    /** Adds a passenger. Returns false if the ID already exists. */
    public boolean add(Passenger p) {
        if (findById(p.getId()) != null) return false;
        passengers.add(p);
        return true;
    }

    /** Linear search by ID. Returns null if not found. */
    public Passenger findById(int id) {
        for (Passenger p : passengers) {
            if (p.getId() == id) return p;
        }
        return null;
    }

    /** Removes and returns the passenger with this ID (null if not found). */
    public Passenger remove(int id) {
        for (int i = 0; i < passengers.size(); i++) {
            if (passengers.get(i).getId() == id) return passengers.remove(i);
        }
        return null;
    }

    /** Overwrites name, flight and boarding time. Returns false if ID not found. */
    public boolean update(int id, String name, String flight, String boardingTime) {
        Passenger p = findById(id);
        if (p == null) return false;
        p.setName(name);
        p.setFlightNumber(flight);
        p.setBoardingTime(boardingTime);
        return true;
    }

    /** Case-insensitive partial match on name. */
    public List<Passenger> searchByName(String query) {
        List<Passenger> result = new ArrayList<>();
        String q = query.toLowerCase();
        for (Passenger p : passengers) {
            if (p.getName().toLowerCase().contains(q)) result.add(p);
        }
        return result;
    }

    /** Case-insensitive exact match on flight number. */
    public List<Passenger> searchByFlight(String flight) {
        List<Passenger> result = new ArrayList<>();
        for (Passenger p : passengers) {
            if (p.getFlightNumber().equalsIgnoreCase(flight)) result.add(p);
        }
        return result;
    }

    /** Sorts the stored records using Merge Sort and the given comparator. */
    public void sortBy(Comparator<Passenger> cmp) {
        Passenger[] arr = passengers.toArray(new Passenger[0]);
        MergeSort.sort(arr, cmp);
        passengers.clear();
        for (Passenger p : arr) passengers.add(p);
    }

    public List<Passenger> getAll() { return new ArrayList<>(passengers); }
    public int size() { return passengers.size(); }
    public boolean isEmpty() { return passengers.isEmpty(); }
}
