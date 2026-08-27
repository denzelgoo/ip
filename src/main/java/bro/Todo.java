package bro;

import java.time.LocalDate;

/**
 * Represents a todo task.
 * Uncompleted todos persist across all dates until marked done.
 */
public class Todo extends Task {
    public Todo(String task) {
        super(task);
    }

    /**
     * Checks if this todo should be listed for a specific date.
     * Uncompleted todos are always listed because they remain active until
     * finished.
     *
     * @param date The LocalDate to check against.
     * @return True if the todo is not completed, false if completed.
     */
    @Override
    public boolean isOnDate(LocalDate date) {
        return !this.isDone;
    }

    @Override
    public String toString() {
        return "[T]" + super.toString();
    }

    @Override
    public String toFileFormat() {
        return "T | " + (this.isDone ? "1" : "0") + " | " + this.task;
    }
}
