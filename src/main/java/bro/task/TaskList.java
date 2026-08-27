package bro.task;

import java.time.LocalDate;
import java.util.ArrayList;

/**
 * Represents and manages the list of tasks in the Bro application.
 * Provides operations to add, delete, retrieve, and filter tasks.
 */
public class TaskList {
    private final ArrayList<Task> tasks;

    /**
     * Constructs an empty TaskList.
     */
    public TaskList() {
        this.tasks = new ArrayList<>();
    }

    /**
     * Constructs a TaskList initialized with an existing list of tasks.
     *
     * @param tasks An ArrayList of Task objects.
     */
    public TaskList(ArrayList<Task> tasks) {
        this.tasks = tasks != null ? tasks : new ArrayList<>();
    }

    /**
     * Adds a task to the list.
     *
     * @param task The task to add.
     */
    public void add(Task task) {
        this.tasks.add(task);
    }

    /**
     * Removes and returns the task at the specified 0-based index.
     *
     * @param index The 0-based index of the task to delete.
     * @return The removed Task.
     * @throws IndexOutOfBoundsException If the index is out of range.
     */
    public Task delete(int index) {
        return this.tasks.remove(index);
    }

    /**
     * Retrieves the task at the specified 0-based index.
     *
     * @param index The 0-based index of the task.
     * @return The Task at the specified index.
     * @throws IndexOutOfBoundsException If the index is out of range.
     */
    public Task get(int index) {
        return this.tasks.get(index);
    }

    /**
     * Returns the number of tasks in the list.
     *
     * @return The total number of tasks.
     */
    public int size() {
        return this.tasks.size();
    }

    /**
     * Returns whether the task list is empty.
     *
     * @return True if the list contains no tasks, false otherwise.
     */
    public boolean isEmpty() {
        return this.tasks.isEmpty();
    }

    /**
     * Finds and returns all tasks occurring on or involving the specified date.
     *
     * @param date The target date to search for.
     * @return An ArrayList of matching Task objects.
     */
    public ArrayList<Task> findTasksOnDate(LocalDate date) {
        ArrayList<Task> matchingTasks = new ArrayList<>();
        for (Task task : this.tasks) {
            if (task.isOnDate(date)) {
                matchingTasks.add(task);
            }
        }
        return matchingTasks;
    }

    /**
     * Returns the underlying list of tasks.
     *
     * @return An ArrayList containing all tasks.
     */
    public ArrayList<Task> getAllTasks() {
        return this.tasks;
    }
}