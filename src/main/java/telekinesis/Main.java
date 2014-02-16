package telekinesis;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;

import telekinesis.connection.Connection;

public class Main {

    public static void main(String[] args) throws UnknownHostException, IOException, InterruptedException {
        //Connection c = new Connection(new InetSocketAddress("schrodt.org", 22));
        Connection c = new Connection(new InetSocketAddress("146.66.152.12", 27017));
        c.connect();
        Thread.sleep(10000);
        c.disconnect();
    }
}
