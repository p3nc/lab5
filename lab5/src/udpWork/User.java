package udpWork;

import java.io.Serializable;
import java.net.InetAddress;

public class User implements Serializable {
    private static final long serialVersionUID = 1L;
    private InetAddress address;
    private int port;

    public User(InetAddress address, int port) {
        this.address = address;
        this.port = port;
    }

    public InetAddress getAddress() {
        return address;
    }

    public int getPort() {
        return port;
    }

    @Override
    public String toString() {
        return "Address: " + address.getHostAddress() + ", Port: " + port;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        User user = (User) obj;
        return port == user.port && address.equals(user.address);
    }
}