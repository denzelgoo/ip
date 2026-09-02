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
        String displayList = "Here are the tasks you have bro:";

        System.out.println("\tHere are the tasks you have bro:");
        for (int i = 0; i < tasks.size(); i++) {
            displayList += "\n" + (i + 1) + ". " + tasks.get(i);
            System.out.println("\t" + (i + 1) + ". " + tasks.get(i));
        }

        return displayList;
    }

    /**
     * Displays all tasks currently stored in an ArrayList.
     *
     * @param tasks The list of tasks to display.
     * @return The formatted string representation of the task list.
     */
    public String showTaskList(ArrayList<Task> tasks) {
        String displayList = "Here are the tasks you have bro:";

        System.out.println("\tHere are the tasks you have bro:");
        for (int i = 0; i < tasks.size(); i++) {
            displayList += "\n" + (i + 1) + ". " + tasks.get(i);
            System.out.println("\t" + (i + 1) + ". " + tasks.get(i));
        }

        return displayList;
    }

    /**
     * Displays tasks occurring on or involving a specific date.
     *
     * @param targetDate    The date queried by the user.
     * @param matchingTasks The tasks matching or involving the queried date.
     * @return The formatted string of tasks occurring on the queried date.
     */
    public String showTasksForDate(LocalDate targetDate, ArrayList<Task> matchingTasks) {
        String displayList;

        String formattedDate = targetDate.format(DateTimeFormatter.ofPattern("MMM dd yyyy", Locale.ENGLISH));
        if (matchingTasks.isEmpty()) {
            System.out.println("\tYou don't have any tasks for " + formattedDate + " bro!");
            return "You don't have any tasks for " + formattedDate + " bro!";
        } else {
            displayList = "Here are the tasks happening on " + formattedDate + " bro:";
            System.out.println("\tHere are the tasks happening on " + formattedDate + " bro:");
            for (int i = 0; i < matchingTasks.size(); i++) {
                displayList += "\n" + (i + 1) + ". " + matchingTasks.get(i);
                System.out.println("\t" + (i + 1) + ". " + matchingTasks.get(i));
            }
        }

        return displayList;
    }

    /**
     * Displays tasks containing the string keywords.
     *
     * @param keywords      The keyword or keyword phrase queried by the user.
     * @param matchingTasks The tasks matching or involving the queried
     *                      keyword/keyword phrase.
     * @return The formatted string of tasks containing the keywords.
     */
    public String showTasksContainingKeywords(String keywords, ArrayList<Task> matchingTasks) {
        String displayList;

        if (matchingTasks.isEmpty()) {
            String message = String.format(
                    "Sorry bro, I couldn't find any tasks containing '%s', "
                            + "can you check that you didn't make a typo?",
                    keywords);
            System.out.println("\t" + message);
            return message;
        } else {
            String message = String.format(
                    "No problem bro, here are the tasks containing '%s':", keywords);
            displayList = message;
            System.out.println("\t" + message);
            for (int i = 0; i < matchingTasks.size(); i++) {
                displayList += "\n" + (i + 1) + ". " + matchingTasks.get(i);
                System.out.println("\t" + (i + 1) + ". " + matchingTasks.get(i));
            }
        }

        return displayList;
    }

    /**
     * Displays confirmation that a task was successfully added.
     *
     * @param task      The task that was added.
     * @param totalSize The new total count of tasks.
     * @return The confirmation message of the added task.
     */
    public String showTaskAdded(Task task, int totalSize) {
        String msg = "I gotchu bro, added this task:\n\t  " + task;

        System.out.println("\t" + msg);
        msg += "\n" + showTaskCount(totalSize);

        return msg;
    }

    /**
     * Displays confirmation that a task was successfully removed.
     *
     * @param task      The removed task.
     * @param totalSize The remaining total count of tasks.
     * @return The confirmation message of the deleted task.
     */
    public String showTaskDeleted(Task task, int totalSize) {
        String msg = "No problem bro, I've removed this task:\n" + " " + task;

        System.out.println("\tNo problem bro, I've removed this task:");
        System.out.println("\t  " + task);
        msg += "\n" + showTaskCount(totalSize);

        return msg;
    }

    /**
     * Displays confirmation that a task was marked as done.
     *
     * @param task The marked task.
     * @return The confirmation message of the marked task.
     */
    public String showTaskMarked(Task task) {
        String msg = "Nice bro, I've marked this task as done for you:\n" + " " + task;

        System.out.println("\tNice bro, I've marked this task as done for you:");
        System.out.println("\t  " + task);

        return msg;
    }

    /**
     * Displays confirmation that a task was unmarked as not done.
     *
     * @param task The unmarked task.
     * @return The confirmation message of the unmarked task.
     */
    public String showTaskUnmarked(Task task) {
        String msg = "That's tough bro, I've marked this task as not done yet:\n" + " " + task;

        System.out.println("\tThat's tough bro, I've marked this task as not done yet:");
        System.out.println("\t  " + task);

        return msg;
    }

    /**
     * Displays the current count of tasks in the list.
     *
     * @param size The number of tasks in the list.
     * @return The formatted task count message string.
     */
    public String showTaskCount(int size) {
        if (size > 1 || size == 0) {
            System.out.println("\tNow you have " + size + " tasks in the list.");
            return "Now you have " + size + " tasks in the list.";
        } else {
            System.out.println("\tNow you have " + size + " task in the list.");
            return "Now you have " + size + " task in the list.";
        }
    }

    /**
     * Displays an error message to the user.
     *
     * @param message The error message to print.
     * @return The stripped error message string.
     */
    public String showError(String message) {
        System.out.println(message);
        return message.strip();
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
