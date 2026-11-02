import javax.swing.*;
import java.awt.*;
import java.time.*;
import java.util.Date;
public class E13_Xtra1{
    public static void main(String[] a){
        JFrame f=new JFrame();
        f.getContentPane().setBackground(new Color(11,37,43));
        f.setLayout(new GridLayout(6,1,4,4));
        Color tc=new Color(220,240,240);
        JLabel l1=new JLabel("Name:",JLabel.CENTER);
        l1.setForeground(tc);
        JTextField t=new JTextField();
        JLabel l2=new JLabel("DOB YYYY-MM-DD:",JLabel.CENTER);
        l2.setForeground(tc);
        JPanel dp=new JPanel();
        dp.setOpaque(false);
        String[] yr=new String[100];
        for(int i=0;i<100;i++) yr[i]=""+(2026-i);
        String[] mo=new String[12];
        for(int i=0;i<12;i++) mo[i]=""+(i+1);
        String[] dy=new String[31];
        for(int i=0;i<31;i++) dy[i]=""+(i+1);
        JComboBox<String> cy=new JComboBox<>(yr);
        JComboBox<String> cm=new JComboBox<>(mo);
        JComboBox<String> cd=new JComboBox<>(dy);
        dp.add(cy);dp.add(cm);dp.add(cd);
        JButton b=new JButton("Calculate");
        b.setBackground(new Color(24,76,89));
        b.setForeground(Color.WHITE);
        JLabel l=new JLabel("",JLabel.CENTER);
        l.setForeground(tc);
        b.addActionListener(e->{
            int y=Integer.parseInt((String)cy.getSelectedItem());
            int m=Integer.parseInt((String)cm.getSelectedItem());
            int d=Integer.parseInt((String)cd.getSelectedItem());
            LocalDate dob=LocalDate.of(y,m,d);
            l.setText(t.getText()+" is "+Period.between(dob,LocalDate.now()).getYears()+" yrs");
        });
        f.add(l1);f.add(t);f.add(l2);f.add(dp);f.add(b);f.add(l);
        f.setSize(260,260);
        f.setVisible(true);
    }
}