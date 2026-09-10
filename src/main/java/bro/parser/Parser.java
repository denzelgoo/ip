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
    private static final String ERROR_EMPTY_TODO = "Sorry bro, you can't have an empty todo.";
    private static final String ERROR_EMPTY_DEADLINE = "Sorry bro, you can't have an empty deadline.";
    private static final String ERROR_INVALID_DEADLINE_FORMAT =
            "I think you forgot to add the deadline bro, write 'deadline [description] /by [deadline]'";
    private static final String ERROR_EMPTY_EVENT = "Sorry bro, you can't have an empty event.";
    private static final String ERROR_INVALID_EVENT_FORMAT =
            "I think you messed up the event format bro, write 'event [description] /from [start] /to [end]'";
    private static final String ERROR_EMPTY_FIND_KEYWORDS =
            "Bro, what are you trying to find? Please provide some keywords.";

    private static final Pattern DEADLINE_PATTERN = Pattern
            .compile("(?<description>.+?)\\s+/by\\s+(?<deadline>.+)");
    private static final Pattern EVENT_PATTERN = Pattern
            .compile("(?<description>.+?)\\s+/from\\s+(?<start>.+?)\\s+/to\\s+(?<end>.+)");

    /**
     * Constructs a Parser instance.
     */
    public Parser() {
    }

    /**
     * Parses the raw input string into a {@link ParsedInput} containing both
     * the Command type and the argument string in a single operation.
     *
     * @param fullCommand The raw input line entered by the user.
     * @return A ParsedInput containing the Command and arguments.
     */
    public static ParsedInput parse(String fullCommand) {
        if (fullCommand == null || fullCommand.trim().isEmpty()) {
            return new ParsedInput(Command.UNKNOWN, "");
        }
        String[] parts = fullCommand.trim().split(" ", 2);
        Command command = Command.fromString(parts[0]);
        String arguments = parts.length > 1 ? parts[1].trim() : "";
        return new ParsedInput(command, arguments);
    }

    /**
     * Parses the raw input string to determine the Command type.
     *
     * @param fullCommand The raw input line entered by the user.
     * @return The corresponding Command enum constant.
     */
    public static Command parseCommand(String fullCommand) {
        Command command = parse(fullCommand).command();

        assert command != null : "Command.fromString should never return null";

        return command;
    }

    /**
     * Parses the raw input string to extract the arguments string.
     *
     * @param fullCommand The raw input line entered by the user.
     * @return The argument string, or an empty string if no arguments were provided.
     */
    public static String parseArguments(String fullCommand) {
        return parse(fullCommand).arguments();
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
            throw new BroException(ERROR_EMPTY_TODO);
        }
        return arguments.trim();
    }

    /**
     * Parses arguments for a deadline task into a {@link DeadlineDetails} record.
     *
     * @param arguments The raw arguments string.
     * @return A DeadlineDetails record containing description and deadline.
     * @throws BroException If arguments is empty or the format is invalid.
     */
    public static DeadlineDetails parseDeadlineDetails(String arguments) throws BroException {
        if (arguments == null || arguments.trim().isEmpty()) {
            throw new BroException(ERROR_EMPTY_DEADLINE);
        }
        Matcher matcher = DEADLINE_PATTERN.matcher(arguments.trim());
        if (!matcher.find()) {
            throw new BroException(ERROR_INVALID_DEADLINE_FORMAT);
        }
        String description = matcher.group("description").trim();
        String deadline = matcher.group("deadline").trim();
        if (description.isEmpty() || deadline.isEmpty()) {
            throw new BroException(ERROR_INVALID_DEADLINE_FORMAT);
        }
        return new DeadlineDetails(description, deadline);
    }

    /**
     * Parses arguments for a deadline task into description and deadline date/time.
     *
     * @param arguments The raw arguments string.
     * @return A 2-element array containing [description, deadlineStr].
     * @throws BroException If arguments is empty or the format is invalid.
     */
    public static String[] parseDeadlineArguments(String arguments) throws BroException {
        DeadlineDetails details = parseDeadlineDetails(arguments);
        String[] detailsArray = new String[] { details.description(), details.deadline() };

        assert detailsArray.length == 2 && !detailsArray[0].isBlank() && !detailsArray[1].isBlank()
                : "Deadline details array must contain exactly 2 non-blank parts";

        return detailsArray;
    }

    /**
     * Parses arguments for an event task into an {@link EventDetails} record.
     *
     * @param arguments The raw arguments string.
     * @return An EventDetails record containing description, start, and end.
     * @throws BroException If arguments is empty or the format does not match the event pattern.
     */
    public static EventDetails parseEventDetails(String arguments) throws BroException {
        if (arguments == null || arguments.trim().isEmpty()) {
            throw new BroException(ERROR_EMPTY_EVENT);
        }
        Matcher matcher = EVENT_PATTERN.matcher(arguments.trim());
        if (!matcher.find()) {
            throw new BroException(ERROR_INVALID_EVENT_FORMAT);
        }
        String description = matcher.group("description").trim();
        String start = matcher.group("start").trim();
        String end = matcher.group("end").trim();
        if (description.isEmpty() || start.isEmpty() || end.isEmpty()) {
            throw new BroException(ERROR_INVALID_EVENT_FORMAT);
        }
        return new EventDetails(description, start, end);
    }

    /**
     * Parses arguments for an event task into description, start date/time, and end date/time.
     *
     * @param arguments The raw arguments string.
     * @return A 3-element array containing [description, startStr, endStr].
     * @throws BroException If arguments is empty or the format does not match the event pattern.
     */
    public static String[] parseEventArguments(String arguments) throws BroException {
        EventDetails details = parseEventDetails(arguments);
        return new String[] { details.description(), details.start(), details.end() };
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
            throw new BroException(ERROR_EMPTY_FIND_KEYWORDS);
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
            throw new BroException(ERROR_EMPTY_FIND_KEYWORDS);
        }

        // validKeywords should not be empty if no exception was thrown
        assert validKeywords.size() > 0 : "Parsed keywords list should have at least 1 keyword";

        return validKeywords.toArray(new String[0]);
    }
}
