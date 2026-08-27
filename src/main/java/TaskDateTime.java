import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Locale;

/**
 * Encapsulates date and time handling for tasks.
 * Supports parsing recognized date and date-time formats into java.time objects,
 * while gracefully falling back to storing unparseable inputs as raw strings.
 */
public class TaskDateTime {
    private static final DateTimeFormatter[] DATE_TIME_FORMATTERS = {
            DateTimeFormatter.ofPattern("d/M/yyyy HHmm"),
            DateTimeFormatter.ofPattern("yyyy-MM-dd HHmm"),
            DateTimeFormatter.ofPattern("d-M-yyyy HHmm"),
            DateTimeFormatter.ofPattern("d/M/yyyy HH:mm"),
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"),
            DateTimeFormatter.ofPattern("d-M-yyyy HH:mm"),
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm")
    };

    private static final DateTimeFormatter[] DATE_FORMATTERS = {
            DateTimeFormatter.ofPattern("yyyy-MM-dd"),
            DateTimeFormatter.ofPattern("d/M/yyyy"),
            DateTimeFormatter.ofPattern("d-M-yyyy"),
            DateTimeFormatter.ofPattern("yyyy/M/d")
    };

    private static final DateTimeFormatter DISPLAY_DATE_TIME =
            DateTimeFormatter.ofPattern("MMM dd yyyy, h:mma", Locale.ENGLISH);
    private static final DateTimeFormatter DISPLAY_DATE =
            DateTimeFormatter.ofPattern("MMM dd yyyy", Locale.ENGLISH);

    private static final DateTimeFormatter FILE_DATE_TIME =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HHmm");
    private static final DateTimeFormatter FILE_DATE =
            DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private final LocalDateTime dateTime;
    private final boolean hasTime;
    private final String rawString;

    /**
     * Constructs a TaskDateTime instance with a LocalDateTime and time flag.
     *
     * @param dateTime The LocalDateTime value.
     * @param hasTime  True if time of day was provided, false if date only.
     */
    public TaskDateTime(LocalDateTime dateTime, boolean hasTime) {
        this(dateTime, hasTime, null);
    }

    /**
     * Constructs a TaskDateTime instance with a raw string fallback.
     *
     * @param rawString The raw date/time string.
     */
    public TaskDateTime(String rawString) {
        this(null, false, rawString);
    }

    /**
     * Private full constructor for TaskDateTime.
     *
     * @param dateTime  The parsed LocalDateTime or null.
     * @param hasTime   True if time of day was provided.
     * @param rawString The raw string input or null.
     */
    private TaskDateTime(LocalDateTime dateTime, boolean hasTime, String rawString) {
        this.dateTime = dateTime;
        this.hasTime = hasTime;
        this.rawString = rawString;
    }

    /**
     * Parses a date or date-time string into a TaskDateTime object.
     * If the input matches a recognized format, it is parsed into a LocalDateTime.
     * Otherwise, it is preserved as a raw string so non-standard inputs (e.g., "2pm", "Sunday") continue to work.
     *
     * @param input The raw input string containing date or date-time information.
     * @return A parsed TaskDateTime instance.
     * @throws BroException If the input string is null or empty.
     */
    public static TaskDateTime parse(String input) throws BroException {
        if (input == null || input.trim().isEmpty()) {
            throw new BroException("\tBro, you can't have an empty date!");
        }

        String trimmed = input.trim();

        // 1. Try parsing with date-time formatters
        for (DateTimeFormatter formatter : DATE_TIME_FORMATTERS) {
            try {
                LocalDateTime parsedDateTime = LocalDateTime.parse(trimmed, formatter);
                return new TaskDateTime(parsedDateTime, true, trimmed);
            } catch (DateTimeParseException ignored) {
                // Try next formatter
            }
        }

        // 2. Try parsing with date-only formatters
        for (DateTimeFormatter formatter : DATE_FORMATTERS) {
            try {
                LocalDate parsedDate = LocalDate.parse(trimmed, formatter);
                return new TaskDateTime(parsedDate.atStartOfDay(), false, trimmed);
            } catch (DateTimeParseException ignored) {
                // Try next formatter
            }
        }

        // 3. Fallback: store as raw string if not matching any date/time pattern
        return new TaskDateTime(null, false, trimmed);
    }

    /**
     * Returns a user-friendly string representation of the date/time.
     * If parsed as a date/time object, returns formatted string (e.g., "Oct 15 2019" or "Dec 02 2019, 6:00PM").
     * Otherwise, returns the raw input string (e.g., "2pm").
     *
     * @return Formatted date display string or raw string.
     */
    public String formatDisplay() {
        if (dateTime != null) {
            if (hasTime) {
                return dateTime.format(DISPLAY_DATE_TIME);
            }
            return dateTime.format(DISPLAY_DATE);
        }
        return rawString;
    }

    /**
     * Returns a standardized string representation of the date/time suitable for file storage.
     * If parsed as a date/time object, returns standard format (e.g., "2019-10-15" or "2019-12-02 1800").
     * Otherwise, returns the raw input string (e.g., "2pm").
     *
     * @return Formatted storage string or raw string.
     */
    public String formatFile() {
        if (dateTime != null) {
            if (hasTime) {
                return dateTime.format(FILE_DATE_TIME);
            }
            return dateTime.format(FILE_DATE);
        }
        return rawString;
    }

    /**
     * Gets the underlying LocalDateTime if parsed, or null if stored as a raw string.
     *
     * @return The LocalDateTime instance, or null.
     */
    public LocalDateTime getDateTime() {
        return dateTime;
    }

    /**
     * Returns whether this date-time object includes a specific time of day.
     *
     * @return True if time is included, false otherwise.
     */
    public boolean hasTime() {
        return hasTime;
    }

    /**
     * Returns whether the date/time was successfully parsed into a java.time object.
     *
     * @return True if parsed as java.time object, false if stored as raw string.
     */
    public boolean isParsedDateTime() {
        return dateTime != null;
    }

    /**
     * Gets the raw string input.
     *
     * @return The raw string representation.
     */
    public String getRawString() {
        return rawString;
    }
}