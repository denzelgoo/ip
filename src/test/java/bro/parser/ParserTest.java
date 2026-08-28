package bro.parser;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

import bro.command.Command;
import bro.exception.BroException;

/**
 * Unit tests for {@link Parser}.
 */
public class ParserTest {

    @Test
    public void testParserInstantiation() {
        Parser parser = new Parser();
        assertNotNull(parser);
    }

    // -------------------------------------------------------------------------
    // parseCommand tests
    // -------------------------------------------------------------------------

    @Test
    public void testParseCommand_validCommands_success() {
        assertEquals(Command.TODO, Parser.parseCommand("todo test task"));
        assertEquals(Command.DEADLINE, Parser.parseCommand("deadline test /by 28/8/2026"));
        assertEquals(Command.EVENT, Parser.parseCommand("event test /from 28/8/2026 /to 29/8/2026"));
        assertEquals(Command.LIST, Parser.parseCommand("list"));
        assertEquals(Command.TASKS, Parser.parseCommand("tasks 28/8/2026"));
        assertEquals(Command.MARK, Parser.parseCommand("mark 1"));
        assertEquals(Command.UNMARK, Parser.parseCommand("unmark 1"));
        assertEquals(Command.DELETE, Parser.parseCommand("delete 1"));
        assertEquals(Command.BYE, Parser.parseCommand("bye"));
    }

    @Test
    public void testParseCommand_unknownAndEmpty_returnsUnknown() {
        assertEquals(Command.UNKNOWN, Parser.parseCommand("unknownCommand 123"));
        assertEquals(Command.UNKNOWN, Parser.parseCommand(""));
        assertEquals(Command.UNKNOWN, Parser.parseCommand("   "));
        assertEquals(Command.UNKNOWN, Parser.parseCommand(null));
    }

    // -------------------------------------------------------------------------
    // parseArguments tests
    // -------------------------------------------------------------------------

    @Test
    public void testParseArguments_withArguments_success() {
        assertEquals("read book", Parser.parseArguments("todo read book"));
        assertEquals("return book /by 28/8/2026", Parser.parseArguments("deadline return book /by 28/8/2026"));
    }

    @Test
    public void testParseArguments_noArgumentsOrEmpty_returnsEmptyString() {
        assertEquals("", Parser.parseArguments("list"));
        assertEquals("", Parser.parseArguments("bye"));
        assertEquals("", Parser.parseArguments(""));
        assertEquals("", Parser.parseArguments("   "));
        assertEquals("", Parser.parseArguments(null));
    }

    // -------------------------------------------------------------------------
    // parseTaskIndex tests
    // -------------------------------------------------------------------------

    @Test
    public void testParseTaskIndex_validInput_returnsZeroBasedIndex() throws BroException {
        assertEquals(0, Parser.parseTaskIndex("1", "error message"));
        assertEquals(4, Parser.parseTaskIndex("5", "error message"));
        assertEquals(2, Parser.parseTaskIndex("  3  ", "error message"));
    }

    @Test
    public void testParseTaskIndex_nullOrEmptyInput_throwsBroException() {
        BroException e1 = assertThrows(BroException.class, () -> Parser.parseTaskIndex(null, "custom error 1"));
        assertEquals("custom error 1", e1.getMessage());

        BroException e2 = assertThrows(BroException.class, () -> Parser.parseTaskIndex("", "custom error 2"));
        assertEquals("custom error 2", e2.getMessage());

        BroException e3 = assertThrows(BroException.class, () -> Parser.parseTaskIndex("   ", "custom error 3"));
        assertEquals("custom error 3", e3.getMessage());
    }

    @Test
    public void testParseTaskIndex_nonNumericInput_throwsNumberFormatException() {
        assertThrows(NumberFormatException.class, () -> Parser.parseTaskIndex("abc", "error"));
        assertThrows(NumberFormatException.class, () -> Parser.parseTaskIndex("1.5", "error"));
    }

    // -------------------------------------------------------------------------
    // parseTodoDescription tests
    // -------------------------------------------------------------------------

    @Test
    public void testParseTodoDescription_validInput_returnsTrimmedDescription() throws BroException {
        assertEquals("buy milk", Parser.parseTodoDescription("buy milk"));
        assertEquals("buy groceries", Parser.parseTodoDescription("   buy groceries   "));
    }

