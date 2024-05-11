package udpWork;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.*;

public class UDPServer {
    private ActiveUsers userList = new ActiveUsers();
    private DatagramSocket socket;
    private DatagramPacket packet;
    private InetAddress address;
    private int port;

    public UDPServer(int serverPort) throws SocketException {
        socket = new DatagramSocket(serverPort);
        System.out.println("Server started on port " + serverPort);
    }

    public void work(int bufferSize) {
        try {
            while (true) {
                receiveUserData(bufferSize);
                System.out.println("Request from: " + address.getHostAddress() + ":" + port);
                sendUserData();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            socket.close();
        }
    }

    private void receiveUserData(int bufferSize) throws IOException {
        byte[] buffer = new byte[bufferSize];
        packet = new DatagramPacket(buffer, buffer.length);
        socket.receive(packet);

        address = packet.getAddress();
        port = packet.getPort();

        User user = new User(address, port);
        if (!userList.contains(user)) {
            userList.add(user);
        }
    }

    private void sendUserData() throws IOException {
        byte[] buffer;
        for (int i = 0; i < userList.size(); i++) {
            ByteArrayOutputStream bout = new ByteArrayOutputStream();
            ObjectOutputStream out = new ObjectOutputStream(bout);
            out.writeObject(userList.get(i));
            buffer = bout.toByteArray();

            packet = new DatagramPacket(buffer, buffer.length, address, port);
            socket.send(packet);
        }

        // Send empty packet to signal the end of the list
        buffer = new byte[0];
        packet = new DatagramPacket(buffer, buffer.length, address, port);
        socket.send(packet);
    }

    public static void main(String[] args) throws SocketException {
        UDPServer server = new UDPServer(1501);
        server.work(256);
    }
}