import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import javax.swing.*;

public class LoginGUI2 extends JFrame implements ActionListener {
    JLabel l1, l2;
    JTextField tf1;
    JPasswordField pf2;
    JButton btn1, btn2;

    LoginGUI2() {
        l1 = new JLabel("Username:");
        l1.setBounds(50, 70, 100, 30);
        tf1 = new JTextField();
        tf1.setBounds(150, 70, 150, 30);
        l2 = new JLabel("Password:");
        l2.setBounds(50, 120, 100, 30);
        pf2 = new JPasswordField();
        pf2.setBounds(150, 120, 150, 30);
        btn1 = new JButton("Login");
        btn1.setBounds(50, 180, 100, 30);
        btn1.addActionListener(this);
        btn2 = new JButton("Clear");
        btn2.setBounds(200, 180, 100, 30);
        btn2.addActionListener(this);
        add(l1);
        add(tf1);
        add(l2);
        add(pf2);
        add(btn1);
        add(btn2);
        setSize(400, 300);
        setLayout(null);
        setVisible(true);
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btn1) {
            String username = tf1.getText();
            String password = new String(pf2.getPassword());
            try {
                Class.forName("com.mysql.jdbc.Driver");
                Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/mydatabase", "username", "password");
                PreparedStatement ps = con.prepareStatement("SELECT * FROM users WHERE username=? AND password=?");
                ps.setString(1, username);
                ps.setString(2, password);
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    JOptionPane.showMessageDialog(this, "Login successful");
                } else {
                    JOptionPane.showMessageDialog(this, "Invalid username or password");
                }
                con.close();
            } catch (Exception ex) {
                System.out.println(ex);
            }
        } else if (e.getSource() == btn2) {
            tf1.setText("");
            pf2.setText("");
        }
    }

    public static void main(String[] args) {
        new LoginGUI2();
    }
}
