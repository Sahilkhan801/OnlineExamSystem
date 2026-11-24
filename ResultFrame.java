import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;

/**
 * ResultFrame: shows the rank list.
 * The list comes from ExamSystem.getRankList(), which uses Bubble Sort.
 */
public class ResultFrame extends JFrame {

    public ResultFrame(ExamSystem system) {
        setTitle("Rank List");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);   // closes only this window
        setSize(650, 450);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        JLabel title = new JLabel("Rank List (highest score first)");
        title.setFont(new Font("SansSerif", Font.BOLD, 20));
        title.setBorder(BorderFactory.createEmptyBorder(12, 15, 0, 15));
        add(title, BorderLayout.NORTH);

        String[] columns = {"Rank", "Student ID", "Name", "Score", "Percentage"};
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        ArrayList<Student> ranked = system.getRankList();   // sorted by Bubble Sort
        int rank = 0;
        for (int i = 0; i < ranked.size(); i++) {
            Student s = ranked.get(i);
            // students with the same score share the same rank
            if (i == 0 || s.getScore() != ranked.get(i - 1).getScore()) {
                rank = i + 1;
            }
            int percent = s.getTotal() == 0 ? 0 : Math.round(s.getScore() * 100f / s.getTotal());
            model.addRow(new Object[]{rank, s.getId(), s.getName(),
                    s.getScore() + " / " + s.getTotal(), percent + "%"});
        }

        JTable table = new JTable(model);
        table.setRowHeight(26);
        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createEmptyBorder(0, 15, 0, 15));
        add(scroll, BorderLayout.CENTER);

        JLabel note = new JLabel(ranked.isEmpty() ? "No student has finished the exam yet."
                : "Total students ranked: " + ranked.size());
        JButton closeBtn = new JButton("Close");
        closeBtn.addActionListener(e -> dispose());

        JPanel bottom = new JPanel(new BorderLayout());
        bottom.setBorder(BorderFactory.createEmptyBorder(0, 15, 12, 15));
        bottom.add(note, BorderLayout.WEST);
        bottom.add(closeBtn, BorderLayout.EAST);
        add(bottom, BorderLayout.SOUTH);
    }
}
