package bro.parser;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import bro.command.Command;
import bro.exception.BroException;
import bro.task.TaskDateTime;

/**
 * Handles parsing and interpreting raw user commands and arguments.
 */
public class Parser {
    private static final Pattern EVENT_PATTERN = Pattern
            .compile("(?<task>.+?)\\s+/from\\s+(?<start>.+?)\\s+/to\\s+(?<end>.+)");

    /**
     * Parses the raw input string to determine the Command type.
     *
     * @param fullCommand The raw input line entered by the user.
     * @return The corresponding Command enum constant.
     */
    public static Command parseCommand(String fullCommand) {
        if (fullCommand == null || fullCommand.trim().isEmpty()) {
            return Command.UNKNOWN;
        }
        String[] parts = fullCommand.trim().split(" ", 2);
        return Command.fromString(parts[0]);
    }

    /**
     * Parses the raw input string to extract the arguments string.
     *
     * @param fullCommand The raw input line entered by the user.
     * @return The argument string, or an empty string if no arguments were
     *         provided.
     */
    public static String parseArguments(String fullCommand) {
        if (fullCommand == null || fullCommand.trim().isEmpty()) {
            return "";
        }
        String[] parts = fullCommand.trim().split(" ", 2);
        return parts.length > 1 ? parts[1].trim() : "";
    }

    /**
     * Parses a 1-based task index from the arguments into a 0-based array index.
     *
     * @param arguments    The argument string containing the task index.
     * @param emptyMessage The error message thrown if arguments is empty.
     * @return The 0-based integer index.
     * @throws BroException          If arguments is empty.
     * @throws NumberFormatException If arguments cannot be parsed as an integer.
     */
    public static int parseTaskIndex(String arguments, String emptyMessage) throws BroException {
        if (arguments == null || arguments.trim().isEmpty()) {
            throw new BroException(emptyMessage);
        }
        return Integer.parseInt(arguments.trim()) - 1;
    }

    /**
     * Parses arguments for a todo task.
     *
     * @param arguments The description argument.
     * @return The trimmed description string.
     * @throws BroException If arguments is empty.
     */
    public static String parseTodoDescription(String arguments) throws BroException {
        if (arguments == null || arguments.trim().isEmpty()) {
            throw new BroException("\tSorry bro, you can't have an empty todo.");
        }
        return arguments.trim();
    }

    /**
     * Parses arguments for a deadline task into description and deadline date/time.
     *
     * @param arguments The raw arguments string.
     * @return A 2-element array containing [description, deadlineStr].
     * @throws BroException If arguments is empty or the format is invalid.
     */
    public static String[] parseDeadlineArguments(String arguments) throws BroException {
        if (arguments == null || arguments.trim().isEmpty()) {
            throw new BroException("\tSorry bro, you can't have an empty deadline.");
        }
        String[] details = arguments.split(" /by ", 2);
        if (details.length == 1 || details[0].isBlank() || details[1].isBlank()) {
            throw new BroException("\t"
                    + "I think you forgot to add the deadline bro, write 'deadline [task] /by [deadline]'");
        }
        return new String[] { details[0].trim(), details[1].trim() };
    }

    /**
     * Parses arguments for an event task into description, start date/time, and end
     * date/time.
     *
     * @param arguments The raw arguments string.
     * @return A 3-element array containing [description, startStr, endStr].
     * @throws BroException If arguments is empty or the format does not match the
     *                      event pattern.
     */
    public static String[] parseEventArguments(String arguments) throws BroException {
        if (arguments == null || arguments.trim().isEmpty()) {
            throw new BroException("\tSorry bro, you can't have an empty event.");
        }
        Matcher matcher = EVENT_PATTERN.matcher(arguments.trim());
        if (matcher.find()) {
            return new String[] {
                    matcher.group("task").trim(),
                    matcher.group("start").trim(),
                    matcher.group("end").trim()
            };
        } else {
            throw new BroException("\t"
                    + "I think you messed up the event format bro, write 'event [task] /from [start] /to [end]'");
        }
    }

    /**
     * Parses a query date string for searching tasks.
     *
     * @param arguments The argument string containing the target date.
     * @return The parsed LocalDate.
     * @throws BroException If arguments is empty or invalid.
     */
    public static LocalDate parseQueryDate(String arguments) throws BroException {
        return TaskDateTime.parseQueryDate(arguments);
    }

    /**
     * Parses the arguments for the find command into an array of keywords delimited by commas.
     *
     * @param arguments The raw arguments string containing comma-separated keywords.
     * @return An array of trimmed, non-empty keyword strings.
     * @throws BroException If arguments is null, empty, or contains only whitespace/commas.
     */
    public static String[] parseFindKeywords(String arguments) throws BroException {
        if (arguments == null || arguments.trim().isEmpty()) {
            throw new BroException("\tBro, what are you trying to find? Please provide some keywords.");
        }
        String[] rawKeywords = arguments.split(",");
        ArrayList<String> validKeywords = new ArrayList<>();
        for (String rawKeyword : rawKeywords) {
            String trimmed = rawKeyword.trim();
            if (!trimmed.isEmpty()) {
                validKeywords.add(trimmed);
            }
        }
        if (validKeywords.isEmpty()) {
            throw new BroException("\tBro, what are you trying to find? Please provide some keywords.");
        }
        return validKeywords.toArray(new String[0]);
    }
}
