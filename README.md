# Smart Airport Operations System (Java 21)

A console simulation of basic airport operations that demonstrates core DSA concepts.

| Feature | DSA concept | File |
|---|---|---|
| Passenger records (add / delete / update / search) | Dynamic array (`ArrayList`), linear search | `PassengerManager.java` |
| Sort by name, flight number or boarding time | Merge Sort (stable, O(n log n)) | `MergeSort.java` |
| Check-in line | Queue (FIFO), linked list | `CheckInQueue.java` |
| Undo and recent history | Stack (LIFO), linked list | `LinkedStack.java`, `Action.java`, `AirportSystem.java` |
| Terminal map | Graph, adjacency list | `TerminalGraph.java` |
| Shortest route between terminals | BFS (queue + visited array + parent array) | `TerminalGraph.shortestPath` |
| Explore connected terminals | DFS (recursion + visited array) | `TerminalGraph.explore` |

## Run in VS Code

1. Install the **Extension Pack for Java** (Microsoft) and make sure JDK 21 is installed.
2. File > Open Folder > select the `SmartAirport` folder.
3. Open `src/Main.java` and click **Run** above `main` (or press F5).
4. Interact in the Terminal panel.

## Run from a terminal

```
cd SmartAirport
javac -d out src/*.java
java -cp out Main
```

## What Undo covers

Add, delete, update, joining the queue, and checking a passenger in (they return to the front of the queue).
Note: if you delete a passenger who was in the queue, undoing the delete restores the record but not their queue spot.

## Ideas to extend

- Save/load passengers to a file.
- Weighted edges (walking minutes) and Dijkstra instead of BFS.
- Binary search on a list sorted by ID.
