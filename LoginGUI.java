import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.*;

public class LoginGUI extends JFrame implements ActionListener {
    JLabel l1, l2;
    JTextField tf1;
    JPasswordField pf2;
    JButton btn1, btn2,btnBack;

    LoginGUI() {
        btnBack = new JButton("←");
        btnBack.setBounds(5, 20, 100, 30);
        btnBack.addActionListener(this);
        add(btnBack);
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
            if (username.equals("") || password.equals("")) {
                JOptionPane.showMessageDialog(this, "All fields are required");
            } else {
                try {
                    Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/mydb2", "root", "root@123");
                    PreparedStatement st = con.prepareStatement("SELECT * FROM users WHERE username = ? AND password = ?");
                    st.setString(1, username);
                    st.setString(2, password);
                    ResultSet rs = st.executeQuery();
                    if (rs.next()) {
                        JOptionPane.showMessageDialog(this, "Login successful");
                        new WelcomeGUI(username);
                        dispose();
                    } else {
                        JOptionPane.showMessageDialog(this, "Invalid username or password");
                    }
                    con.close();
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
                }
            }
        } else if (e.getSource() == btn2) {
            tf1.setText("");
            pf2.setText("");
        }
        else if(e.getSource() == btnBack) {
            // close the current SignupGUI window and open the main GUI window
            dispose();
            new MainGUI();
        }
    }

    public static void main(String[] args) {
        new LoginGUI();
    }
}

class WelcomeGUI extends JFrame {
    JLabel l;
    JButton btn;

    WelcomeGUI(String username) {
        l = new JLabel("Welcome, " + username + "!");
        l.setBounds(50, 50, 200, 30);
        add(l);
        btn = new JButton("ok");
        btn.setBounds(50, 100, 100, 30);
        btn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    new ChatClient();
                } catch (Exception e2) {
                    // TODO: handle exception
                    e2.printStackTrace();
                }
               dispose();
            }
        });
        add(btn);
        setSize(300, 200);
        setLayout(null);
        setVisible(true);
    }
}
