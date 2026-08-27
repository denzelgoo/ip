import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Handles reading tasks from and writing tasks to the local storage file.
 */
public class Storage {
    private final Path filePath;

    /**
     * Constructs a Storage instance targeting the specified Path.
     *
     * @param filePath The relative path to the data storage file.
     */
    public Storage(Path filePath) {
        this.filePath = filePath;
    }

    /**
     * Constructs a Storage instance targeting the specified file path string.
     *
     * @param filePathStr The relative string path to the data storage file.
     */
    public Storage(String filePathStr) {
        this.filePath = Path.of(filePathStr);
    }

    /**
     * Loads saved tasks from the data file into a TaskList.
     * Returns an empty TaskList if the file does not exist, and safely skips corrupted lines.
     *
     * @param ui The UserInterface instance for reporting loading errors, or null.
     * @return A TaskList containing the loaded Task objects.
     */
    public TaskList load(UserInterface ui) {
        ArrayList<Task> tasks = new ArrayList<>();
        File file = filePath.toFile();
        if (!file.exists()) {
            return new TaskList(tasks);
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
            if (ui != null) {
                ui.showLoadingError(e.getMessage());
            }
        }

        return new TaskList(tasks);
    }

    /**
     * Loads saved tasks from the data file without an explicit UserInterface reference.
     *
     * @return A TaskList containing the loaded Task objects.
     */
    public TaskList load() {
        return load(null);
    }

    /**
     * Saves the TaskList to the storage file.
     * Creates parent directories if they do not already exist.
     *
     * @param taskList The TaskList to save.
     * @param ui       The UserInterface instance for reporting save errors, or null.
     */
    public void save(TaskList taskList, UserInterface ui) {
        save(taskList.getAllTasks(), ui);
    }

    /**
     * Saves an ArrayList of tasks to the storage file.
     * Creates parent directories if they do not already exist.
     *
     * @param tasks The list of tasks to save.
     * @param ui    The UserInterface instance for reporting save errors, or null.
     */
    public void save(ArrayList<Task> tasks, UserInterface ui) {
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
            if (ui != null) {
                ui.showSavingError(e.getMessage());
            }
        }
    }

    /**
     * Saves the TaskList to the storage file without an explicit UserInterface reference.
     *
     * @param taskList The TaskList to save.
     */
    public void save(TaskList taskList) {
        save(taskList, null);
    }

    /**
     * Returns the file path associated with this storage.
     *
     * @return The Path of the data file.
     */
    public Path getFilePath() {
        return filePath;
    }
}