import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;

/**
 * Airport terminals as an undirected graph stored as an adjacency list.
 * BFS finds the route with the fewest hops; DFS explores everything reachable.
 */
public class TerminalGraph {

    private final List<String> names = new ArrayList<>();
    private final List<List<Integer>> adj = new ArrayList<>();   // adjacency list
    private final Map<String, Integer> index = new HashMap<>();  // lower-case name -> vertex id

    // ---------- building the graph ----------

    public boolean addTerminal(String name) {
        String key = name.trim().toLowerCase();
        if (key.isEmpty() || index.containsKey(key)) return false;
        index.put(key, names.size());
        names.add(name.trim());
        adj.add(new ArrayList<>());
        return true;
    }

    /** Adds a two-way connection. Returns false if either terminal is unknown or already linked. */
    public boolean addConnection(String a, String b) {
        int u = resolve(a);
        int v = resolve(b);
        if (u < 0 || v < 0 || u == v) return false;
        if (adj.get(u).contains(v)) return false;
        adj.get(u).add(v);
        adj.get(v).add(u);
        return true;
    }

    /** Accepts a terminal name or its list number (1-based). Returns -1 if unknown. */
    public int resolve(String input) {
        String s = input.trim();
        try {
            int n = Integer.parseInt(s);
            if (n >= 1 && n <= names.size()) return n - 1;
        } catch (NumberFormatException ignored) { }
        Integer id = index.get(s.toLowerCase());
        return id == null ? -1 : id;
    }

    // ---------- BFS: shortest route ----------

    /**
     * Returns the shortest path (fewest hops) as a list of terminal names.
     * Returns null if a terminal is unknown, or an empty list if there is no route.
     */
    public List<String> shortestPath(String from, String to) {
        int s = resolve(from);
        int t = resolve(to);
        if (s < 0 || t < 0) return null;

        boolean[] visited = new boolean[names.size()];
        int[] parent = new int[names.size()];
        Arrays.fill(parent, -1);

        Queue<Integer> queue = new ArrayDeque<>();
        visited[s] = true;
        queue.add(s);

        while (!queue.isEmpty()) {
            int u = queue.poll();
            if (u == t) break;
            for (int v : adj.get(u)) {
                if (!visited[v]) {
                    visited[v] = true;
                    parent[v] = u;
                    queue.add(v);
                }
            }
        }

        List<String> path = new ArrayList<>();
        if (!visited[t]) return path; // unreachable

        for (int at = t; at != -1; at = parent[at]) path.add(0, names.get(at));
        return path;
    }

    // ---------- DFS: explore connected terminals ----------

    /** Returns every terminal reachable from start, in DFS visiting order. Null if start unknown. */
    public List<String> explore(String start) {
        int s = resolve(start);
        if (s < 0) return null;
        boolean[] visited = new boolean[names.size()];
        List<String> order = new ArrayList<>();
        dfs(s, visited, order);
        return order;
    }

    private void dfs(int u, boolean[] visited, List<String> order) {
        visited[u] = true;
        order.add(names.get(u));
        for (int v : adj.get(u)) {
            if (!visited[v]) dfs(v, visited, order);
        }
    }

    // ---------- display ----------

    public String describe() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < names.size(); i++) {
            sb.append(String.format("%2d. %-16s -> ", i + 1, names.get(i)));
            List<String> neighbours = new ArrayList<>();
            for (int v : adj.get(i)) neighbours.add(names.get(v));
            sb.append(neighbours.isEmpty() ? "(no connections)" : String.join(", ", neighbours));
            sb.append('\n');
        }
        return sb.toString();
    }

    public int size() { return names.size(); }

    /** A ready-made demo airport. */
    public static TerminalGraph sampleAirport() {
        TerminalGraph g = new TerminalGraph();
        String[] terminals = {
                "Entrance", "Check-in Hall", "Baggage Claim", "Security", "Immigration",
                "Duty Free", "Food Court", "Lounge", "Gate A", "Gate B", "Gate C"
        };
        for (String t : terminals) g.addTerminal(t);

        g.addConnection("Entrance", "Check-in Hall");
        g.addConnection("Check-in Hall", "Baggage Claim");
        g.addConnection("Check-in Hall", "Security");
        g.addConnection("Security", "Immigration");
        g.addConnection("Security", "Duty Free");
        g.addConnection("Immigration", "Gate C");
        g.addConnection("Duty Free", "Food Court");
        g.addConnection("Duty Free", "Gate A");
        g.addConnection("Food Court", "Lounge");
        g.addConnection("Food Court", "Gate B");
        g.addConnection("Gate A", "Gate B");
        g.addConnection("Gate B", "Gate C");
        g.addConnection("Lounge", "Gate C");
        return g;
    }
}
