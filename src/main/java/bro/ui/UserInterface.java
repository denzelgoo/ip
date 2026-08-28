package bro.ui;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Scanner;

import bro.task.Task;
import bro.task.TaskList;

/**
 * Handles all user interactions and console input/output for the Bro chatbot.
 */
public class UserInterface {
    private static final String DIVIDER_LINE = "____________________________________________________________";
    private static final String BANNER = "    ____   ____  ____ \n"
            + "   / __ ) / __ \\/ __ \\\n"
            + "  / __  |/ /_/ / / / /\n"
            + " / /_/ // _, _/ /_/ / \n"
            + "/_____//_/ |_|\\____/  \n";

    private final Scanner scanner;

    /**
     * Constructs a UserInterface instance initializing the input scanner.
     */
    public UserInterface() {
        this.scanner = new Scanner(System.in);
    }

    /**
     * Reads the next command line entered by the user.
     *
     * @return The raw user input string.
     */
    public String readCommand() {
        return scanner.nextLine();
    }

    /**
     * Displays the initial welcome greeting and ASCII banner.
     */
    public void showWelcome() {
        System.out.println(DIVIDER_LINE);
        System.out.println(BANNER);
        System.out.println("What's up bro, I'm Bro.");
        System.out.println("If you need anything, just ask bro.");
        System.out.println(DIVIDER_LINE + "\n");
    }

    /**
     * Displays the goodbye message when exiting.
     */
    public void showGoodbye() {
        System.out.println("\tSee you soon bro.");
    }

    /**
     * Displays the tabbed divider line above command responses.
     */
    public void showLine() {
        System.out.println("\t" + DIVIDER_LINE);
    }

    /**
     * Displays the closing tabbed divider line below command responses.
     */
    public void showDividerWithSpacing() {
        System.out.println("\t" + DIVIDER_LINE + "\n");
    }

    /**
     * Displays all tasks currently stored in the TaskList.
     *
     * @param tasks The TaskList to display.
     */
    public void showTaskList(TaskList tasks) {
        System.out.println("\tHere are the tasks you have bro:");
        for (int i = 0; i < tasks.size(); i++) {
            System.out.println("\t" + (i + 1) + ". " + tasks.get(i));
        }
    }

    /**
     * Displays all tasks currently stored in an ArrayList.
     *
     * @param tasks The list of tasks to display.
     */
    public void showTaskList(ArrayList<Task> tasks) {
        System.out.println("\tHere are the tasks you have bro:");
        for (int i = 0; i < tasks.size(); i++) {
            System.out.println("\t" + (i + 1) + ". " + tasks.get(i));
        }
    }

    /**
     * Displays tasks occurring on or involving a specific date.
     *
     * @param targetDate    The date queried by the user.
     * @param matchingTasks The tasks matching or involving the queried date.
     */
    public void showTasksForDate(LocalDate targetDate, ArrayList<Task> matchingTasks) {
        String formattedDate = targetDate.format(DateTimeFormatter.ofPattern("MMM dd yyyy", Locale.ENGLISH));
        if (matchingTasks.isEmpty()) {
            System.out.println("\tYou don't have any tasks for " + formattedDate + " bro!");
        } else {
            System.out.println("\tHere are the tasks happening on " + formattedDate + " bro:");
            for (int i = 0; i < matchingTasks.size(); i++) {
                System.out.println("\t" + (i + 1) + ". " + matchingTasks.get(i));
            }
        }
    }

    /**
     * Displays tasks containing the string keywords.
     * 
     * @param keywords The keyword or keyword phrase queried by the user.
     * @param matchingTasks The tasks matching or involving the queried keyword/keyword phrase.
     */
    public void showTasksContainingKeywords(String keywords, ArrayList<Task> matchingTasks) {
        if (matchingTasks.isEmpty()) {
            String message = String.format(
                    "\tSorry bro, I couldn't find any tasks containing '%s', can you check that you didn't make a typo?",
                    keywords);
            System.out.println(message);
        } else {
            String message = String.format(
                    "\tNo problem bro, here are the tasks containing '%s':", keywords);
            System.out.println(message);
            for (int i = 0; i < matchingTasks.size(); i++) {
                System.out.println("\t" + (i + 1) + ". " + matchingTasks.get(i));
            }
        }
    }

    /**
     * Displays confirmation that a task was successfully added.
     *
     * @param task      The task that was added.
     * @param totalSize The new total count of tasks.
     */
    public void showTaskAdded(Task task, int totalSize) {
        System.out.println("\tI gotchu bro, added this task:\n\t  " + task);
        showTaskCount(totalSize);
    }

    /**
     * Displays confirmation that a task was successfully removed.
     *
     * @param task      The removed task.
     * @param totalSize The remaining total count of tasks.
     */
    public void showTaskDeleted(Task task, int totalSize) {
        System.out.println("\tNo problem bro, I've removed this task:");
        System.out.println("\t  " + task);
        showTaskCount(totalSize);
    }

    /**
     * Displays confirmation that a task was marked as done.
     *
     * @param task The marked task.
     */
    public void showTaskMarked(Task task) {
        System.out.println("\tNice bro, I've marked this task as done for you:");
        System.out.println("\t  " + task);
    }

    /**
     * Displays confirmation that a task was unmarked as not done.
     *
     * @param task The unmarked task.
     */
    public void showTaskUnmarked(Task task) {
        System.out.println("\tThat's tough bro, I've marked this task as not done yet:");
        System.out.println("\t  " + task);
    }

    /**
     * Displays the current count of tasks in the list.
     *
     * @param size The number of tasks in the list.
     */
    public void showTaskCount(int size) {
        if (size > 1 || size == 0) {
            System.out.println("\tNow you have " + size + " tasks in the list.");
        } else {
            System.out.println("\tNow you have " + size + " task in the list.");
        }
    }

    /**
     * Displays an error message to the user.
     *
     * @param message The error message to print.
     */
    public void showError(String message) {
        System.out.println(message);
    }

    /**
     * Displays an error message when a task number input is invalid.
     */
    public void showInvalidTaskNumberError() {
        System.out.println("\tBro...please enter a valid task number.");
    }

    /**
     * Displays an error message when the selected task index does not exist.
     */
    public void showNoSuchTaskError() {
        System.out.println("\tUhh...that item doesn't exist in your list bro.");
    }

    /**
     * Displays an error message when loading tasks from file fails.
     *
     * @param message The underlying error detail.
     */
    public void showLoadingError(String message) {
        System.out.println("\tOops, couldn't load tasks from file bro: " + message);
    }

    /**
     * Displays an error message when saving tasks to file fails.
     *
     * @param message The underlying error detail.
     */
    public void showSavingError(String message) {
        System.out.println("\tOops, couldn't save your tasks to file bro: " + message);
    }

    /**
     * Closes the underlying input scanner resource.
     */
    public void close() {
        scanner.close();
    }
}