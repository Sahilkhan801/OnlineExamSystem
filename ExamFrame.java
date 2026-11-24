import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.Stack;

/**
 * ExamFrame: the window where a student takes the exam.
 *
 * STACK: every time the student moves to another question, the question they LEFT
 * is pushed onto a Stack. The "Previous" button pops the stack, which returns to
 * the most recently visited question (Last In, First Out).
 */
public class ExamFrame extends JFrame {

    private ExamSystem system;
    private Student student;
    private ArrayList<Question> questions;   // snapshot of the question bank
    private int[] answers;                   // answers[i] = chosen option for question i (0 = not answered)
    private int current = 0;                 // index of the question on screen
    private Stack<Integer> history = new Stack<>();

    private JLabel questionLabel = new JLabel();
    private JRadioButton[] optionButtons = new JRadioButton[4];
    private ButtonGroup group = new ButtonGroup();
    private JLabel progressLabel = new JLabel();
    private JLabel timerLabel = new JLabel();
    private JButton prevBtn = new JButton("Previous");
    private JButton nextBtn = new JButton("Next");

    private javax.swing.Timer timer;
    private int secondsLeft;
    private boolean finished = false;

    public ExamFrame(ExamSystem system, Student student) {
        this.system = system;
        this.student = student;
        this.questions = new ArrayList<>(system.getQuestionBank());
        this.answers = new int[questions.size()];
        this.secondsLeft = questions.size() * 60;   // 1 minute per question

        setTitle("Exam - " + student.getName());
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        setSize(900, 560);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        add(buildHeader(), BorderLayout.NORTH);
        add(buildNavigator(), BorderLayout.WEST);
        add(buildQuestionPanel(), BorderLayout.CENTER);

        // closing the window with X asks to submit
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                submit(false);
            }
        });

        showQuestion();
        startTimer();
    }

    // ----- Top: student name and countdown -----
    private JPanel buildHeader() {
        JLabel title = new JLabel("Student: " + student.getName() + "  (ID " + student.getId() + ")");
        title.setFont(new Font("SansSerif", Font.BOLD, 18));
        timerLabel.setFont(new Font("SansSerif", Font.BOLD, 18));

        JPanel header = new JPanel(new BorderLayout());
        header.setBorder(BorderFactory.createEmptyBorder(12, 15, 0, 15));
        header.add(title, BorderLayout.WEST);
        header.add(timerLabel, BorderLayout.EAST);
        return header;
    }

    // ----- Left: numbered buttons to jump to any question -----
    private JScrollPane buildNavigator() {
        JPanel grid = new JPanel(new GridLayout(0, 4, 5, 5));
        for (int i = 0; i < questions.size(); i++) {
            final int index = i;
            JButton b = new JButton(String.valueOf(i + 1));
            b.setMargin(new Insets(4, 4, 4, 4));
            b.addActionListener(e -> goTo(index));
            grid.add(b);
        }
        JPanel holder = new JPanel(new BorderLayout());
        holder.add(grid, BorderLayout.NORTH);

        JScrollPane scroll = new JScrollPane(holder);
        scroll.setPreferredSize(new Dimension(190, 0));
        scroll.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(5, 15, 5, 0),
                BorderFactory.createTitledBorder("Questions")));
        return scroll;
    }

    // ----- Centre: question, 4 options, buttons -----
    private JPanel buildQuestionPanel() {
        questionLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        questionLabel.setVerticalAlignment(SwingConstants.TOP);

        JPanel options = new JPanel(new GridLayout(4, 1, 0, 6));
        for (int i = 0; i < 4; i++) {
            final int choice = i + 1;
            optionButtons[i] = new JRadioButton();
            optionButtons[i].setFont(new Font("SansSerif", Font.PLAIN, 16));
            optionButtons[i].addActionListener(e -> answers[current] = choice);   // remember the answer
            group.add(optionButtons[i]);
            options.add(optionButtons[i]);
        }

        prevBtn.addActionListener(e -> previous());
        nextBtn.addActionListener(e -> goTo(current + 1));
        JButton submitBtn = new JButton("Submit Exam");
        submitBtn.addActionListener(e -> submit(false));

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 5));
        buttons.add(prevBtn);
        buttons.add(nextBtn);
        buttons.add(submitBtn);

        JPanel south = new JPanel(new BorderLayout());
        south.add(progressLabel, BorderLayout.NORTH);
        south.add(buttons, BorderLayout.CENTER);

        JPanel panel = new JPanel(new BorderLayout(0, 15));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 15, 20));
        panel.add(questionLabel, BorderLayout.NORTH);
        panel.add(options, BorderLayout.CENTER);
        panel.add(south, BorderLayout.SOUTH);
        return panel;
    }

    // ----- Stack logic -----

    // Move to another question. The question we are LEAVING is pushed onto the stack.
    private void goTo(int index) {
        if (index < 0 || index >= questions.size() || index == current) {
            return;
        }
        history.push(current);   // push = remember where we were
        current = index;
        showQuestion();
    }

    // "Previous": pop the stack to go back to the last question we visited.
    private void previous() {
        if (history.isEmpty()) {
            return;
        }
        current = history.pop();   // pop = take the most recent question off the stack
        showQuestion();
    }

    // ----- Showing the current question -----
    private void showQuestion() {
        Question q = questions.get(current);
        questionLabel.setText("<html><body style='width:480px'>Q" + (current + 1) + ". "
                + escape(q.getText()) + "</body></html>");

        String[] opts = q.getOptions();
        group.clearSelection();
        for (int i = 0; i < 4; i++) {
            optionButtons[i].setText(opts[i]);
        }
        if (answers[current] != 0) {
            optionButtons[answers[current] - 1].setSelected(true);   // show the earlier answer
        }

        int answered = 0;
        for (int a : answers) {
            if (a != 0) answered++;
        }
        progressLabel.setText("Question " + (current + 1) + " of " + questions.size()
                + "   |   Answered: " + answered);

        prevBtn.setEnabled(!history.isEmpty());
        nextBtn.setEnabled(current < questions.size() - 1);
    }

    // Stops characters like < from breaking the HTML label
    private String escape(String s) {
        return s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
    }

    // ----- Timer -----
    private void startTimer() {
        updateTimerLabel();
        timer = new javax.swing.Timer(1000, e -> {
            secondsLeft--;
            updateTimerLabel();
            if (secondsLeft <= 0) {
                JOptionPane.showMessageDialog(this, "Time is up! Your exam will be submitted.",
                        "Time over", JOptionPane.INFORMATION_MESSAGE);
                submit(true);
            }
        });
        timer.start();
    }

    private void updateTimerLabel() {
        timerLabel.setText(String.format("Time left: %02d:%02d", secondsLeft / 60, secondsLeft % 60));
        timerLabel.setForeground(secondsLeft < 60 ? Color.RED : Color.BLACK);
    }

    // ----- Submit: calculate the score and save the result -----
    private void submit(boolean auto) {
        if (finished) {
            return;
        }
        if (!auto) {
            int unanswered = 0;
            for (int a : answers) {
                if (a == 0) unanswered++;
            }
            String msg = unanswered > 0
                    ? "You have " + unanswered + " unanswered question(s).\nSubmit the exam now?"
                    : "Submit the exam now?";
            if (JOptionPane.showConfirmDialog(this, msg, "Submit exam",
                    JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) {
                return;
            }
        }
        finished = true;
        timer.stop();

        int score = 0;
        for (int i = 0; i < questions.size(); i++) {
            if (questions.get(i).isCorrect(answers[i])) {
                score++;
            }
        }
        student.setScore(score);
        student.setTotal(questions.size());
        system.addResult(student);   // saved in the results ArrayList

        JOptionPane.showMessageDialog(this,
                "Exam submitted!\n\nYour score: " + score + " / " + questions.size(),
                "Result", JOptionPane.INFORMATION_MESSAGE);
        dispose();
        new StudentFrame(system).setVisible(true);
    }
}
