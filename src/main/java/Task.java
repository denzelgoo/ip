public class Task {
    private final String task;
    private boolean done;

    public Task(String task) {
        this.task = task;
        this.done = false;
    }

    public Task(String task, boolean done) {
        this.task = task;
        this.done = done;
    }

    public void markDone() {
        this.done = true;
    }

    public void unmarkDone() {
        this.done = false;
    }

    @Override
    public String toString() {
        if (this.done) {
            return String.format("[X] %s", this.task);
        } else {
            return String.format("[ ] %s", this.task);
        }
    }
}
