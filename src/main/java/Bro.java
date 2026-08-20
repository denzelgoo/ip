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

        // Echo user input
        Scanner scanner = new Scanner(System.in);
        String input = scanner.nextLine();
        while (!input.trim().equalsIgnoreCase("bye")) {
            System.out.println("\t" + line);
            System.out.println("\t" + input);
            System.out.println("\t" + line + "\n");
            input = scanner.nextLine();
        }

        // Exit
        System.out.println("\t" + line);
        System.out.println("\t" + "See you soon bro.");
        System.out.println("\t" + line);
    }
}
