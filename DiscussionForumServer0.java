import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DiscussionForumServer0 {
    private static final int PORT = 12345;
    private static List<ObjectOutputStream> clientOutputStreams;

    public static void main(String[] args) {
        clientOutputStreams = new ArrayList<>();
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            while (true) {
                Socket clientSocket = serverSocket.accept();
                ObjectOutputStream outputStream = new ObjectOutputStream(clientSocket.getOutputStream());
                clientOutputStreams.add(outputStream);
                new Thread(new ClientHandler(clientSocket)).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static class ClientHandler implements Runnable {
        private Socket clientSocket;
        private ObjectInputStream inputStream;

        public ClientHandler(Socket clientSocket) {
            this.clientSocket = clientSocket;
            try {
                inputStream = new ObjectInputStream(clientSocket.getInputStream());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void run() {
            try {
                while (true) {
                    String message = (String) inputStream.readObject();
                    saveMessageToDatabase(message);
                    notifyClients(message);
                }
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }

        private void saveMessageToDatabase(String message) {
            try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/discussion_forum", "username", "password")) {
                PreparedStatement statement = connection.prepareStatement("INSERT INTO messages (message) VALUES (?)");
                statement.setString(1, message);
                statement.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        private void notifyClients(String message) {
            for (ObjectOutputStream outputStream : clientOutputStreams) {
                try {
                    outputStream.writeObject(message);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
