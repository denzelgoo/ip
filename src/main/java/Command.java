public enum Command {
    LIST,
    MARK,
    UNMARK,
    DELETE,
    TODO,
    DEADLINE,
    EVENT,
    BYE,
    UNKNOWN;

    public static Command fromString(String text) {
        if (text == null || text.trim().isEmpty()) {
            return UNKNOWN;
        }
        try {
            return Command.valueOf(text.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            return UNKNOWN;
        }
    }
}
