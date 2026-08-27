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

    /**
     * Formats the task into a pipe-delimited string for text file storage.
     */
    public abstract String toFileFormat();
}
