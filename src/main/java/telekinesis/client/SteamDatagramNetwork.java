package telekinesis.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.channel.EventLoop;
import io.netty.util.concurrent.ScheduledFuture;
import org.slf4j.Logger;
import telekinesis.logger.PrintfLoggerFactory;
import telekinesis.model.SteamClientDelegate;
import telekinesis.model.datagram.DataCenter;
import telekinesis.model.datagram.NetworkConfig;
import telekinesis.model.datagram.RoutingCluster;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

public class SteamDatagramNetwork {

    private static final Logger log = PrintfLoggerFactory.getLogger("steam.sdr");

    private static final String configFile = "network_config.json";
    private static final ObjectMapper mapper = new ObjectMapper();

    private final SteamClientDelegate delegate;
    private final EventLoop eventLoop;
    private NetworkConfig config = new NetworkConfig();
    private ScheduledFuture<?> fetchFuture;

    public SteamDatagramNetwork(EventLoop eventLoop, SteamClientDelegate delegate) {
        this.eventLoop = eventLoop;
        this.delegate = delegate;
        readConfigFromCache();
    }

    public int getConfigVersion() {
        return config.getRevision();
    }

    public Map<String, RoutingCluster> getRoutingClusters() {
        return Collections.unmodifiableMap(config.getRoutingClusters());
    }

    public String findDataCenterForServerNetId(long serverNetId) {
        int ip = (int)(serverNetId >> 16);
        for (Map.Entry<String, DataCenter> dce : config.getDataCenters().entrySet()) {
            if (dce.getValue().containsAddress(ip)) {
                return dce.getKey();
            }
        }
        return null;
    }

    public void connect() {
        fetchFuture = eventLoop.schedule(this::readConfigFromWeb, 0L, TimeUnit.SECONDS);
    }

    public void disconnect() {
        fetchFuture.cancel(false);
        fetchFuture = null;
    }

    private void readConfigFromCache() {
        try {
            Optional<Path> fileOption = delegate.findFile(configFile).findFirst();
            if (fileOption.isPresent()) {
                ByteBuffer buf = delegate.readFile(configFile, null, null);
                config = mapper.readValue(Charset.forName("UTF-8").decode(buf).toString(), NetworkConfig.class);
            }
        } catch (IOException er) {
            log.error("reading config from cache failed", er);
            try {
                delegate.deleteFile(configFile);
            } catch (IOException ed) {
                log.error("deleting cached config failed", ed);
            }
        }
    }

    private void readConfigFromWeb() {
        try {
            URL url = new URL("http://media.steampowered.com/apps/sdr/network_config.json");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            int status = connection.getResponseCode();
            for (Map.Entry<String, List<String>> entry : connection.getHeaderFields().entrySet()) {
                log.debug("%s: %s", entry.getKey(), entry.getValue());
            }
            int retrySeconds;
            if (status == 200) {
                config = mapper.readValue(connection.getInputStream(), NetworkConfig.class);
                delegate.writeFile(configFile, 0, ByteBuffer.wrap(mapper.writeValueAsString(config).getBytes("UTF-8")), StandardOpenOption.TRUNCATE_EXISTING);
                retrySeconds = 600;
            } else {
                log.warn("querying steam datagram relay config returned unexpected status %d", status);
                retrySeconds = 60;
            }
            log.info("next fetch of steam datagram relay config in %d seconds.", retrySeconds);
            fetchFuture = eventLoop.schedule(this::readConfigFromWeb, retrySeconds, TimeUnit.SECONDS);
        } catch (Exception e) {
            log.info("getting network_config.json from steam failed: %s", e.getMessage());
            fetchFuture = eventLoop.schedule(this::readConfigFromWeb, 60, TimeUnit.SECONDS);
        }
    }
    
    public static int stringToIntId(String value) {
        int r = 0;
        for (char c : value.toCharArray()) {
            r = (r << 8) | (c & 0xFF);
        }
        return r;
    }

    public static String intToStringId(int value) {
        String result = "";
        while (value != 0) {
            result = ((char)(value & 0xFF)) + result;
            value >>>= 8;
        }
        return result;
    }

    public static void main(String[] args) {
        System.out.println(stringToIntId("vie"));
        System.out.println(intToStringId(7760229));
    }

}

