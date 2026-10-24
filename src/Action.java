/**
 * One recorded operation. Pushed on the history stack so it can be undone.
 */
public class Action {

    public enum Type { ADD, DELETE, UPDATE, ENQUEUE, CHECK_IN }

    private final Type type;
    private final Passenger before; // state before the operation (copy for UPDATE)
    private final String description;

    public Action(Type type, Passenger before, String description) {
        this.type = type;
        this.before = before;
        this.description = description;
    }

    public Type getType() { return type; }
    public Passenger getBefore() { return before; }
    public String getDescription() { return description; }
}
