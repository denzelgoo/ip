import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Bro {
    private static final Path DATA_FILE_PATH = Path.of("data", "bro.txt");

    /**
     * Loads saved tasks from a file into an ArrayList of Task objects.
     * Handles missing files gracefully by returning an empty list, and safely skips malformed lines.
     *
     * @param filePath The relative path to the data file.
     * @return An ArrayList containing the loaded Task objects, or an empty list if the file does not exist.
     */
    private static ArrayList<Task> loadTasksFromFile(Path filePath) {
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
            System.out.println("\t" + "Oops, couldn't load tasks from file bro: " + e.getMessage());
        }

        return tasks;
    }

    /**
     * Saves the current list of tasks to the specified file.
     * Creates parent directories if they do not already exist.
     *
     * @param filePath The relative path to the data file.
     * @param tasks    The list of tasks to be saved.
     */
    private static void saveTasksToFile(Path filePath, ArrayList<Task> tasks) {
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
            System.out.println("\t" + "Oops, couldn't save your tasks to file bro: " + e.getMessage());
        }
    }

    public static void main(String[] args) throws BroException {
        String line = "____________________________________________________________";

        String banner = "    ____   ____  ____ \n"
                + "   / __ ) / __ \\/ __ \\\n"
                + "  / __  |/ /_/ / / / /\n"
                + " / /_/ // _, _/ /_/ / \n"
                + "/_____//_/ |_|\\____/  \n";
        System.out.println(line);
        System.out.println(banner);

        // Greet the user and wait for user input
        System.out.println("What's up bro, I'm Bro.");
        System.out.println("If you need anything, just ask bro.");
        System.out.println(line + "\n");

        // Load existing tasks from file if available
        ArrayList<Task> tasks = loadTasksFromFile(DATA_FILE_PATH);
        Scanner scanner = new Scanner(System.in);
        boolean isRunning = true;
        while (isRunning) {
            String input = scanner.nextLine();
            System.out.println("\t" + line);

            try {
                String[] inputParts = input.trim().split(" ", 2);
                Command command = Command.fromString(inputParts[0]);
                String arguments = inputParts.length > 1 ? inputParts[1].trim() : "";

                switch (command) {
                    case BYE -> {
                        isRunning = false;
                        System.out.println("\t" + "See you soon bro.");
                    }
                    case LIST -> {
                        // list the tasks stored
                        System.out.println("\t" + "Here are the tasks you have bro:");
                        for (int i = 0; i < tasks.size(); i++) {
                            System.out.println("\t" + (i + 1) + ". " + tasks.get(i));
                        }
                    }
                    case MARK, UNMARK -> {
                        // mark or unmark tasks as done
                        if (arguments.isEmpty()) {
                            throw new BroException("\t" + "Yo which task do you want to mark/unmark bro?");
                        }
                        int listIndex = Integer.parseInt(arguments) - 1;
                        Task task = tasks.get(listIndex);
                        if (command == Command.MARK) {
                            task.markDone();
                            System.out.println("\t" + "Nice bro, I've marked this task as done for you:");
                            System.out.println("\t" + "  " + task);
                        } else {
                            // unmark command
                            task.unmarkDone();
                            System.out.println("\t" + "That's tough bro, I've marked this task as not done yet:");
                            System.out.println("\t" + "  " + task);
                        }
                        saveTasksToFile(DATA_FILE_PATH, tasks);
                    }
                    case DELETE -> {
                        if (arguments.isEmpty()) {
                            throw new BroException("\t" + "Which task do you want to delete bro?");
                        }
                        int listIndex = Integer.parseInt(arguments) - 1;
                        Task task = tasks.remove(listIndex);
                        saveTasksToFile(DATA_FILE_PATH, tasks);
                        System.out.println("\t" + "No problem bro, I've removed this task:");
                        System.out.println("\t" + "  " + task);
                        printTaskCount(tasks.size());
                    }
                    case TODO -> {
                        if (arguments.isEmpty()) {
                            throw new BroException("\t" + "Sorry bro, you can't have an empty todo.");
                        }
                        Todo newTodo = new Todo(arguments);
                        tasks.add(newTodo);
                        saveTasksToFile(DATA_FILE_PATH, tasks);
                        System.out.println("\t" + "I gotchu bro, added this task:\n\t  " + newTodo);
                        printTaskCount(tasks.size());
                    }
                    case DEADLINE -> {
                        if (arguments.isEmpty()) {
                            throw new BroException("\t" + "Sorry bro, you can't have an empty deadline.");
                        }
                        String[] details = arguments.split(" /by ", 2);
                        if (details.length == 1 || details[0].isBlank() || details[1].isBlank()) {
                            throw new BroException("\t"
                                    + "I think you forgot to add the deadline bro, write 'deadline [task] /by [deadline]'");
                        }
                        Deadline newDeadline = new Deadline(details[0].trim(), details[1].trim());
                        tasks.add(newDeadline);
                        saveTasksToFile(DATA_FILE_PATH, tasks);
                        System.out.println("\t" + "I gotchu bro, added this task:\n\t  " + newDeadline);
                        printTaskCount(tasks.size());
                    }
                    case EVENT -> {
                        if (arguments.isEmpty()) {
                            throw new BroException("\t" + "Sorry bro, you can't have an empty event.");
                        }
                        Pattern pattern = Pattern
                                .compile("(?<task>.+?)\\s+/from\\s+(?<start>.+?)\\s+/to\\s+(?<end>.+)");
                        Matcher matcher = pattern.matcher(arguments);

                        if (matcher.find()) {
                            Event newEvent = new Event(matcher.group("task").trim(), matcher.group("start").trim(),
                                    matcher.group("end").trim());
                            tasks.add(newEvent);
                            saveTasksToFile(DATA_FILE_PATH, tasks);
                            System.out.println("\t" + "I gotchu bro, added this task:\n\t  " + newEvent);
                            printTaskCount(tasks.size());
                        } else {
                            throw new BroException("\t"
                                    + "I think you messed up the event format bro, write 'event [task] /from [start] /to [end]'");
                        }
                    }
                    case UNKNOWN -> {
                        throw new BroException("\t" + "I don't get what you're trying to say bro, can you try again?");
                    }
                }
            } catch (BroException e) {
                System.out.println(e.getMessage());
            } catch (NumberFormatException e) {
                System.out.println("\t" + "Bro...please enter a valid task number.");
            } catch (IndexOutOfBoundsException e) {
                System.out.println("\t" + "Uhh...that item doesn't exist in your list bro.");
            } finally {
                System.out.println("\t" + line + "\n");
            }
        }

        scanner.close();
    }

    private static void printTaskCount(int size) {
        if (size > 1 || size == 0) {
            System.out.println("\t" + "Now you have " + size + " tasks in the list.");
        } else {
            System.out.println("\t" + "Now you have " + size + " task in the list.");
        }
    }
}
