
import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.geometry.Pos;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

/**
 * A program that shows randomly generated "art". When the user
 * clicks a "Start" button, a new random artwork is generated every
 * two seconds.
 *
 * This program demonstrates using a thread for a very simple
 * animation. The thread uses Platform.runLater() to redraw
 * the canvas on the JavaFX application thread.
 */
public class ShapeMultiThread extends Application {
    public static void main(String[] args) {
        launch(args);
    }
    // -------------------------------------------------------------------

    private Canvas c2w_canvas; // A panel where the random "art" is drawn
    private volatile boolean c2w_running; // Set to true while thread is started;

    // It is set to false as a signal to

    // the thread that it should stop.

    private c2w_Runner c2w_runner; // The thread that drives the animation.

    // Class Runner is a nested class, defined below.
    private Button c2w_startButton; // A button that is used to start

    // and stop the animation.

    public void start(Stage stage) {
        c2w_canvas = new Canvas(640, 480);
        c2w_redraw(); // fill the canvas with white
        c2w_startButton = new Button("Start!");
        c2w_startButton.setOnAction(e -> c2w_doStartOrStop());
        HBox c2w_bottom = new HBox(c2w_startButton);
        c2w_bottom.setStyle("-fx-padding: 6px; -fx-border-color: black; -fx-border-width: 3px 0 0 0");
        c2w_bottom.setAlignment(Pos.CENTER);
        BorderPane c2w_root = new BorderPane(c2w_canvas);
        c2w_root.setBottom(c2w_bottom);
        Scene c2w_scene = new Scene(c2w_root);
        stage.setScene(c2w_scene);
        stage.setTitle("Click Start to Make Random Art!");
        stage.setResizable(false);
        stage.show();
    }

    /**
     * This class defines the threads that drive the animation.
     */
    private class c2w_Runner extends Thread {
        public void run() {
            while (c2w_running) {
                Platform.runLater(() -> c2w_redraw());
                try {
                    Thread.sleep(2000); // Wait two seconds between redraws.
                } catch (InterruptedException e) {

                }
            }
        }
    }

    /**
     * If called when an animation is running, this method
     * fills the canvas with a random work of "art". If no thread
     * is running, it just fills the canvas with white.
     */
    private void c2w_redraw() {
        GraphicsContext c2w_g = c2w_canvas.getGraphicsContext2D();
        double c2w_width = c2w_canvas.getWidth();
        double c2w_height = c2w_canvas.getHeight();
        if (!c2w_running) {
            c2w_g.setFill(Color.WHITE);
            c2w_g.fillRect(0, 0, c2w_width, c2w_height);
            return;
        }
        Color c2w_randomGray = Color.hsb(1, 0, Math.random());
        c2w_g.setFill(c2w_randomGray);
        c2w_g.fillRect(0, 0, c2w_width, c2w_height);
        int c2w_artType = (int) (3 * Math.random());
        switch (c2w_artType) {
            case 0:
                c2w_g.setLineWidth(2);
                for (int i = 0; i < 500; i++) {
                    int x1 = (int) (c2w_width * Math.random());
                    int y1 = (int) (c2w_height * Math.random());
                    int x2 = (int) (c2w_width * Math.random());
                    int y2 = (int) (c2w_height * Math.random());
                    Color randomHue = Color.hsb(360 * Math.random(), 1, 1);
                    c2w_g.setStroke(randomHue);
                    c2w_g.strokeLine(x1, y1, x2, y2);
                }
                break;

            case 1:
                for (int i = 0; i < 200; i++) {
                    int c2w_centerX = (int) (c2w_width * Math.random());
                    int c2w_centerY = (int) (c2w_height * Math.random());
                    Color c2w_randomHue = Color.hsb(360 * Math.random(), 1, 1);
                    c2w_g.setStroke(c2w_randomHue);
                    c2w_g.strokeOval(c2w_centerX - 50, c2w_centerY - 50, 100, 100);
                }
                break;
            default:
                c2w_g.setStroke(Color.BLACK);
                c2w_g.setLineWidth(4);
                for (int i = 0; i < 25; i++) {
                    int centerX = (int) (c2w_width * Math.random());
                    int centerY = (int) (c2w_height * Math.random());
                    int size = 30 + (int) (170 * Math.random());
                    Color randomColor = Color.color(Math.random(), Math.random(),
                            Math.random());
                    c2w_g.setFill(randomColor);
                    c2w_g.fillRect(centerX - size / 2, centerY - size / 2, size, size);
                    c2w_g.strokeRect(centerX - size / 2, centerY - size / 2, size, size);
                }
                break;
        }
    }

    /**
     * This method is called when the user clicks the Start button,
     * If no thread is running, it sets the signaling variable, running,
     * to true and starts a new thread; it also changes
     * the text on the Start button to "Stop". If the user clicks the button while
     * a thread is running, then a signal is sent to the thread to terminate,
     * by setting the value of the signaling variable, running, to false;
     * and the method also sets the text on the Start button back to "Start."
     */
    private void c2w_doStartOrStop() {
        if (c2w_running == false) { // start a thread

            c2w_startButton.setText("Stop");
            c2w_runner = new c2w_Runner();
            c2w_running = true; // Set the signal before starting the thread!
            c2w_runner.start();
        } else { // stop the running thread
            c2w_startButton.setDisable(true); // Disable button until thread exits,

            // so user can't start another
            // thread until after the current
            // thread exits.

            /*
             * Set the value of the signaling variable to false as a signal
             * to the thread to terminate.
             */
            c2w_running = false;
            c2w_redraw(); // Redraw the canvas, which will show only white since running = false.
            /*
             * Wake the thread, in case it is sleeping, to get a more
             * immediate reaction to the signal.
             */

            c2w_runner.interrupt();

            /*
             * Wait for the thread to stop before setting runner = null.
             * One second should be plenty of time for this to happen, but
             * in case something goes wrong, it's better not to wait forever.
             */
            try {
                c2w_runner.join(1000); // Wait for thread to stop.
            } catch (InterruptedException e) {
            }
            c2w_runner = null;

            c2w_startButton.setText("Start");
            c2w_startButton.setDisable(false);
        }
    }
} // end RandomArtWithThreads