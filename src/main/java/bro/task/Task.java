package bro.task;

import java.time.LocalDate;

/**
 * Abstract representation of a task in the Bro application.
 */
public abstract class Task {
    protected final String task;
    protected boolean isDone;

    public Task(String task) {
        this.task = task;
        this.isDone = false;
    }

    public Task(String task, boolean isDone) {
        this.task = task;
        this.isDone = isDone;
    }

    public void markDone() {
        this.isDone = true;
    }

    public void unmarkDone() {
        this.isDone = false;
    }

    public boolean isDone() {
        return this.isDone;
    }

    /**
     * Checks whether this task occurs on or involves the given date.
     * Default implementation returns false. Subclasses should override where
     * appropriate.
     *
     * @param date The LocalDate to check against.
     * @return True if the task involves the given date, false otherwise.
     */
    public boolean isOnDate(LocalDate date) {
        return false;
    }

    @Override
    public String toString() {
        if (this.isDone) {
            return String.format("[X] %s", this.task);
        } else {
            return String.format("[ ] %s", this.task);
        }
    }

    public String getStatusIcon() {
        return (isDone ? "1" : "0");
    }

    public String getTask() {
        return this.task;
    }

    /**
     * Formats the task into a pipe-delimited string for text file storage.
     */
    public abstract String toFileFormat();
}
