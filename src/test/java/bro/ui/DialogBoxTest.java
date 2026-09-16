package bro.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import javafx.application.Platform;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.shape.Circle;

/**
 * Unit tests for {@link DialogBox}.
 */
public class DialogBoxTest {
    @BeforeAll
    public static void initJavaFx() {
        try {
            Platform.startup(() -> {});
        } catch (IllegalStateException e) {
            // Platform already started
        }
    }

    @Test
    public void getUserDialog_validInput_createsDialogWithCircularClip() {
        Image img = new Image(DialogBoxTest.class.getResourceAsStream("/images/DaUser.png"));
        DialogBox db = DialogBox.getUserDialog("Hello bro!", img);
        assertNotNull(db);

        ImageView displayPicture = (ImageView) db.getChildren().stream()
                .filter(node -> node instanceof ImageView)
                .findFirst()
                .orElse(null);
        assertNotNull(displayPicture);
        assertNotNull(displayPicture.getClip());
        assertTrue(displayPicture.getClip() instanceof Circle);

        Circle circleClip = (Circle) displayPicture.getClip();
        assertEquals(25.0, circleClip.getRadius(), 0.001);
        assertEquals(25.0, circleClip.getCenterX(), 0.001);
        assertEquals(25.0, circleClip.getCenterY(), 0.001);
    }

    @Test
    public void getBroDialog_validInput_createsFlippedDialogWithCircularClip() {
        Image img = new Image(DialogBoxTest.class.getResourceAsStream("/images/DaBro.png"));
        DialogBox db = DialogBox.getBroDialog("What's up bro!", img);
        assertNotNull(db);

        ImageView displayPicture = (ImageView) db.getChildren().stream()
                .filter(node -> node instanceof ImageView)
                .findFirst()
                .orElse(null);
        assertNotNull(displayPicture);
        assertNotNull(displayPicture.getClip());
        assertTrue(displayPicture.getClip() instanceof Circle);

        Circle circleClip = (Circle) displayPicture.getClip();
        assertEquals(25.0, circleClip.getRadius(), 0.001);
        assertEquals(25.0, circleClip.getCenterX(), 0.001);
        assertEquals(25.0, circleClip.getCenterY(), 0.001);
    }
}
