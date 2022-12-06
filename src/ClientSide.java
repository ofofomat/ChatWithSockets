import java.io.*;
import java.net.*;
import java.util.Scanner;

public class ClientSide
{
    final static int ServerPort = 1147;

    public static void main(String args[]) throws UnknownHostException, IOException
    {
        Scanner reader = new Scanner(System.in);

        // getting localhost ip
        InetAddress ipClient = InetAddress.getByName("localhost");

        // establish the connection
        Socket socket = new Socket(ipClient, ServerPort);

        // obtaining input and out streams
        DataInputStream read = new DataInputStream(socket.getInputStream());
        DataOutputStream write = new DataOutputStream(socket.getOutputStream());

        // sendMessage thread
        Thread sendMessage = new Thread(new Runnable()
        {
            @Override
            public void run() {
                while (true)
                {

                    // read the message to deliver.
                    String message = reader.nextLine();

                    try {
                        // write on the output stream
                        write.writeUTF(message);
                    }
                    catch (IOException exception)
                    {
                        exception.printStackTrace();
                    }
                }
            }
        });

        // readMessage thread
        Thread readMessage = new Thread(new Runnable()
        {
            @Override
            public void run() {

                while (true)
                {
                    try {
                        // read the message sent to this client
                        String message = read.readUTF();
                        System.out.println(message);
                    }
                    catch (IOException exception)
                    {

                        exception.printStackTrace();
                    }
                }
            }
        });

        sendMessage.start();
        readMessage.start();

    }
}