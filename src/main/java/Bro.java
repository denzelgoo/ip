import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Main application class for the Bro chatbot.
 * Coordinates user interaction, task storage, and command execution.
 */
public class Bro {
    private static final Path DATA_FILE_PATH = Path.of("data", "bro.txt");

    private final Storage storage;
    private final UserInterface ui;
    private final ArrayList<Task> tasks;

    /**
     * Constructs a Bro chatbot instance with its Storage, UserInterface, and loaded tasks.
     */
    public Bro() {
        this.ui = new UserInterface();
        this.storage = new Storage(DATA_FILE_PATH);
        this.tasks = this.storage.load(this.ui);
    }

    /**
     * Starts the main chatbot loop, reading and handling user commands.
     */
    public void run() {
        ui.showWelcome();

        boolean isRunning = true;
        while (isRunning) {
            String input = ui.readCommand();
            ui.showLine();

            try {
                String[] inputParts = input.trim().split(" ", 2);
                Command command = Command.fromString(inputParts[0]);
                String arguments = inputParts.length > 1 ? inputParts[1].trim() : "";

                switch (command) {
                    case BYE -> {
                        isRunning = false;
                        ui.showGoodbye();
                    }
                    case LIST -> {
                        ui.showTaskList(tasks);
                    }
                    case TASKS -> {
                        LocalDate targetDate = TaskDateTime.parseQueryDate(arguments);
                        ArrayList<Task> matchingTasks = new ArrayList<>();
                        for (Task task : tasks) {
                            if (task.isOnDate(targetDate)) {
                                matchingTasks.add(task);
                            }
                        }
                        ui.showTasksForDate(targetDate, matchingTasks);
                    }
                    case MARK, UNMARK -> {
                        if (arguments.isEmpty()) {
                            throw new BroException("\tYo which task do you want to mark/unmark bro?");
                        }
                        int listIndex = Integer.parseInt(arguments) - 1;
                        Task task = tasks.get(listIndex);
                        if (command == Command.MARK) {
                            task.markDone();
                            ui.showTaskMarked(task);
                        } else {
                            task.unmarkDone();
                            ui.showTaskUnmarked(task);
                        }
                        storage.save(tasks, ui);
                    }
                    case DELETE -> {
                        if (arguments.isEmpty()) {
                            throw new BroException("\tWhich task do you want to delete bro?");
                        }
                        int listIndex = Integer.parseInt(arguments) - 1;
                        Task task = tasks.remove(listIndex);
                        storage.save(tasks, ui);
                        ui.showTaskDeleted(task, tasks.size());
                    }
                    case TODO -> {
                        if (arguments.isEmpty()) {
                            throw new BroException("\tSorry bro, you can't have an empty todo.");
                        }
                        Todo newTodo = new Todo(arguments);
                        tasks.add(newTodo);
                        storage.save(tasks, ui);
                        ui.showTaskAdded(newTodo, tasks.size());
                    }
                    case DEADLINE -> {
                        if (arguments.isEmpty()) {
                            throw new BroException("\tSorry bro, you can't have an empty deadline.");
                        }
                        String[] details = arguments.split(" /by ", 2);
                        if (details.length == 1 || details[0].isBlank() || details[1].isBlank()) {
                            throw new BroException("\t"
                                    + "I think you forgot to add the deadline bro, write 'deadline [task] /by [deadline]'");
                        }
                        Deadline newDeadline = new Deadline(details[0].trim(), details[1].trim());
                        tasks.add(newDeadline);
                        storage.save(tasks, ui);
                        ui.showTaskAdded(newDeadline, tasks.size());
                    }
                    case EVENT -> {
                        if (arguments.isEmpty()) {
                            throw new BroException("\tSorry bro, you can't have an empty event.");
                        }
                        Pattern pattern = Pattern
                                .compile("(?<task>.+?)\\s+/from\\s+(?<start>.+?)\\s+/to\\s+(?<end>.+)");
                        Matcher matcher = pattern.matcher(arguments);

                        if (matcher.find()) {
                            Event newEvent = new Event(matcher.group("task").trim(), matcher.group("start").trim(),
                                    matcher.group("end").trim());
                            tasks.add(newEvent);
                            storage.save(tasks, ui);
                            ui.showTaskAdded(newEvent, tasks.size());
                        } else {
                            throw new BroException("\t"
                                    + "I think you messed up the event format bro, write 'event [task] /from [start] /to [end]'");
                        }
                    }
                    case UNKNOWN -> {
                        throw new BroException("\tI don't get what you're trying to say bro, can you try again?");
                    }
                }
            } catch (BroException e) {
                ui.showError(e.getMessage());
            } catch (NumberFormatException e) {
                ui.showInvalidTaskNumberError();
            } catch (IndexOutOfBoundsException e) {
                ui.showNoSuchTaskError();
            } finally {
                ui.showDividerWithSpacing();
            }
        }

        ui.close();
    }

    public static void main(String[] args) {
        new Bro().run();
    }
}