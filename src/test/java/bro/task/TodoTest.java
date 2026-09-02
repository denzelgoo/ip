package bro.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

/**
 * Unit tests for {@link Todo}.
 */
public class TodoTest {
    @Test
    public void toString_uncompletedTodo_formattedStringReturned() {
        assertEquals("[T][ ] test todo task", new Todo("test todo task").toString());
    }

    @Test
    public void toFileFormat_uncompletedTodo_formattedStringReturned() {
        assertEquals("T | 0 | test todo task", new Todo("test todo task").toFileFormat());
    }

    @Test
    public void markDone_uncompletedTodo_taskMarkedDone() {
        Todo todo = new Todo("test todo task");
        todo.markDone();
        assertTrue(todo.isDone());
        assertEquals("[T][X] test todo task", todo.toString());
        assertEquals("T | 1 | test todo task", todo.toFileFormat());
    }

    @Test
    public void unmarkDone_completedTodo_taskUnmarkedDone() {
        Todo todo = new Todo("test todo task");
        // mark as done first
        todo.markDone();
        assertTrue(todo.isDone());
        assertEquals("[T][X] test todo task", todo.toString());
        assertEquals("T | 1 | test todo task", todo.toFileFormat());

        // unmark as done
        todo.unmarkDone();
        assertFalse(todo.isDone());
        assertEquals("[T][ ] test todo task", todo.toString());
        assertEquals("T | 0 | test todo task", todo.toFileFormat());
    }

    @Test
    public void isOnDate_completedAndUncompletedTodo_correctBooleanReturned() {
        Todo todo = new Todo("test todo task");
        // todo is not done, so it should always return true
        LocalDate testDate = LocalDate.of(2026, 8, 28);
        assertTrue(todo.isOnDate(testDate));

        // mark as done, then it should return false
        todo.markDone();
        assertFalse(todo.isOnDate(testDate));
    }
}
