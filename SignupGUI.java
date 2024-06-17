import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.*;

public class SignupGUI extends JFrame implements ActionListener {
    JLabel l1, l2, l3, l4;
    JTextField tf1, tf2;
    JPasswordField pf3, pf4;
    JButton btn1, btn2,btnBack;

    SignupGUI() {
        btnBack = new JButton("←");
        btnBack.setBounds(5, 20, 100, 30);
        btnBack.addActionListener(this);
        add(btnBack);
        l1 = new JLabel("Username:");
        l1.setBounds(50, 70, 100, 30);
        tf1 = new JTextField();
        tf1.setBounds(150, 70, 150, 30);
        l2 = new JLabel("Email:");
        l2.setBounds(50, 120, 100, 30);
        tf2 = new JTextField();
        tf2.setBounds(150, 120, 150, 30);
        l3 = new JLabel("Password:");
        l3.setBounds(50, 170, 100, 30);
        pf3 = new JPasswordField();
        pf3.setBounds(150, 170, 150, 30);
        l4 = new JLabel("Confirm Password:");
        l4.setBounds(50, 220, 150, 30);
        pf4 = new JPasswordField();
        pf4.setBounds(200, 220, 100, 30);
        btn1 = new JButton("Signup");
        btn1.setBounds(50, 280, 100, 30);
        btn1.addActionListener(this);
        btn2 = new JButton("Clear");
        btn2.setBounds(200, 280, 100, 30);
        btn2.addActionListener(this);
        add(l1);
        add(tf1);
        add(l2);
        add(tf2);
        add(l3);
        add(pf3);
        add(l4);
        add(pf4);
        add(btn1);
        add(btn2);
        setSize(400, 400);
        setLayout(null);
        setVisible(true);
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btn1) {
            String username = tf1.getText();
            String email = tf2.getText();
            String password = new String(pf3.getPassword());
            String confirmPassword = new String(pf4.getPassword());
            if (username.equals("") || email.equals("") || password.equals("") || confirmPassword.equals("")) {
                JOptionPane.showMessageDialog(this, "All fields are required");
            } else if (!password.equals(confirmPassword)) {
                JOptionPane.showMessageDialog(this, "Passwords do not match");
            } 
            else if(!email.endsWith("@gmail.com")){
                JOptionPane.showMessageDialog(this, "The email ID entered is invalid please type again..");

            }
            else {
                try {
                    Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/mydb2", "root", "root@123");
                    PreparedStatement st = con.prepareStatement("SELECT * FROM users WHERE username = ?");
                    st.setString(1, username);
                    ResultSet rs = st.executeQuery();
                    if (rs.next()) {
                        JOptionPane.showMessageDialog(this, "Username already exists");
                    } else {
                        st = con.prepareStatement("INSERT INTO users (username, email, password) VALUES (?, ?, ?)");
                        st.setString(1, username);
                        st.setString(2, email);
                        st.setString(3, password);
                        st.executeUpdate();
                        JOptionPane.showMessageDialog(this, "Signup successful");
                    }
                    con.close();
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
                }
            }
        } else if (e.getSource() == btn2) {
            tf1.setText("");
            tf2.setText("");
            pf3.setText("");
            pf4.setText("");
        }
        else if (e.getSource() == btnBack) {
            // close the current SignupGUI window and open the main GUI window
            dispose();
            new MainGUI();
        }
    }

    public static void main(String[] args) {
        new SignupGUI();
    }
}
