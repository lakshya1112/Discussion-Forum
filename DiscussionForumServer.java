import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.sql.*;

public class DiscussionForumServer extends JFrame implements ActionListener {
    private JTextArea textArea;
    private JButton startButton;
    private JButton stopButton;
    private ServerSocket serverSocket;
    private boolean serverRunning;
    private Connection dbConnection;

    public DiscussionForumServer() {
        super("Discussion Forum Server");

        // Initialize GUI components
        textArea = new JTextArea(20, 40);
        textArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(textArea);
        startButton = new JButton("Start");
        startButton.addActionListener(this);
        stopButton = new JButton("Stop");
        stopButton.addActionListener(this);
        stopButton.setEnabled(false);

        // Add GUI components to frame
        JPanel panel = new JPanel();
        panel.add(startButton);
        panel.add(stopButton);
        add(panel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        // Initialize database connection
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            dbConnection = DriverManager.getConnection("jdbc:mysql://localhost/discussion_forum", "username", "password");
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
            System.exit(1);
        }

        // Configure frame settings
        setSize(500, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == startButton) {
            startButton.setEnabled(false);
            stopButton.setEnabled(true);
            serverRunning = true;

            // Start listening for connections in a separate thread
            new Thread(() -> {
                try {
                    serverSocket = new ServerSocket(12345);
                    textArea.append("Server started on port 12345\n");
                    while (serverRunning) {
                        Socket clientSocket = serverSocket.accept();
                        textArea.append("Client connected: " + clientSocket.getInetAddress().getHostAddress() + "\n");

                        // Handle client connection in a separate thread
                        new Thread(() -> {
                            try {
                                BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                                String username = in.readLine();
                                while (serverRunning) {
                                    String message = in.readLine();
                                    if (message == null) {
                                        break;
                                    }
                                    textArea.append(username + ": " + message + "\n");

                                    // Save message to database
                                    try {
                                        PreparedStatement statement = dbConnection.prepareStatement("INSERT INTO messages (username, message) VALUES (?, ?)");
                                        statement.setString(1, username);
                                        statement.setString(2, message);
                                        statement.executeUpdate();
                                    } catch (SQLException ex) {
                                        ex.printStackTrace();
                                    }

                                    // Broadcast message to all connected clients
                                    for (Thread thread : Thread.getAllStackTraces().keySet()) {
                                        if (thread.getName().startsWith("clientThread")) {
                                            ClientThread clientThread = (ClientThread)thread;
                                            clientThread.sendMessage(username + ": " + message);
                                        }
                                    }
                                }
                                textArea.append("Client disconnected: " + clientSocket.getInetAddress().getHostAddress() + "\n");
                                clientSocket.close();
                            } catch (IOException ex) {
                                ex.printStackTrace();
                            }
                        }, "clientThread-" + clientSocket.getInetAddress().getHostAddress()).start();
                    }
                    serverSocket.close();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }).start();
        } else if (e.getSource() == stopButton) {
            startButton.setEnabled(true);
            stopButton.setEnabled(false);
            serverRunning = false;
            try {
            serverSocket.close();
            } catch (IOException ex) {
            ex.printStackTrace();
            }
            }
            }
    
            public static void main(String[] args) {
                new DiscussionForumServer();
            }
            
            private class ClientThread {
                private Socket clientSocket;
                private BufferedWriter out;
            
                public ClientThread(Socket clientSocket) {
                    this.clientSocket = clientSocket;
                    try {
                        out = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            
                public void sendMessage(String message) {
                    try {
                        out.write(message);
                        out.newLine();
                        out.flush();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            }