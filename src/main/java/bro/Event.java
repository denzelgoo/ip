package bro;

import java.time.LocalDate;

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
     * Checks if this event occurs on or spans across the specified date.
     *
     * @param date The LocalDate to check against.
     * @return True if the event spans across or includes the specified date, false
     *         otherwise.
     */
    @Override
    public boolean isOnDate(LocalDate date) {
        LocalDate startDate = this.start.toLocalDate();
        LocalDate endDate = this.end.toLocalDate();

        if (startDate != null && endDate != null) {
            return !date.isBefore(startDate) && !date.isAfter(endDate);
        } else if (startDate != null) {
            return startDate.isEqual(date);
        } else if (endDate != null) {
            return endDate.isEqual(date);
        }
        return false;
    }

    /**
     * Gets the TaskDateTime start object.
     *
     * @return The TaskDateTime instance for start.
     */
    public TaskDateTime getStart() {
        return this.start;
    }

    /**
     * Gets the TaskDateTime end object.
     *
     * @return The TaskDateTime instance for end.
     */
    public TaskDateTime getEnd() {
        return this.end;
    }

    /**
     * Returns a string representation of the Event task for display.
     *
     * @return Formatted string including task type, status icon, description, and
     *         formatted start/end dates.
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
