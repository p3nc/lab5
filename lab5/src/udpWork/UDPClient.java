package udpWork;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.*;

public class UDPClient {
    private ActiveUsers userList = new ActiveUsers();
    private DatagramSocket socket;
    private DatagramPacket packet;
    private int serverPort;
    private InetAddress serverAddress;

    public UDPClient(String address, int port) throws UnknownHostException, SocketException {
        serverAddress = InetAddress.getByName(address);
        serverPort = port;
        socket = new DatagramSocket();
        socket.setSoTimeout(1000);
    }

    public void work(int bufferSize) {
        try {
            sendRegistrationRequest(bufferSize);
            receiveUserList(bufferSize);
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            socket.close();
        }
        System.out.println("Registered users:\n" + userList);
    }

    private void sendRegistrationRequest(int bufferSize) throws IOException {
        byte[] buffer = new byte[bufferSize];
        packet = new DatagramPacket(buffer, buffer.length, serverAddress, serverPort);
        socket.send(packet);
        System.out.println("Registration request sent.");
    }

    private void receiveUserList(int bufferSize) throws IOException, ClassNotFoundException {
        byte[] buffer = new byte[bufferSize];
        while (true) {
            packet = new DatagramPacket(buffer, buffer.length);
            socket.receive(packet);
            if (packet.getLength() == 0) {
                break;
            }
            ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(packet.getData(), 0, packet.getLength()));
            User user = (User) in.readObject();
            userList.add(user);
        }
    }

    public static void main(String[] args) throws UnknownHostException, SocketException, ClassNotFoundException {
        UDPClient client = new UDPClient("localhost", 1501);
        client.work(256);
    }
}