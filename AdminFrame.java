import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;

/**
 * AdminFrame: the Admin Panel.
 * The admin can see all questions (in a table), add new ones and delete old ones.
 * All questions live in the ArrayList inside ExamSystem.
 */
public class AdminFrame extends JFrame {

    private ExamSystem system;

    private DefaultTableModel tableModel;   // the data behind the table
    private JTable table;
    private JLabel countLabel;

    private JTextField questionField = new JTextField();
    private JTextField[] optionFields = new JTextField[4];
    private JComboBox<String> correctBox = new JComboBox<>(new String[]{"1", "2", "3", "4"});

    public AdminFrame(ExamSystem system) {
        this.system = system;

        setTitle("Admin Panel - Question Bank");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(950, 650);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        add(buildHeader(), BorderLayout.NORTH);
        add(buildTable(), BorderLayout.CENTER);
        add(buildForm(), BorderLayout.SOUTH);

        refreshTable();   // fill the table with the questions from the ArrayList
    }

    // ----- Top bar: title, question count, logout button -----
    private JPanel buildHeader() {
        JLabel title = new JLabel("Admin Panel - Question Bank");
        title.setFont(new Font("SansSerif", Font.BOLD, 20));

        countLabel = new JLabel();

        JButton logoutBtn = new JButton("Logout");
        logoutBtn.addActionListener(e -> {
            dispose();
            new LoginFrame(system).setVisible(true);
        });

        JPanel left = new JPanel(new GridLayout(2, 1));
        left.add(title);
        left.add(countLabel);

        JPanel header = new JPanel(new BorderLayout());
        header.setBorder(BorderFactory.createEmptyBorder(12, 15, 0, 15));
        JButton rankBtn = new JButton("View Rank List");
        rankBtn.addActionListener(e -> new ResultFrame(system).setVisible(true));
        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        right.add(rankBtn);
        right.add(logoutBtn);

        header.add(left, BorderLayout.WEST);
        header.add(right, BorderLayout.EAST);
        return header;
    }

    // ----- Middle: table that shows every question -----
    private JScrollPane buildTable() {
        String[] columns = {"No.", "Question", "Option 1", "Option 2", "Option 3", "Option 4", "Correct"};

        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;   // the admin cannot type directly into the table
            }
        };

        table = new JTable(tableModel);
        table.setRowHeight(26);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getColumnModel().getColumn(0).setPreferredWidth(40);
        table.getColumnModel().getColumn(1).setPreferredWidth(280);
        table.getColumnModel().getColumn(6).setPreferredWidth(60);

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createEmptyBorder(0, 15, 0, 15));
        return scroll;
    }

    // ----- Bottom: form to add a question + delete button -----
    private JPanel buildForm() {
        JPanel form = new JPanel(new GridLayout(0, 2, 8, 6));
        form.add(new JLabel("Question:"));
        form.add(questionField);
        for (int i = 0; i < 4; i++) {
            optionFields[i] = new JTextField();
            form.add(new JLabel("Option " + (i + 1) + ":"));
            form.add(optionFields[i]);
        }
        form.add(new JLabel("Correct option (1-4):"));
        form.add(correctBox);

        JButton addBtn = new JButton("Add Question");
        JButton deleteBtn = new JButton("Delete Selected Question");
        addBtn.addActionListener(e -> addQuestion());
        deleteBtn.addActionListener(e -> deleteSelectedQuestion());

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 5));
        buttons.add(addBtn);
        buttons.add(deleteBtn);

        JPanel bottom = new JPanel(new BorderLayout(0, 8));
        bottom.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(5, 15, 12, 15),
                BorderFactory.createTitledBorder("Add a new question")));
        bottom.add(form, BorderLayout.CENTER);
        bottom.add(buttons, BorderLayout.SOUTH);
        return bottom;
    }

    // ----- Actions -----

    // Reads the form, checks it, and adds a new Question to the ArrayList
    private void addQuestion() {
        String text = questionField.getText().trim();
        String[] options = new String[4];

        for (int i = 0; i < 4; i++) {
            options[i] = optionFields[i].getText().trim();
            if (options[i].isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please fill all 4 options.",
                        "Missing data", JOptionPane.WARNING_MESSAGE);
                return;
            }
        }
        if (text.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please type the question.",
                    "Missing data", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int correct = Integer.parseInt((String) correctBox.getSelectedItem());
        system.addQuestion(new Question(text, options, correct));

        // clear the form
        questionField.setText("");
        for (JTextField f : optionFields) {
            f.setText("");
        }
        correctBox.setSelectedIndex(0);

        refreshTable();
    }

    // Deletes the question the admin selected in the table
    private void deleteSelectedQuestion() {
        int row = table.getSelectedRow();   // row number in the table = index in the ArrayList
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Please select a question in the table first.",
                    "Nothing selected", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int sure = JOptionPane.showConfirmDialog(this, "Delete question " + (row + 1) + "?",
                "Confirm delete", JOptionPane.YES_NO_OPTION);
        if (sure == JOptionPane.YES_OPTION) {
            system.deleteQuestion(row);
            refreshTable();
        }
    }

    // Clears the table and refills it from the ArrayList
    private void refreshTable() {
        tableModel.setRowCount(0);
        ArrayList<Question> list = system.getQuestionBank();

        for (int i = 0; i < list.size(); i++) {
            Question q = list.get(i);
            String[] o = q.getOptions();
            tableModel.addRow(new Object[]{i + 1, q.getText(), o[0], o[1], o[2], o[3], q.getCorrectOption()});
        }
        countLabel.setText("Total questions: " + list.size());
    }
}
