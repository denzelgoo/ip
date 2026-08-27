/**
 * Represents a task with a deadline.
 * The deadline date and time is parsed and stored as a TaskDateTime object.
 */
public class Deadline extends Task {
    private final TaskDateTime deadline;

    /**
     * Constructs a Deadline task with a description and a date/time string.
     *
     * @param task        The description of the deadline task.
     * @param deadlineStr The date or date-time string for the deadline.
     * @throws BroException If the date/time string cannot be parsed.
     */
    public Deadline(String task, String deadlineStr) throws BroException {
        super(task);
        this.deadline = TaskDateTime.parse(deadlineStr);
    }

    /**
     * Returns a string representation of the Deadline task for display.
     *
     * @return Formatted string including task type, status icon, description, and formatted deadline date/time.
     */
    @Override
    public String toString() {
        return String.format("[D]%s (by: %s)", super.toString(), this.deadline.formatDisplay());
    }

    /**
     * Returns a string representation of the Deadline task formatted for file storage.
     *
     * @return Formatted storage string with pipe delimiters.
     */
    @Override
    public String toFileFormat() {
        return "D | " + (this.isDone ? "1" : "0") + " | " + this.task + " | " + this.deadline.formatFile();
    }
}
