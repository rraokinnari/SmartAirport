/**
 * A single passenger record.
 */
public class Passenger {
    private final int id;
    private String name;
    private String flightNumber;
    private String boardingTime; // format HH:mm (24-hour)
    private boolean checkedIn;

    public Passenger(int id, String name, String flightNumber, String boardingTime) {
        this.id = id;
        this.name = name;
        this.flightNumber = flightNumber;
        this.boardingTime = boardingTime;
        this.checkedIn = false;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public String getFlightNumber() { return flightNumber; }
    public String getBoardingTime() { return boardingTime; }
    public boolean isCheckedIn() { return checkedIn; }

    public void setName(String name) { this.name = name; }
    public void setFlightNumber(String flightNumber) { this.flightNumber = flightNumber; }
    public void setBoardingTime(String boardingTime) { this.boardingTime = boardingTime; }
    public void setCheckedIn(boolean checkedIn) { this.checkedIn = checkedIn; }

    /** Independent copy, used to remember the old state for undo. */
    public Passenger copy() {
        Passenger p = new Passenger(id, name, flightNumber, boardingTime);
        p.checkedIn = checkedIn;
        return p;
    }

    public static String tableHeader() {
        return String.format("%-6s %-22s %-9s %-9s %-12s", "ID", "Name", "Flight", "Boarding", "Status")
                + "\n" + "-".repeat(62);
    }

    @Override
    public String toString() {
        return String.format("%-6d %-22s %-9s %-9s %-12s",
                id, name, flightNumber, boardingTime, checkedIn ? "Checked in" : "Pending");
    }
}
