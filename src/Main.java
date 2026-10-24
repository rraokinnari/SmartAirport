import java.util.Comparator;
import java.util.List;
import java.util.Scanner;

/**
 * Console menu for the Smart Airport Operations System.
 */
public class Main {

    private static final Scanner in = new Scanner(System.in);
    private static final AirportSystem airport = new AirportSystem();
    private static final TerminalGraph map = TerminalGraph.sampleAirport();

    public static void main(String[] args) {
        airport.loadSampleData();
        System.out.println("=====================================");
        System.out.println("   SMART AIRPORT OPERATIONS SYSTEM");
        System.out.println("=====================================");
        System.out.println("(5 sample passengers and 3 queued check-ins are pre-loaded.)");

        boolean running = true;
        while (running) {
            printMenu();
            int choice = readInt("Choose an option: ");
            System.out.println();
            switch (choice) {
                case 1 -> addPassenger();
                case 2 -> deletePassenger();
                case 3 -> updatePassenger();
                case 4 -> searchPassengers();
                case 5 -> viewAll();
                case 6 -> sortPassengers();
                case 7 -> joinQueue();
                case 8 -> serveNext();
                case 9 -> viewQueue();
                case 10 -> System.out.println(airport.undo());
                case 11 -> viewHistory();
                case 12 -> shortestRoute();
                case 13 -> exploreTerminals();
                case 14 -> System.out.print(map.describe());
                case 15 -> addTerminalOrConnection();
                case 0 -> running = false;
                default -> System.out.println("Invalid option. Please choose from the menu.");
            }
        }
        System.out.println("Goodbye!");
    }

    // ---------- menu ----------

    private static void printMenu() {
        System.out.println();
        System.out.println("--------------- MENU ---------------");
        System.out.println(" PASSENGERS");
        System.out.println("  1. Add passenger          2. Delete passenger");
        System.out.println("  3. Update passenger       4. Search passengers");
        System.out.println("  5. View all passengers    6. Sort passengers (Merge Sort)");
        System.out.println(" CHECK-IN QUEUE (FIFO)");
        System.out.println("  7. Add passenger to queue 8. Serve next passenger");
        System.out.println("  9. View queue");
        System.out.println(" UNDO (LIFO)");
        System.out.println(" 10. Undo last operation   11. View recent history");
        System.out.println(" TERMINALS (Graph)");
        System.out.println(" 12. Shortest route (BFS)  13. Explore from terminal (DFS)");
        System.out.println(" 14. Show terminal map     15. Add terminal / connection");
        System.out.println("  0. Exit");
        System.out.println("------------------------------------");
    }

    // ---------- passenger actions ----------

    private static void addPassenger() {
        String name = readNonEmpty("Name: ");
        String flight = readNonEmpty("Flight number (e.g. AI-512): ").toUpperCase();
        String time = readTime("Boarding time (HH:mm, 24-hour): ");
        Passenger p = airport.addPassenger(name, flight, time);
        System.out.println("Added with ID " + p.getId() + ".");
    }

    private static void deletePassenger() {
        int id = readInt("Passenger ID to delete: ");
        System.out.println(airport.deletePassenger(id) ? "Passenger deleted." : "No passenger with ID " + id + ".");
    }

    private static void updatePassenger() {
        int id = readInt("Passenger ID to update: ");
        Passenger p = airport.passengers().findById(id);
        if (p == null) {
            System.out.println("No passenger with ID " + id + ".");
            return;
        }
        System.out.println("Press Enter to keep the current value.");
        String name = readLine("Name [" + p.getName() + "]: ");
        String flight = readLine("Flight [" + p.getFlightNumber() + "]: ");
        String time = readLine("Boarding time [" + p.getBoardingTime() + "]: ");

        if (name.isEmpty()) name = p.getName();
        flight = flight.isEmpty() ? p.getFlightNumber() : flight.toUpperCase();
        if (time.isEmpty()) {
            time = p.getBoardingTime();
        } else if (!isValidTime(time)) {
            System.out.println("Invalid time format. Update cancelled.");
            return;
        }
        airport.updatePassenger(id, name, flight, time);
        System.out.println("Passenger updated.");
    }

    private static void searchPassengers() {
        System.out.println("Search by: 1) ID  2) Name  3) Flight number");
        int option = readInt("Choose: ");
        switch (option) {
            case 1 -> {
                int id = readInt("ID: ");
                Passenger p = airport.passengers().findById(id);
                printPassengers(p == null ? List.of() : List.of(p));
            }
            case 2 -> printPassengers(airport.passengers().searchByName(readNonEmpty("Name (or part of it): ")));
            case 3 -> printPassengers(airport.passengers().searchByFlight(readNonEmpty("Flight number: ")));
            default -> System.out.println("Invalid choice.");
        }
    }

    private static void viewAll() {
        printPassengers(airport.passengers().getAll());
    }

