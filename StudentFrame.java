import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

/**
 * StudentFrame: the Student Portal.
 * Students register here and join the waiting line (a Queue).
 * The "Call Next Student" button takes the student at the FRONT of the queue.
 */
public class StudentFrame extends JFrame {

    private ExamSystem system;

    private JTextField nameField = new JTextField();
    private DefaultTableModel tableModel;
    private JLabel queueInfoLabel = new JLabel();
    private JLabel nextLabel = new JLabel();

    public StudentFrame(ExamSystem system) {
        this.system = system;

        setTitle("Student Portal");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(900, 560);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        add(buildHeader(), BorderLayout.NORTH);
        add(buildRegisterPanel(), BorderLayout.WEST);
        add(buildQueuePanel(), BorderLayout.CENTER);
        add(buildBottomPanel(), BorderLayout.SOUTH);

        refreshQueue();
    }

    // ----- Top bar: title + back button -----
    private JPanel buildHeader() {
        JLabel title = new JLabel("Student Portal - Registration and Waiting Line");
        title.setFont(new Font("SansSerif", Font.BOLD, 20));

        JButton backBtn = new JButton("Back to Login");
        backBtn.addActionListener(e -> {
            dispose();
            new LoginFrame(system).setVisible(true);
        });

        JPanel header = new JPanel(new BorderLayout());
        header.setBorder(BorderFactory.createEmptyBorder(12, 15, 0, 15));
        header.add(title, BorderLayout.WEST);
        header.add(backBtn, BorderLayout.EAST);
        return header;
    }

    // ----- Left: registration form -----
    private JPanel buildRegisterPanel() {
        JButton registerBtn = new JButton("Register and Join Waiting Line");
        registerBtn.addActionListener(e -> registerStudent());
        nameField.addActionListener(e -> registerStudent());   // pressing Enter also registers

        JPanel form = new JPanel(new GridLayout(0, 1, 0, 8));
        form.add(new JLabel("Your full name:"));
        form.add(nameField);
        form.add(registerBtn);

        JPanel panel = new JPanel(new BorderLayout());
        panel.setPreferredSize(new Dimension(280, 0));
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(5, 15, 5, 5),
                BorderFactory.createTitledBorder("New Registration")));
        panel.add(form, BorderLayout.NORTH);
        return panel;
    }

    // ----- Centre: table showing the queue from front to back -----
    private JPanel buildQueuePanel() {
        String[] columns = {"Position", "Student ID", "Name"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        JTable table = new JTable(tableModel);
        table.setRowHeight(26);

        queueInfoLabel.setFont(new Font("SansSerif", Font.BOLD, 14));

        JPanel panel = new JPanel(new BorderLayout(0, 8));
        panel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 15));
        panel.add(queueInfoLabel, BorderLayout.NORTH);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        return panel;
    }

    // ----- Bottom: who is next + call button -----
    private JPanel buildBottomPanel() {
        JButton callBtn = new JButton("Call Next Student and Start Exam");
        callBtn.addActionListener(e -> callNextStudent());
        JButton rankBtn = new JButton("View Rank List");
        rankBtn.addActionListener(e -> new ResultFrame(system).setVisible(true));

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttons.add(rankBtn);
        buttons.add(callBtn);

        JPanel bottom = new JPanel(new BorderLayout());
        bottom.setBorder(BorderFactory.createEmptyBorder(0, 15, 15, 15));
        bottom.add(nextLabel, BorderLayout.WEST);
        bottom.add(buttons, BorderLayout.EAST);
        return bottom;
    }

    // ----- Actions -----

    // Registers a student: they are added to the BACK of the queue
    private void registerStudent() {
        String name = nameField.getText().trim();
        if (name.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter your name.",
                    "Missing data", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Student s = system.registerStudent(name);
        nameField.setText("");
        refreshQueue();

        JOptionPane.showMessageDialog(this,
                "Registration successful!\n\nStudent ID: " + s.getId()
                        + "\nYour position in the line: " + system.getQueueSize(),
                "Registered", JOptionPane.INFORMATION_MESSAGE);
    }

    // Takes the student at the FRONT of the queue and opens the exam for them
    private void callNextStudent() {
        if (system.getQuestionCount() == 0) {
            JOptionPane.showMessageDialog(this, "There are no questions yet.\nAsk the admin to add questions first.",
                    "No questions", JOptionPane.WARNING_MESSAGE);
            return;
        }
        Student s = system.callNextStudent();   // poll(): removes the FRONT student
        if (s == null) {
            JOptionPane.showMessageDialog(this, "No students are waiting.",
                    "Queue is empty", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        JOptionPane.showMessageDialog(this,
                "Now calling: " + s.getName() + " (ID " + s.getId() + ")\n\nClick OK to start the exam.\n"
                        + "You have 1 minute per question.",
                "Your turn", JOptionPane.INFORMATION_MESSAGE);
        dispose();
        new ExamFrame(system, s).setVisible(true);
    }

    // Redraws the table by reading the queue from front to back
    private void refreshQueue() {
        tableModel.setRowCount(0);
        int position = 1;
        for (Student s : system.getWaitingQueue()) {   // loops front -> back
            tableModel.addRow(new Object[]{position, s.getId(), s.getName()});
            position++;
        }

        queueInfoLabel.setText("Students waiting: " + system.getQueueSize());

        Student next = system.peekNextStudent();       // look at the front, do not remove
        nextLabel.setText(next == null ? "Next in line: nobody"
                : "Next in line: " + next.getName() + " (ID " + next.getId() + ")");
    }
}
