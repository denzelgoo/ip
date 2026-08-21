import java.util.ArrayList;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Bro {
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

        // Save tasks to a list and display list when asked

        ArrayList<Task> tasks = new ArrayList<>();
        Scanner scanner = new Scanner(System.in);
        while (true) {
            String input = scanner.nextLine();
            if (input.equalsIgnoreCase("bye")) {
                break;
            }

            System.out.println("\t" + line);

            try {
                String[] inputParts = input.trim().split(" ", 2);
                Command command = Command.fromString(inputParts[0]);
                String arguments = inputParts.length > 1 ? inputParts[1].trim() : "";

                switch (command) {
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
                    }
                    case DELETE -> {
                        if (arguments.isEmpty()) {
                            throw new BroException("\t" + "Which task do you want to delete bro?");
                        }
                        int listIndex = Integer.parseInt(arguments) - 1;
                        Task task = tasks.remove(listIndex);
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
                        System.out.println("\t" + "I gotchu bro, added this task:\n\t  " + newTodo);
                        printTaskCount(tasks.size());
                    }
                    case DEADLINE -> {
                        if (arguments.isEmpty()) {
                            throw new BroException("\t" + "Sorry bro, you can't have an empty deadline.");
                        }
                        String[] details = arguments.split(" /by ", 2);
                        if (details.length == 1 || details[0].isBlank() || details[1].isBlank()) {
                            throw new BroException("\t" + "I think you forgot to add the deadline bro, write 'deadline [task] /by [deadline]'");
                        }
                        Deadline newDeadline = new Deadline(details[0], details[1]);
                        tasks.add(newDeadline);
                        System.out.println("\t" + "I gotchu bro, added this task:\n\t  " + newDeadline);
                        printTaskCount(tasks.size());
                    }
                    case EVENT -> {
                        if (arguments.isEmpty()) {
                            throw new BroException("\t" + "Sorry bro, you can't have an empty event.");
                        }
                        Pattern pattern = Pattern.compile("(?<task>.+?)\\s+/from\\s+(?<start>.+?)\\s+/to\\s+(?<end>.+)");
                        Matcher matcher = pattern.matcher(arguments);

                        if (matcher.find()) {
                            Event newEvent = new Event(matcher.group("task"), matcher.group("start"), matcher.group("end"));
                            tasks.add(newEvent);
                            System.out.println("\t" + "I gotchu bro, added this task:\n\t  " + newEvent);
                            printTaskCount(tasks.size());
                        } else {
                            throw new BroException("\t" + "I think you messed up the event format bro, write 'event [task] /from [start] /to [end]'");
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

        // Exit
        System.out.println("\t" + line);
        System.out.println("\t" + "See you soon bro.");
        System.out.println("\t" + line);
    }

    private static void printTaskCount(int size) {
        if (size > 1 || size == 0) {
            System.out.println("\t" + "Now you have " + size + " tasks in the list.");
        } else {
            System.out.println("\t" + "Now you have " + size + " task in the list.");
        }
    }
}
