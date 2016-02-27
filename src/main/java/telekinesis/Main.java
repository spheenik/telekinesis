package telekinesis;

import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import telekinesis.client.SteamClient;
import telekinesis.model.SteamClientDelegate;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class Main {

    private static final Logger log = LoggerFactory.getLogger("main");

    public static void main(String[] args) {
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            SteamClientDelegate credentials = new SimpleSteamClientDelegate("run/client-1");
            SteamClient steamClient = new SteamClient(workerGroup, "client-1", credentials);
            steamClient.connect();
            workerGroup.schedule(steamClient::disconnect, 30, TimeUnit.MINUTES);
            while(steamClient.isConnectionAlive()) {
                Thread.sleep(100L);
            }
        } catch (InterruptedException e) {
            // fallthrough
        } catch (IOException e) {
            log.error("failed to init credentials", e);
        } finally {
            workerGroup.shutdownGracefully();
        }
    }

}
