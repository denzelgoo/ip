package bro.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.ArrayList;

import org.junit.jupiter.api.Test;

import bro.exception.BroException;

/**
 * Unit tests for {@link TaskList}.
 */
public class TaskListTest {

    // -------------------------------------------------------------------------
    // Constructor tests
    // -------------------------------------------------------------------------

    @Test
    public void testDefaultConstructor_createsEmptyList() {
        TaskList taskList = new TaskList();
        assertTrue(taskList.isEmpty());
        assertEquals(0, taskList.size());
        assertNotNull(taskList.getAllTasks());
        assertEquals(0, taskList.getAllTasks().size());
    }

    @Test
    public void testParameterizedConstructor_withExistingTasks() {
        ArrayList<Task> initialTasks = new ArrayList<>();
        initialTasks.add(new Todo("read book"));
        initialTasks.add(new Todo("write tests"));

        TaskList taskList = new TaskList(initialTasks);
        assertFalse(taskList.isEmpty());
        assertEquals(2, taskList.size());
        assertEquals("read book", taskList.get(0).getTask());
        assertEquals("write tests", taskList.get(1).getTask());
    }

    @Test
    public void testParameterizedConstructor_withNull_createsEmptyList() {
        TaskList taskList = new TaskList(null);
        assertTrue(taskList.isEmpty());
        assertEquals(0, taskList.size());
        assertNotNull(taskList.getAllTasks());
    }

    // -------------------------------------------------------------------------
    // add, get, size, isEmpty tests
    // -------------------------------------------------------------------------

    @Test
    public void testAddAndGet() {
        TaskList taskList = new TaskList();
        Todo todo1 = new Todo("buy groceries");
        Todo todo2 = new Todo("clean room");

        taskList.add(todo1);
        assertEquals(1, taskList.size());
        assertFalse(taskList.isEmpty());
        assertEquals(todo1, taskList.get(0));

        taskList.add(todo2);
        assertEquals(2, taskList.size());
        assertEquals(todo2, taskList.get(1));
    }

    @Test
    public void testGet_outOfBounds_throwsException() {
        TaskList taskList = new TaskList();
        assertThrows(IndexOutOfBoundsException.class, () -> taskList.get(0));

        taskList.add(new Todo("task 1"));
        assertThrows(IndexOutOfBoundsException.class, () -> taskList.get(-1));
        assertThrows(IndexOutOfBoundsException.class, () -> taskList.get(1));
    }

    // -------------------------------------------------------------------------
    // delete tests
    // -------------------------------------------------------------------------

    @Test
    public void testDelete_validIndex() {
        TaskList taskList = new TaskList();
        Todo todo1 = new Todo("task 1");
        Todo todo2 = new Todo("task 2");
        Todo todo3 = new Todo("task 3");

        taskList.add(todo1);
        taskList.add(todo2);
        taskList.add(todo3);

        Task removed = taskList.delete(1);
        assertEquals(todo2, removed);
        assertEquals(2, taskList.size());
        assertEquals(todo1, taskList.get(0));
        assertEquals(todo3, taskList.get(1));
    }

    @Test
    public void testDelete_outOfBounds_throwsException() {
        TaskList taskList = new TaskList();
        assertThrows(IndexOutOfBoundsException.class, () -> taskList.delete(0));

        taskList.add(new Todo("task 1"));
        assertThrows(IndexOutOfBoundsException.class, () -> taskList.delete(-1));
        assertThrows(IndexOutOfBoundsException.class, () -> taskList.delete(1));
    }

    // -------------------------------------------------------------------------
    // findTasksOnDate tests
    // -------------------------------------------------------------------------

