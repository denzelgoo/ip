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
     *
     * @return The welcome message string.
     */
    public String showWelcome() {
        String welcomeMsg = "What's up bro, I'm Bro.\n" + "If you need anything, just ask bro.";

        System.out.println(DIVIDER_LINE);
        System.out.println(BANNER);
        System.out.println(welcomeMsg);
        System.out.println(DIVIDER_LINE + "\n");

        return welcomeMsg;
    }

    /**
     * Displays the goodbye message when exiting.
     *
     * @return The goodbye message string.
     */
    public String showGoodbye() {
        String goodbyeMsg = "See you soon bro.";
        System.out.println("\t" + goodbyeMsg);
        return goodbyeMsg;
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
     * @return The formatted string representation of the task list.
     */
    public String showTaskList(TaskList tasks) {
        return showTaskList(tasks.getAllTasks());
    }

    /**
     * Displays all tasks currently stored in an ArrayList.
     *
     * @param tasks The list of tasks to display.
     * @return The formatted string representation of the task list.
     */
    public String showTaskList(ArrayList<Task> tasks) {
        return formatIndexedTaskList("Here are the tasks you have bro:", tasks);
    }

    /**
     * Formats an indexed list of tasks under a specified header, prints them to the console,
     * and returns the combined multi-line string.
     *
     * @param header The header message preceding the list of tasks.
     * @param tasks  The list of tasks to format.
     * @return The formatted multi-line task list string.
     */
    private String formatIndexedTaskList(String header, ArrayList<Task> tasks) {
        StringBuilder builder = new StringBuilder(header);
        System.out.println("\t" + header);

        for (int i = 0; i < tasks.size(); i++) {
            String taskLine = (i + 1) + ". " + tasks.get(i);
            builder.append("\n").append(taskLine);
            System.out.println("\t" + taskLine);
        }

        return builder.toString();
    }

    /**
     * Displays tasks occurring on or involving a specific date.
     *
     * @param targetDate    The date queried by the user.
     * @param matchingTasks The tasks matching or involving the queried date.
     * @return The formatted string of tasks occurring on the queried date.
     */
    public String showTasksForDate(LocalDate targetDate, ArrayList<Task> matchingTasks) {
        String formattedDate = targetDate.format(DateTimeFormatter.ofPattern("MMM dd yyyy", Locale.ENGLISH));
        if (matchingTasks.isEmpty()) {
            String message = "You don't have any tasks for " + formattedDate + " bro!";
            System.out.println("\t" + message);
            return message;
        }

        String header = "Here are the tasks happening on " + formattedDate + " bro:";
        return formatIndexedTaskList(header, matchingTasks);
    }

    /**
     * Displays tasks containing the string keywords.
     *
     * @param keywords      The keyword or keyword phrase queried by the user.
     * @param matchingTasks The tasks matching or involving the queried keyword/phrase.
     * @return The formatted string of tasks containing the keywords.
     */
    public String showTasksContainingKeywords(String keywords, ArrayList<Task> matchingTasks) {
        if (matchingTasks.isEmpty()) {
            String message = String.format(
                    "Sorry bro, I couldn't find any tasks containing '%s', "
                            + "can you check that you didn't make a typo?",
                    keywords);
            System.out.println("\t" + message);
            return message;
        }

        String header = String.format("No problem bro, here are the tasks containing '%s':", keywords);
        return formatIndexedTaskList(header, matchingTasks);
    }

    /**
     * Formats and prints feedback for an action performed on a single task.
     *
     * @param header The action message header.
     * @param task   The affected task.
     * @return The formatted two-line action feedback string.
     */
    private String formatTaskAction(String header, Task task) {
        System.out.println("\t" + header);
        System.out.println("\t  " + task);
        return header + "\n  " + task;
    }

    /**
     * Displays confirmation that a task was successfully added.
     *
     * @param task      The task that was added.
     * @param totalSize The new total count of tasks.
     * @return The confirmation message of the added task.
     */
    public String showTaskAdded(Task task, int totalSize) {
        String msg = formatTaskAction("I gotchu bro, added this task:", task);
        return msg + "\n" + showTaskCount(totalSize);
    }

    /**
     * Displays confirmation that a task was successfully removed.
     *
     * @param task      The removed task.
     * @param totalSize The remaining total count of tasks.
     * @return The confirmation message of the deleted task.
     */
    public String showTaskDeleted(Task task, int totalSize) {
        String msg = formatTaskAction("No problem bro, I've removed this task:", task);
        return msg + "\n" + showTaskCount(totalSize);
    }

    /**
     * Displays confirmation that a task was marked as done.
     *
     * @param task The marked task.
     * @return The confirmation message of the marked task.
     */
    public String showTaskMarked(Task task) {
        return formatTaskAction("Nice bro, I've marked this task as done for you:", task);
    }

    /**
     * Displays confirmation that a task was unmarked as not done.
     *
     * @param task The unmarked task.
     * @return The confirmation message of the unmarked task.
     */
    public String showTaskUnmarked(Task task) {
        return formatTaskAction("That's tough bro, I've marked this task as not done yet:", task);
    }

    /**
     * Displays the current count of tasks in the list.
     *
     * @param size The number of tasks in the list.
     * @return The formatted task count message string.
     */
    public String showTaskCount(int size) {
        String countMessage;
        if (size == 1) {
            countMessage = "Now you have 1 task in the list.";
        } else {
            countMessage = "Now you have " + size + " tasks in the list.";
        }
        System.out.println("\t" + countMessage);
        return countMessage;
    }

    /**
     * Displays an error message to the user.
     *
     * @param message The error message to print.
     * @return The stripped error message string.
     */
    public String showError(String message) {
        String trimmed = message.strip();
        System.out.println("\t" + trimmed);
        return trimmed;
    }

    /**
     * Displays an error message when a task number input is invalid.
     *
     * @return The invalid task number error message string.
     */
    public String showInvalidTaskNumberError() {
        System.out.println("\tBro...please enter a valid task number.");
        return "Bro...please enter a valid task number.";
    }

    /**
     * Displays an error message when the selected task index does not exist.
     *
     * @return The non-existent task error message string.
     */
    public String showNoSuchTaskError() {
        System.out.println("\tUhh...that item doesn't exist in your list bro.");
        return "Uhh...that item doesn't exist in your list bro.";
    }

    /**
     * Displays an error message when loading tasks from file fails.
     *
     * @param message The underlying error detail.
     * @return The loading error message string.
     */
    public String showLoadingError(String message) {
        System.out.println("\tOops, couldn't load tasks from file bro: " + message);
        return "Oops, couldn't load tasks from file bro: " + message;
    }

    /**
     * Displays an error message when saving tasks to file fails.
     *
     * @param message The underlying error detail.
     * @return The saving error message string.
     */
    public String showSavingError(String message) {
        System.out.println("\tOops, couldn't save your tasks to file bro: " + message);
        return "Oops, couldn't save your tasks to file bro: " + message;
    }

    /**
     * Closes the underlying input scanner resource.
     */
    public void close() {
        scanner.close();
    }
}
