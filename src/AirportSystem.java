import java.util.List;

/**
 * Ties together the passenger records, the check-in queue and the undo history.
 * Every state-changing operation records an Action on the stack.
 */
public class AirportSystem {

    private final PassengerManager passengers = new PassengerManager();
    private final CheckInQueue queue = new CheckInQueue();
    private final LinkedStack<Action> history = new LinkedStack<>();
    private int nextId = 1001;

    public PassengerManager passengers() { return passengers; }
    public CheckInQueue queue() { return queue; }

    // ---------- passenger operations ----------

    public Passenger addPassenger(String name, String flight, String boardingTime) {
        Passenger p = new Passenger(nextId++, name, flight, boardingTime);
        passengers.add(p);
        history.push(new Action(Action.Type.ADD, p,
                "Added passenger #" + p.getId() + " (" + p.getName() + ")"));
        return p;
    }

    public boolean deletePassenger(int id) {
        Passenger p = passengers.remove(id);
        if (p == null) return false;
        queue.removeById(id); // a deleted passenger cannot stay in the line
        history.push(new Action(Action.Type.DELETE, p,
                "Deleted passenger #" + id + " (" + p.getName() + ")"));
        return true;
    }

    public boolean updatePassenger(int id, String name, String flight, String boardingTime) {
        Passenger p = passengers.findById(id);
        if (p == null) return false;
        Passenger old = p.copy();
        passengers.update(id, name, flight, boardingTime);
        history.push(new Action(Action.Type.UPDATE, old,
                "Updated passenger #" + id + " (" + old.getName() + ")"));
        return true;
    }

    // ---------- check-in queue operations ----------

    /** Puts a passenger in the check-in line. Returns a message describing the result. */
    public String joinQueue(int id) {
        Passenger p = passengers.findById(id);
        if (p == null) return "No passenger with ID " + id + ".";
        if (p.isCheckedIn()) return p.getName() + " has already checked in.";
        if (queue.contains(id)) return p.getName() + " is already in the queue.";
        queue.enqueue(p);
        history.push(new Action(Action.Type.ENQUEUE, p,
                "Queued passenger #" + id + " (" + p.getName() + ")"));
        return p.getName() + " joined the check-in queue (position " + queue.size() + ").";
    }

    /** Serves the passenger at the front of the line. Returns null if nobody is waiting. */
    public Passenger serveNext() {
        Passenger p = queue.dequeue();
        if (p == null) return null;
        p.setCheckedIn(true);
        history.push(new Action(Action.Type.CHECK_IN, p,
                "Checked in passenger #" + p.getId() + " (" + p.getName() + ")"));
        return p;
    }

    // ---------- undo / history ----------

    public String undo() {
        Action a = history.pop();
        if (a == null) return "Nothing to undo.";

        Passenger p = a.getBefore();
        switch (a.getType()) {
            case ADD -> {
                passengers.remove(p.getId());
                queue.removeById(p.getId());
            }
            case DELETE -> passengers.add(p);
            case UPDATE -> passengers.update(p.getId(), p.getName(), p.getFlightNumber(), p.getBoardingTime());
            case ENQUEUE -> queue.removeById(p.getId());
            case CHECK_IN -> {
                p.setCheckedIn(false);
                queue.addFront(p); // they were at the front before being served
            }
        }
        return "Undone: " + a.getDescription();
    }

    /** Most recent actions first, up to 'limit' entries. */
    public List<Action> recentHistory(int limit) {
        List<Action> all = history.toList();
        return all.size() <= limit ? all : all.subList(0, limit);
    }

    public int historySize() { return history.size(); }

    // ---------- demo data ----------

    /** Loads a few passengers WITHOUT recording history, so Undo starts clean. */
    public void loadSampleData() {
        String[][] data = {
                {"Aarav Shetty", "6E-201", "09:30"},
                {"Meera Nair", "AI-512", "08:15"},
                {"Rohan Das", "UK-833", "11:45"},
                {"Sneha Kulkarni", "6E-201", "09:30"},
                {"Karthik Rao", "AI-512", "08:15"},
        };
        for (String[] d : data) {
            passengers.add(new Passenger(nextId++, d[0], d[1], d[2]));
        }
        queue.enqueue(passengers.findById(1001));
        queue.enqueue(passengers.findById(1002));
        queue.enqueue(passengers.findById(1003));
    }
}
