import javax.swing.*;

/**
 * Main: the starting point of the program.
 * It creates the ExamSystem (our data) and opens the Login window.
 */
public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                // if it fails, Java just uses its default look
            }
            ExamSystem system = new ExamSystem();
            new LoginFrame(system).setVisible(true);
        });
    }
}
