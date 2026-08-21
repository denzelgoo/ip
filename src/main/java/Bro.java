import java.util.ArrayList;
import java.util.Scanner;

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

            String[] command = input.trim().toLowerCase().split("\\s+");
            if (command.length == 1 && command[0].equals("list")) {
                // list the tasks stored
                System.out.println("\t" + "Here are the tasks you have bro:");
                for (int i = 0; i < tasks.size(); i++) {
                    System.out.println("\t" + (i + 1) + ". " + tasks.get(i));
                }
            } else if (command.length == 2 && (command[0].equals("mark") || command[0].equals("unmark"))) {
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
            } else {
                // general case: add a task to the list
                Task newTask = new Task(input);
                tasks.add(newTask);
                System.out.println("\t" + "added: " + input);
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
