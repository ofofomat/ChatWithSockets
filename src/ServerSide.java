import java.io.*;
import java.util.*;
import java.net.*;

// Server class
public class ServerSide
{

    // Vector to store active clients
    static Vector<ClientHandler> clientsArray = new Vector<>();

    // counter for clients
    static int i = 0;

    public static void main(String[] args) throws IOException
    {
        // server is listening on port 1147
        ServerSocket serverSocket = new ServerSocket(1147);
        Socket socket;

        // running infinite loop for getting
        // client request
        while (true)
        {
            // Accept the incoming request
            socket = serverSocket.accept();
            InetAddress ipAddress = socket.getInetAddress();

            System.out.println("New client request received : " + ipAddress.getHostName());

            // obtain input and output streams
            DataInputStream read = new DataInputStream(socket.getInputStream());
            DataOutputStream write = new DataOutputStream(socket.getOutputStream());

            System.out.println("Creating a new handler for this client...");

            // Create a new handler object for handling this request.
            ClientHandler matchClient = new ClientHandler(socket,"client " + i, read, write);

            // Create a new Thread with this object.
            Thread clientThread = new Thread(matchClient);

            System.out.println("Adding this client to active clients list");

            // add this client to active clients list
            clientsArray.add(matchClient);

            // start the thread.
            clientThread.start();

            // increment i for new client.
            // i is used for naming only, and can be replaced
            // by any naming scheme
            i++;
        }
    }
}

// ClientHandler class
class ClientHandler implements Runnable
{
    Scanner reader = new Scanner(System.in);
    private String name;
    final DataInputStream read;
    final DataOutputStream write;
    Socket socket;
    boolean isLogged;

    // constructor
    public ClientHandler(Socket socket, String name,
                         DataInputStream read, DataOutputStream write) {
        this.read = read;
        this.write = write;
        this.name = name;
        this.socket = socket;
        this.isLogged=true;
    }

    @Override
    public void run() {

        String text;
        while (true)
        {
            try
            {
                // receive the string
                text = read.readUTF();

                System.out.println(text);

                if(text.equals("logout")){
                    this.isLogged=false;
                    this.socket.close();
                    break;
                }

                // break the string into message and recipient part
                StringTokenizer tokenizer = new StringTokenizer(text, "#");
                String sender = tokenizer.nextToken();
                String receptor = tokenizer.nextToken();

                // search for the recipient in the connected devices list.
                // ar is the vector storing client of active users
                for (ClientHandler mc : ServerSide.clientsArray)
                {
                    // if the recipient is found, write on its
                    // output stream
                    if (mc.name.equals(receptor) && mc.isLogged==true)
                    {
                        mc.write.writeUTF(this.name+"> "+sender);
                        break;
                    }
                }
            }
            catch (IOException exception)
            {
                exception.printStackTrace();
            }

        }
        try
        {
            // closing resources
            this.read.close();
            this.write.close();

        }
        catch(IOException exception)
        {
            exception.printStackTrace();
        }
    }
}

