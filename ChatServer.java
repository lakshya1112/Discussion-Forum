import java.io.*;
import java.net.*;
import java.util.*;
import java.sql.*;


public class ChatServer {
    private ArrayList<PrintWriter> clients = new ArrayList<>();

    public static void main(String[] args) {
        new ChatServer().startServer(8080);
    }

    public void startServer(int port) {
        try {
            ServerSocket serverSocket = new ServerSocket(port);
            while (true) {
                Socket clientSocket = serverSocket.accept();
                PrintWriter writer = new PrintWriter(clientSocket.getOutputStream(), true);
                clients.add(writer);
                Thread t = new Thread(new ClientHandler(clientSocket));
                t.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private class ClientHandler implements Runnable {
        private BufferedReader reader;
        private String clientName;
    
        public ClientHandler(Socket socket) {
           palce: try {
            int k=0;
                reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                // ask the client for its name
                PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
                previouschatname(socket);

                while(k==0){
                 writer.println("Enter your Username for verification:");
                clientName = reader.readLine();

                Connection conn3 = DriverManager.getConnection("jdbc:mysql://localhost:3306/mydb2", "root", "rootpass@272");
                PreparedStatement st = conn3.prepareStatement("SELECT * FROM users WHERE username =?");
                st.setString(1, clientName);
                ResultSet rs = st.executeQuery();
                if (rs.next()) {
                    
                    // send a welcome message to all clients
                for (PrintWriter client : clients) {
                    client.println(clientName + " has joined the forum chat");
                    k++;

                    
                
                }
             } else{

                    writer.println("Wrong Username entered");
                    k=0;
                
                }
            }
            
                // add the client name to the MySQL schema table
                insertClientName(clientName);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    
        public void previouschatname(Socket socket){
            String name,message;

            try {
                Connection conn2 = DriverManager.getConnection("jdbc:mysql://localhost:3306/chatdb", "root", "rootpass@272");
                PreparedStatement st = conn2.prepareStatement("SELECT * FROM messages");
                ResultSet rs = st.executeQuery();

                try {
                    while (rs.next()){
                        reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                // ask the client for its name
                PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
                        name=rs.getString("client_name");
                        writer.println(name+":");
                        message=rs.getString("message");
                        writer.println(message+"\n");
                    }
                } catch (Exception e) {
                    // TODO: handle exception
                    e.printStackTrace();
                }

            } catch (SQLException e) {
                // TODO: handle exception

                e.printStackTrace();
            }

        }
        public void run(){
            String message;
            try {
                while ((message = reader.readLine()) != null) {
                    System.out.println("Received message: " + message);
                    // send the message to all clients
                    for (PrintWriter client : clients) {
                        client.println(clientName + ": " + message);
                    }
                    // insert the message into the MySQL schema table
                    insertMessage(clientName, message);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            // remove the client from the clients list and close the reader
            clients.remove(this);
            try {
                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            // send a goodbye message to all clients
            for (PrintWriter client : clients) {
                client.println(clientName + " has left the forum chat");
            }
        }
    
        private void insertClientName(String name) {
            try {
                // create a connection to the MySQL database
                    Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/chatdb", "root", "rootpass@272");
                    PreparedStatement st = conn.prepareStatement("SELECT * FROM clients WHERE name = ?");
                        st.setString(1, name);
                        ResultSet rs = st.executeQuery();
                    if (rs.next()) {
                    
                    }
                else{
                // create a statement to insert the client name into the table
                String sql = "INSERT INTO clients (name) VALUES (?)";
                PreparedStatement stmt = conn.prepareStatement(sql);
                stmt.setString(1, name);
                stmt.executeUpdate();
                // close the statement and connection
                stmt.close();
                conn.close();}
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    
        private void insertMessage(String clientName, String message) {
            try {
                // create a connection to the MySQL database
                Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/chatdb", "root", "rootpass@272");
                // create a statement to insert the message and client name into the table
                String sql = "INSERT INTO messages (client_name, message) VALUES (?, ?)";
                PreparedStatement stmt = conn.prepareStatement(sql);
                stmt.setString(1, clientName);
                stmt.setString(2, message);
                stmt.executeUpdate();
                // close the statement and connection
                stmt.close();
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
    