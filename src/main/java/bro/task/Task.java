package bro.task;

import java.time.LocalDate;

/**
 * Abstract representation of a task in the Bro application.
 */
public abstract class Task {
    protected final String task;
    protected boolean isDone;

    /**
     * Constructs a new Task with the specified description, initially not done.
     *
     * @param task The description of the task.
     */
    public Task(String task) {
        this.task = task;
        this.isDone = false;
    }

    /**
     * Constructs a new Task with the specified description and completion status.
     *
     * @param task   The description of the task.
     * @param isDone The initial completion status.
     */
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

    /**
     * Checks whether this task contains the string keywords.
     *
     * @param keywords The keyword or keyword phrase to check against.
     * @return True if the task contains the string keywords, false otherwise.
     */
    public boolean containsKeywords(String keywords) {
        return this.task.contains(keywords);
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