    private static void sortPassengers() {
        if (airport.passengers().isEmpty()) {
            System.out.println("No passengers to sort.");
            return;
        }
        System.out.println("Sort by: 1) Name  2) Flight number  3) Boarding time");
        int option = readInt("Choose: ");
        Comparator<Passenger> cmp;
        switch (option) {
            case 1 -> cmp = Comparator.comparing(p -> p.getName().toLowerCase());
            case 2 -> cmp = Comparator.comparing(Passenger::getFlightNumber);
            case 3 -> cmp = Comparator.comparing(Passenger::getBoardingTime);
            default -> {
                System.out.println("Invalid choice.");
                return;
            }
        }
        airport.passengers().sortBy(cmp);
        System.out.println("Sorted using Merge Sort:");
        viewAll();
    }

    // ---------- queue actions ----------

    private static void joinQueue() {
        int id = readInt("Passenger ID to add to the check-in queue: ");
        System.out.println(airport.joinQueue(id));
    }

    private static void serveNext() {
        Passenger p = airport.serveNext();
        if (p == null) {
            System.out.println("The queue is empty.");
        } else {
            System.out.println("Now checking in: " + p.getName() + " (ID " + p.getId() + ", flight " + p.getFlightNumber() + ")");
            System.out.println("Passengers still waiting: " + airport.queue().size());
        }
    }

    private static void viewQueue() {
        List<Passenger> line = airport.queue().toList();
        if (line.isEmpty()) {
            System.out.println("The check-in queue is empty.");
            return;
        }
        System.out.println("Check-in queue (front -> back):");
        int pos = 1;
        for (Passenger p : line) {
            System.out.printf("  %d. %s (ID %d, %s)%n", pos++, p.getName(), p.getId(), p.getFlightNumber());
        }
    }

    // ---------- history ----------

    private static void viewHistory() {
        List<Action> recent = airport.recentHistory(10);
        if (recent.isEmpty()) {
            System.out.println("No operations recorded yet.");
            return;
        }
        System.out.println("Recent operations (newest first, " + airport.historySize() + " stored):");
        int n = 1;
        for (Action a : recent) System.out.println("  " + n++ + ". " + a.getDescription());
    }

    // ---------- graph actions ----------

    private static void shortestRoute() {
        System.out.print(map.describe());
        String from = readNonEmpty("From (name or number): ");
        String to = readNonEmpty("To (name or number): ");
        List<String> path = map.shortestPath(from, to);
        if (path == null) {
            System.out.println("Unknown terminal.");
        } else if (path.isEmpty()) {
            System.out.println("No route exists between those terminals.");
        } else {
            System.out.println("Shortest route (" + (path.size() - 1) + " step(s)):");
            System.out.println("  " + String.join(" -> ", path));
        }
    }

    private static void exploreTerminals() {
        System.out.print(map.describe());
        String start = readNonEmpty("Start from (name or number): ");
        List<String> order = map.explore(start);
        if (order == null) {
            System.out.println("Unknown terminal.");
        } else {
            System.out.println("Reachable terminals in DFS order (" + order.size() + "):");
            System.out.println("  " + String.join(" -> ", order));
        }
    }

    private static void addTerminalOrConnection() {
        System.out.println("1) Add a new terminal  2) Connect two terminals");
        int option = readInt("Choose: ");
        if (option == 1) {
            String name = readNonEmpty("New terminal name: ");
            System.out.println(map.addTerminal(name) ? "Terminal added." : "That terminal already exists.");
        } else if (option == 2) {
            System.out.print(map.describe());
            String a = readNonEmpty("First terminal (name or number): ");
            String b = readNonEmpty("Second terminal (name or number): ");
            System.out.println(map.addConnection(a, b)
                    ? "Connected."
                    : "Could not connect (unknown terminal, same terminal, or already connected).");
        } else {
            System.out.println("Invalid choice.");
        }
    }

    // ---------- display helper ----------

    private static void printPassengers(List<Passenger> list) {
        if (list.isEmpty()) {
            System.out.println("No passengers found.");
            return;
        }
        System.out.println(Passenger.tableHeader());
        for (Passenger p : list) System.out.println(p);
    }

    // ---------- input helpers ----------

    private static String readLine(String prompt) {
        System.out.print(prompt);
        if (!in.hasNextLine()) {
            System.out.println("\nInput ended. Exiting.");
            System.exit(0);
        }
        return in.nextLine().trim();
    }

    private static String readNonEmpty(String prompt) {
        while (true) {
            String s = readLine(prompt);
            if (!s.isEmpty()) return s;
            System.out.println("This field cannot be empty.");
        }
    }

    private static int readInt(String prompt) {
        while (true) {
            String s = readLine(prompt);
            try {
                return Integer.parseInt(s);
            } catch (NumberFormatException e) {
                System.out.println("Please enter a whole number.");
            }
        }
    }

    private static boolean isValidTime(String s) {
        return s.matches("([01]\\d|2[0-3]):[0-5]\\d");
    }

    private static String readTime(String prompt) {
        while (true) {
            String s = readLine(prompt);
            if (isValidTime(s)) return s;
            System.out.println("Use HH:mm, e.g. 09:30 or 18:05.");
        }
    }
}
