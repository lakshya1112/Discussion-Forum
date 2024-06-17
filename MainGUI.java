import java.io.IOException;

import javax.swing.*;

public class MainGUI extends JFrame {
    JButton btn1, btn2;

    MainGUI() {
        JLabel label = new JLabel("|| Welcome to the online discussion forum || ");
        label.setBounds(70, 30, 250, 30);
        add(label);
        JLabel label2 = new JLabel(" ||Choose Login or Sign UP to Continue|| ");
        label2.setBounds(75, 50, 250, 30);
        add(label2);

        btn1 = new JButton("Login");
        btn1.setBounds(50, 100, 100, 30);
        btn1.addActionListener(e -> {
            new LoginGUI();
            dispose();
        });

        btn2 = new JButton("Signup");
        btn2.setBounds(200, 100, 100, 30);
        btn2.addActionListener(e -> {
            new SignupGUI();

            dispose();
        });

        add(btn1);
        add(btn2);

        setSize(400, 250);
        setLayout(null);
        setVisible(true);
    }

    public static void main(String[] args) throws IOException{
        new MainGUI();
     //new ChatClient();
        
    }
}
