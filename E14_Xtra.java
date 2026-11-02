import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.util.*;
public class E14_Xtra {
    public static void main(String[] a) {
        JFrame f = new JFrame("Table");
        f.setSize(400, 300);
        Color bg = new Color(11, 37, 43), fg = Color.WHITE;
        ArrayList<String> arr = new ArrayList<>();
        JPanel p = new JPanel();
        p.setBackground(bg);
        JLabel lbl = new JLabel("Add:");
        lbl.setForeground(fg);
        JTextField tf = new JTextField(10);
        JButton btn = new JButton("OK");
        p.add(lbl); p.add(tf); p.add(btn);
        DefaultTableModel tm = new DefaultTableModel(new String[]{"ID", "Val"}, 0);
        JTable t = new JTable(tm);
        t.setBackground(bg);
        t.setForeground(fg);
        JScrollPane sp = new JScrollPane(t);
        sp.getViewport().setBackground(bg);
        btn.addActionListener(e -> {
            String s = tf.getText().trim();
            if (!s.isEmpty()) {
                arr.add(s);
                tm.addRow(new Object[]{arr.size(), s});
                tf.setText("");
            }
        });
        f.add(p, BorderLayout.NORTH);
        f.add(sp);
        f.setVisible(true);
    }
}