    @Test
    public void testParseTodoDescription_nullOrEmpty_throwsBroException() {
        assertThrows(BroException.class, () -> Parser.parseTodoDescription(null));
        assertThrows(BroException.class, () -> Parser.parseTodoDescription(""));
        assertThrows(BroException.class, () -> Parser.parseTodoDescription("    "));
    }

    // -------------------------------------------------------------------------
    // parseDeadlineArguments tests
    // -------------------------------------------------------------------------

    @Test
    public void testParseDeadlineArguments_validInput_returnsDetails() throws BroException {
        String[] result = Parser.parseDeadlineArguments("return book /by 28/8/2026 1800");
        assertArrayEquals(new String[] { "return book", "28/8/2026 1800" }, result);

        String[] trimmedResult = Parser.parseDeadlineArguments("   submit report    /by    2026-08-28   ");
        assertArrayEquals(new String[] { "submit report", "2026-08-28" }, trimmedResult);
    }

    @Test
    public void testParseDeadlineArguments_nullOrEmpty_throwsBroException() {
        assertThrows(BroException.class, () -> Parser.parseDeadlineArguments(null));
        assertThrows(BroException.class, () -> Parser.parseDeadlineArguments(""));
        assertThrows(BroException.class, () -> Parser.parseDeadlineArguments("   "));
    }

    @Test
    public void testParseDeadlineArguments_missingByFlag_throwsBroException() {
        assertThrows(BroException.class, () -> Parser.parseDeadlineArguments("return book by 28/8/2026"));
    }

    @Test
    public void testParseDeadlineArguments_blankDescription_throwsBroException() {
        assertThrows(BroException.class, () -> Parser.parseDeadlineArguments("   /by 28/8/2026"));
    }

    @Test
    public void testParseDeadlineArguments_blankDeadline_throwsBroException() {
        assertThrows(BroException.class, () -> Parser.parseDeadlineArguments("return book /by   "));
    }

    // -------------------------------------------------------------------------
    // parseEventArguments tests
    // -------------------------------------------------------------------------

    @Test
    public void testParseEventArguments_validInput_returnsDetails() throws BroException {
        String[] result = Parser.parseEventArguments("project meeting /from 28/8/2026 1400 /to 28/8/2026 1600");
        assertArrayEquals(new String[] { "project meeting", "28/8/2026 1400", "28/8/2026 1600" }, result);

        String[] trimmedResult = Parser.parseEventArguments("  orientation  /from  2026-08-28  /to  2026-08-29  ");
        assertArrayEquals(new String[] { "orientation", "2026-08-28", "2026-08-29" }, trimmedResult);
    }

    @Test
    public void testParseEventArguments_nullOrEmpty_throwsBroException() {
        assertThrows(BroException.class, () -> Parser.parseEventArguments(null));
        assertThrows(BroException.class, () -> Parser.parseEventArguments(""));
        assertThrows(BroException.class, () -> Parser.parseEventArguments("    "));
    }

    @Test
    public void testParseEventArguments_missingFromFlag_throwsBroException() {
        assertThrows(BroException.class, () -> Parser.parseEventArguments("project meeting /to 28/8/2026 1600"));
    }

    @Test
    public void testParseEventArguments_missingToFlag_throwsBroException() {
        assertThrows(BroException.class, () -> Parser.parseEventArguments("project meeting /from 28/8/2026 1400"));
    }

    @Test
    public void testParseEventArguments_reversedFlags_throwsBroException() {
        assertThrows(BroException.class, () -> Parser
                .parseEventArguments("meeting /to 28/8/2026 1600 /from 28/8/2026 1400"));
    }

    // -------------------------------------------------------------------------
    // parseQueryDate tests
    // -------------------------------------------------------------------------

    @Test
    public void testParseQueryDate_validDate_returnsLocalDate() throws BroException {
        LocalDate expected = LocalDate.of(2026, 8, 28);
        assertEquals(expected, Parser.parseQueryDate("28/8/2026"));
        assertEquals(expected, Parser.parseQueryDate("2026-08-28"));
    }

    @Test
    public void testParseQueryDate_invalidOrEmpty_throwsBroException() {
        assertThrows(BroException.class, () -> Parser.parseQueryDate(""));
        assertThrows(BroException.class, () -> Parser.parseQueryDate("invalid date"));
    }
}