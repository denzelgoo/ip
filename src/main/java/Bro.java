import java.util.ArrayList;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Bro {
    public static void main(String[] args) {
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

        // Save tasks to a list and display list when asked
        ArrayList<Task> tasks = new ArrayList<>();
        Scanner scanner = new Scanner(System.in);
        String input = scanner.nextLine();
        while (!input.trim().equalsIgnoreCase("bye")) {
            System.out.println("\t" + line);

            String[] command = input.trim().split(" ", 2);
            command[0] = command[0].toLowerCase();
            if (command.length == 1 && command[0].equals("list")) {
                // list the tasks stored
                System.out.println("\t" + "Here are the tasks you have bro:");
                for (int i = 0; i < tasks.size(); i++) {
                    System.out.println("\t" + (i + 1) + ". " + tasks.get(i));
                }
            } else if (command[0].equals("mark") || command[0].equals("unmark")) {
                // mark or unmark tasks as done
                int listIndex = Integer.parseInt(command[1]) - 1;
                Task task = tasks.get(listIndex);
                if (command[0].equals("mark")) {
                    task.markDone();
                    System.out.println("\t" + "Nice bro, I've marked this task as done for you:");
                    System.out.println("\t" + "  " + task);
                } else {
                    // unmark command
                    task.unmarkDone();
                    System.out.println("\t" + "That's tough bro, I've marked this task as not done yet:");
                    System.out.println("\t" + "  " + task);
                }
            } else if (command.length > 1) {
                // general case: add a task to the list
                if (command[0].equals("todo")) {
                    Todo newTodo = new Todo(command[1]);
                    tasks.add(newTodo);
                    System.out.println("\t" + "I gotchu bro, added this task:\n\t  " + newTodo);

                    if (tasks.size() > 1) {
                        System.out.println("\t" + "Now you have " + tasks.size() + " tasks in the list.");
                    } else {
                        System.out.println("\t" + "Now you have " + tasks.size() + " task in the list.");
                    }
                } else if (command[0].equals("deadline")) {
                    String[] details = command[1].split(" /by ", 2);
                    Deadline newDeadline = new Deadline(details[0], details[1]);
                    tasks.add(newDeadline);
                    System.out.println("\t" + "I gotchu bro, added this task:\n\t  " + newDeadline);

                    if (tasks.size() > 1) {
                        System.out.println("\t" + "Now you have " + tasks.size() + " tasks in the list.");
                    } else {
                        System.out.println("\t" + "Now you have " + tasks.size() + " task in the list.");
                    }
                } else if (command[0].equals("event")) {
                    Pattern pattern = Pattern.compile("(?<task>.+?)\\s+/from\\s+(?<start>.+?)\\s+/to\\s+(?<end>.+)");
                    Matcher matcher = pattern.matcher(command[1]);
                    String task = "";
                    String start = "";
                    String end = "";

                    if (matcher.find()) {
                        task = matcher.group("task");   // "project meeting"
                        start = matcher.group("start"); // "Mon 2pm"
                        end = matcher.group("end");     // "4pm"

                        Event newEvent = new Event(task, start, end);
                        tasks.add(newEvent);
                        System.out.println("\t" + "I gotchu bro, added this task:\n\t  " + newEvent);

                        if (tasks.size() > 1) {
                            System.out.println("\t" + "Now you have " + tasks.size() + " tasks in the list.");
                        } else {
                            System.out.println("\t" + "Now you have " + tasks.size() + " task in the list.");
                        }
                    } else {
                        System.out.println("\t" + "I think you messed up the format of the event bro, can you try again?");
                    }
                } else {
                    // invalid command
                    System.out.println("\t" + "I don't get what you're trying to say bro, can you try again?");
                }
            } else {
                // the command is either just a single word that is not "list" or nothing, invalid command
                System.out.println("\t" + "I don't get what you're trying to say bro, can you try again?");
            }

            System.out.println("\t" + line + "\n");
            input = scanner.nextLine();
        }

        // Exit
        System.out.println("\t" + line);
        System.out.println("\t" + "See you soon bro.");
        System.out.println("\t" + line);
    }
}
