import javax.swing.*;
import java.awt.*;

/**
 * LoginFrame: the first window the user sees.
 * The user chooses to enter as Admin or as Student.
 */
public class LoginFrame extends JFrame {

    private ExamSystem system;
    private static final String ADMIN_PASSWORD = "admin123";

    public LoginFrame(ExamSystem system) {
        this.system = system;

        setTitle("Online Examination System");
        setDefaultCloseOperation(EXIT_ON_CLOSE);   // closing this window ends the program
        setSize(420, 330);
        setLocationRelativeTo(null);                // centre on screen
        setLayout(new BorderLayout());

        // ----- Top: title -----
        JLabel title = new JLabel("Online Examination System", SwingConstants.CENTER);
        title.setFont(new Font("SansSerif", Font.BOLD, 22));
        JLabel subtitle = new JLabel("Please choose how you want to continue", SwingConstants.CENTER);

        JPanel top = new JPanel(new GridLayout(2, 1));
        top.setBorder(BorderFactory.createEmptyBorder(25, 10, 10, 10));
        top.add(title);
        top.add(subtitle);

        // ----- Centre: two big buttons -----
        JButton adminBtn = new JButton("Admin Login");
        JButton studentBtn = new JButton("Student Portal");
        adminBtn.setFont(new Font("SansSerif", Font.PLAIN, 16));
        studentBtn.setFont(new Font("SansSerif", Font.PLAIN, 16));

        JPanel centre = new JPanel(new GridLayout(2, 1, 0, 15));
        centre.setBorder(BorderFactory.createEmptyBorder(15, 70, 40, 70));
        centre.add(adminBtn);
        centre.add(studentBtn);

        add(top, BorderLayout.NORTH);
        add(centre, BorderLayout.CENTER);

        // ----- What happens when buttons are clicked -----
        adminBtn.addActionListener(e -> adminLogin());
        studentBtn.addActionListener(e -> {
            dispose();
            new StudentFrame(system).setVisible(true);
        });
    }

    // Asks for the admin password and opens the Admin Panel if it is correct
    private void adminLogin() {
        JPasswordField passwordField = new JPasswordField();
        int choice = JOptionPane.showConfirmDialog(this, passwordField,
                "Enter admin password", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (choice == JOptionPane.OK_OPTION) {
            String entered = new String(passwordField.getPassword());
            if (entered.equals(ADMIN_PASSWORD)) {
                dispose();                                   // close the login window
                new AdminFrame(system).setVisible(true);     // open the admin window
            } else {
                JOptionPane.showMessageDialog(this, "Wrong password!",
                        "Login failed", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
