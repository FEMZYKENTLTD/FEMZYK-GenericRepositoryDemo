package com.femzyk.genericsdemo.gui;

import java.io.File;

import javax.imageio.ImageIO;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.control.TextField;
import javafx.scene.image.WritableImage;
import javafx.stage.Stage;

/**
 * Development utility (not part of the submitted program): renders the real
 * GenericsFxApp, drives it through a session, and saves a screenshot for
 * the README.
 */
public class SnapshotRunner extends Application {

    /** Directory where the screenshots are written (override with -Dsnapshot.dir=...). */
    private static final String OUT_DIR = System.getProperty("snapshot.dir", ".");

    @Override
    public void start(Stage stage) {
        try {
            new GenericsFxApp().start(stage);
            Thread.sleep(600);

            // Add one product through the generic validation chain.
            TextField nameField = (TextField) stage.getScene().lookup("#productNameField");
            TextField qtyField = (TextField) stage.getScene().lookup("#productQuantityField");
            nameField.setText("Gaming Mouse");
            qtyField.setText("12");
            fire(stage, "#addProductButton");
            Thread.sleep(400);

            Platform.runLater(() -> {
                try {
                    WritableImage image = stage.getScene().snapshot(null);
                    File output = new File(OUT_DIR, "screenshot-gui.png");
                    ImageIO.write(SwingFXUtils.fromFXImage(image, null), "png", output);
                    System.out.println("Saved screenshot: " + output.getPath());
                } catch (Exception error) {
                    error.printStackTrace();
                } finally {
                    Platform.exit();
                }
            });
        } catch (Exception error) {
            error.printStackTrace();
            Platform.exit();
        }
    }

    /** Fires the button with the given id. */
    private void fire(Stage stage, String buttonId) {
        javafx.scene.control.Button button =
                (javafx.scene.control.Button) stage.getScene().lookup(buttonId);
        if (button != null) {
            button.fire();
        }
    }

    /**
     * Launches the utility.
     *
     * @param args command-line arguments (not used)
     */
    public static void main(String[] args) {
        launch(args);
    }
}
