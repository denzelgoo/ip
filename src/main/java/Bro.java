import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Main application class for the Bro chatbot.
 * Coordinates user interaction, task storage, and command execution.
 */
public class Bro {
    private static final Path DATA_FILE_PATH = Path.of("data", "bro.txt");

    private final UserInterface ui;
    private final ArrayList<Task> tasks;

    /**
     * Constructs a Bro chatbot instance with its UserInterface and loaded tasks.
     */
    public Bro() {
        this.ui = new UserInterface();
        this.tasks = loadTasksFromFile(DATA_FILE_PATH, this.ui);
    }

    /**
     * Loads saved tasks from a file into an ArrayList of Task objects.
     * Handles missing files gracefully by returning an empty list, and safely skips malformed lines.
     *
     * @param filePath The relative path to the data file.
     * @param ui       The UserInterface instance for reporting errors.
     * @return An ArrayList containing the loaded Task objects, or an empty list if the file does not exist.
     */
    private static ArrayList<Task> loadTasksFromFile(Path filePath, UserInterface ui) {
        ArrayList<Task> tasks = new ArrayList<>();
        File file = filePath.toFile();
        if (!file.exists()) {
            return tasks;
        }

        try (Scanner fileScanner = new Scanner(file)) {
            while (fileScanner.hasNextLine()) {
                String line = fileScanner.nextLine().trim();
                if (line.startsWith("\uFEFF")) {
                    line = line.substring(1).trim();
                }
                if (line.isEmpty()) {
                    continue;
                }

                String[] parts = line.split(" \\| ");
                if (parts.length < 3) {
                    continue;
                }

                String taskType = parts[0];
                boolean isDone = parts[1].equals("1");
                String description = parts[2];

                Task task = null;
                try {
                    task = switch (taskType) {
                        case "T" -> new Todo(description);
                        case "D" -> (parts.length >= 4) ? new Deadline(description, parts[3]) : null;
                        case "E" -> {
                            if (parts.length >= 5) {
                                yield new Event(description, parts[3], parts[4]);
                            } else if (parts.length == 4) {
                                String[] times = parts[3].split(" /to | to | - | -|-", 2);
                                if (times.length == 2) {
                                    yield new Event(description, times[0], times[1]);
                                }
                                yield null;
                            }
                            yield null;
                        }
                        default -> null;
                    };
                } catch (BroException e) {
                    // Skip tasks with corrupted/invalid date entries
                    continue;
                }

                if (task != null) {
                    if (isDone) {
                        task.markDone();
                    }
                    tasks.add(task);
                }
            }
        } catch (IOException e) {
            ui.showLoadingError(e.getMessage());
        }

        return tasks;
    }

    /**
     * Saves the current list of tasks to the specified file.
     * Creates parent directories if they do not already exist.
     *
     * @param filePath The relative path to the data file.
     * @param tasks    The list of tasks to be saved.
     * @param ui       The UserInterface instance for reporting errors.
     */
    private static void saveTasksToFile(Path filePath, ArrayList<Task> tasks, UserInterface ui) {
        try {
            File file = filePath.toFile();
            // Create parent directories if they don't exist
            if (file.getParentFile() != null) {
                file.getParentFile().mkdirs();
            }

            try (PrintWriter writer = new PrintWriter(new FileWriter(file))) {
                for (Task task : tasks) {
                    writer.println(task.toFileFormat());
                }
            }
        } catch (IOException e) {
            ui.showSavingError(e.getMessage());
        }
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
                        saveTasksToFile(DATA_FILE_PATH, tasks, ui);
                    }
                    case DELETE -> {
                        if (arguments.isEmpty()) {
                            throw new BroException("\tWhich task do you want to delete bro?");
                        }
                        int listIndex = Integer.parseInt(arguments) - 1;
                        Task task = tasks.remove(listIndex);
                        saveTasksToFile(DATA_FILE_PATH, tasks, ui);
                        ui.showTaskDeleted(task, tasks.size());
                    }
                    case TODO -> {
                        if (arguments.isEmpty()) {
                            throw new BroException("\tSorry bro, you can't have an empty todo.");
                        }
                        Todo newTodo = new Todo(arguments);
                        tasks.add(newTodo);
                        saveTasksToFile(DATA_FILE_PATH, tasks, ui);
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
                        saveTasksToFile(DATA_FILE_PATH, tasks, ui);
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
                            saveTasksToFile(DATA_FILE_PATH, tasks, ui);
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