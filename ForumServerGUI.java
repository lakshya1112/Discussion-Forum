import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

public class ForumServerGUI extends JFrame {
    private ServerSocket serverSocket;
    private List<ForumServerThread> clients = new ArrayList<ForumServerThread>();
    private DefaultListModel<String> model = new DefaultListModel<String>();
    private JList<String> list = new JList<String>(model);
    private JTextField textField = new JTextField(20);
    private JButton sendButton = new JButton("Send");
    private Connection connection;
    private PreparedStatement insertStatement;
    
    public ForumServerGUI(int port, String dbUrl, String dbUser, String dbPassword) {
        try {
            serverSocket = new ServerSocket(port);
            System.out.println("Server started on port " + port);
            
            connection = DriverManager.getConnection(dbUrl, dbUser, dbPassword);
            insertStatement = connection.prepareStatement("INSERT INTO posts (text) VALUES (?)");
            
            JPanel inputPanel = new JPanel();
            inputPanel.add(textField);
            inputPanel.add(sendButton);
            
            add(new JScrollPane(list), BorderLayout.CENTER);
            add(inputPanel, BorderLayout.SOUTH);
            
            sendButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    String text = textField.getText();
                    broadcast(text);
                    savePost(text);
                    textField.setText("");
                }
            });
            
            pack();
            setVisible(true);
            
            while (true) {
                Socket socket = serverSocket.accept();
                ForumServerThread thread = new ForumServerThread(this, socket);
                clients.add(thread);
                thread.start();
            }
        } catch (IOException | SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                serverSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    
    public void broadcast(String message) {
        for (ForumServerThread thread : clients) {
            thread.send(message);
        }
        model.addElement(message);
    }
    
    public void removeClient(ForumServerThread thread) {
        clients.remove(thread);
    }
    private void savePostToDatabase(String username, String message) {
        try {
            Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            Statement stmt = conn.createStatement();
            String sql = "INSERT INTO posts (username, message) VALUES ('" + username + "', '" + message + "')";
            stmt.executeUpdate(sql);
            stmt.close();
            conn.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
    
    public void savePost(String text) {
        try {
            insertStatement.setString(1, text);
            insertStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    public static void main(String[] args) {
        String dbUrl = "jdbc:mysql://localhost/forum";
        String dbUser = "root";
        String dbPassword = "password";
        int port = 8080;
        new ForumServerGUI(port, dbUrl, dbUser, dbPassword);
    }
}
