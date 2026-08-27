/**
 * Represents an event task spanning a specific start and end time.
 * Start and end timestamps are parsed and stored as TaskDateTime objects.
 */
public class Event extends Task {
    private final TaskDateTime start;
    private final TaskDateTime end;

    /**
     * Constructs an Event task with a description, start time, and end time.
     *
     * @param task     The description of the event.
     * @param startStr The date or date-time string for the start of the event.
     * @param endStr   The date or date-time string for the end of the event.
     * @throws BroException If either date/time string cannot be parsed.
     */
    public Event(String task, String startStr, String endStr) throws BroException {
        super(task);
        this.start = TaskDateTime.parse(startStr);
        this.end = TaskDateTime.parse(endStr);
    }

    /**
     * Returns a string representation of the Event task for display.
     *
     * @return Formatted string including task type, status icon, description, and formatted start/end dates.
     */
    @Override
    public String toString() {
        return String.format("[E]%s (from: %s to: %s)", super.toString(),
                this.start.formatDisplay(), this.end.formatDisplay());
    }

    /**
     * Returns a string representation of the Event task formatted for file storage.
     *
     * @return Formatted storage string with pipe delimiters.
     */
    @Override
    public String toFileFormat() {
        return "E | " + (this.isDone ? "1" : "0") + " | " + this.task + " | "
                + this.start.formatFile() + " | " + this.end.formatFile();
    }
}