    @Test
    public void testFindTasksOnDate_filtersCorrectly() throws BroException {
        TaskList taskList = new TaskList();
        LocalDate targetDate = LocalDate.of(2026, 8, 28);

        // 1. Uncompleted Todo (should be included)
        Todo uncompletedTodo = new Todo("uncompleted todo");
        taskList.add(uncompletedTodo);

        // 2. Completed Todo (should be excluded)
        Todo completedTodo = new Todo("completed todo");
        completedTodo.markDone();
        taskList.add(completedTodo);

        // 3. Deadline on target date (should be included)
        Deadline matchingDeadline = new Deadline("submit assignment", "28/8/2026 2359");
        taskList.add(matchingDeadline);

        // 4. Deadline on different date (should be excluded)
        Deadline nonMatchingDeadline = new Deadline("pay bills", "29/8/2026 2359");
        taskList.add(nonMatchingDeadline);

        // 5. Event spanning across target date (should be included)
        Event matchingEvent = new Event("hackathon", "27/8/2026 0900", "29/8/2026 1800");
        taskList.add(matchingEvent);

        // 6. Event outside target date (should be excluded)
        Event pastEvent = new Event("workshop", "20/8/2026 1000", "21/8/2026 1200");
        taskList.add(pastEvent);

        ArrayList<Task> results = taskList.findTasksOnDate(targetDate);
        assertEquals(3, results.size());
        assertTrue(results.contains(uncompletedTodo));
        assertTrue(results.contains(matchingDeadline));
        assertTrue(results.contains(matchingEvent));
        assertFalse(results.contains(completedTodo));
        assertFalse(results.contains(nonMatchingDeadline));
        assertFalse(results.contains(pastEvent));
    }

    @Test
    public void testFindTasksOnDate_emptyList_returnsEmptyList() {
        TaskList taskList = new TaskList();
        LocalDate targetDate = LocalDate.of(2026, 8, 28);
        ArrayList<Task> results = taskList.findTasksOnDate(targetDate);

        assertNotNull(results);
        assertTrue(results.isEmpty());
    }

    // -------------------------------------------------------------------------
    // findTasksByKeywords tests
    // -------------------------------------------------------------------------

    @Test
    public void testFindTasksByKeywords_matchingKeyword_returnsMatchingTasks() throws BroException {
        TaskList taskList = new TaskList();
        Todo todo1 = new Todo("read book");
        Deadline deadline = new Deadline("return book to library", "28/8/2026 2359");
        Event event = new Event("book club meeting", "28/8/2026 1400", "28/8/2026 1600");
        Todo todo2 = new Todo("buy groceries");

        taskList.add(todo1);
        taskList.add(deadline);
        taskList.add(event);
        taskList.add(todo2);

        ArrayList<Task> matchingTasks = taskList.findTasksByKeywords("book");
        assertEquals(3, matchingTasks.size());
        assertTrue(matchingTasks.contains(todo1));
        assertTrue(matchingTasks.contains(deadline));
        assertTrue(matchingTasks.contains(event));
        assertFalse(matchingTasks.contains(todo2));
    }

    @Test
    public void testFindTasksByKeywords_noMatch_returnsEmptyList() {
        TaskList taskList = new TaskList();
        taskList.add(new Todo("read book"));
        taskList.add(new Todo("buy groceries"));

        ArrayList<Task> matchingTasks = taskList.findTasksByKeywords("gym");
        assertNotNull(matchingTasks);
        assertTrue(matchingTasks.isEmpty());
    }

    @Test
    public void testFindTasksByKeywords_emptyList_returnsEmptyList() {
        TaskList taskList = new TaskList();
        ArrayList<Task> matchingTasks = taskList.findTasksByKeywords("book");

        assertNotNull(matchingTasks);
        assertTrue(matchingTasks.isEmpty());
    }

    @Test
    public void testFindTasksByKeywords_phraseOrSubstringMatch() {
        TaskList taskList = new TaskList();
        Todo todo = new Todo("clean living room");
        taskList.add(todo);
        taskList.add(new Todo("clean kitchen"));

        ArrayList<Task> matchingTasks = taskList.findTasksByKeywords("living room");
        assertEquals(1, matchingTasks.size());
        assertEquals(todo, matchingTasks.get(0));
    }

    // -------------------------------------------------------------------------
    // getAllTasks tests
    // -------------------------------------------------------------------------

    @Test
    public void testGetAllTasks_returnsUnderlyingList() {
        TaskList taskList = new TaskList();
        Todo todo = new Todo("buy milk");
        taskList.add(todo);

        ArrayList<Task> allTasks = taskList.getAllTasks();
        assertEquals(1, allTasks.size());
        assertEquals(todo, allTasks.get(0));
    }
}