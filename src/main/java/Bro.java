import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;

/**
 * Main application class for the Bro chatbot.
 * Coordinates user interaction, task storage, parsing, and command execution.
 */
public class Bro {
    private static final Path DATA_FILE_PATH = Path.of("data", "bro.txt");

    private final Storage storage;
    private final UserInterface ui;
    private final TaskList tasks;

    /**
     * Constructs a Bro chatbot instance with its Storage, UserInterface, and TaskList.
     */
    public Bro() {
        this.ui = new UserInterface();
        this.storage = new Storage(DATA_FILE_PATH);
        this.tasks = this.storage.load(this.ui);
    }

    /**
     * Starts the main chatbot loop, reading, parsing, and executing user commands.
     */
    public void run() {
        ui.showWelcome();

        boolean isRunning = true;
        while (isRunning) {
            String input = ui.readCommand();
            ui.showLine();

            try {
                Command command = Parser.parseCommand(input);
                String arguments = Parser.parseArguments(input);

                switch (command) {
                    case BYE -> {
                        isRunning = false;
                        ui.showGoodbye();
                    }
                    case LIST -> {
                        ui.showTaskList(tasks);
                    }
                    case TASKS -> {
                        LocalDate targetDate = Parser.parseQueryDate(arguments);
                        ArrayList<Task> matchingTasks = tasks.findTasksOnDate(targetDate);
                        ui.showTasksForDate(targetDate, matchingTasks);
                    }
                    case MARK, UNMARK -> {
                        int listIndex = Parser.parseTaskIndex(arguments,
                                "\tYo which task do you want to mark/unmark bro?");
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
                        int listIndex = Parser.parseTaskIndex(arguments,
                                "\tWhich task do you want to delete bro?");
                        Task task = tasks.delete(listIndex);
                        storage.save(tasks, ui);
                        ui.showTaskDeleted(task, tasks.size());
                    }
                    case TODO -> {
                        String description = Parser.parseTodoDescription(arguments);
                        Todo newTodo = new Todo(description);
                        tasks.add(newTodo);
                        storage.save(tasks, ui);
                        ui.showTaskAdded(newTodo, tasks.size());
                    }
                    case DEADLINE -> {
                        String[] details = Parser.parseDeadlineArguments(arguments);
                        Deadline newDeadline = new Deadline(details[0], details[1]);
                        tasks.add(newDeadline);
                        storage.save(tasks, ui);
                        ui.showTaskAdded(newDeadline, tasks.size());
                    }
                    case EVENT -> {
                        String[] details = Parser.parseEventArguments(arguments);
                        Event newEvent = new Event(details[0], details[1], details[2]);
                        tasks.add(newEvent);
                        storage.save(tasks, ui);
                        ui.showTaskAdded(newEvent, tasks.size());
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