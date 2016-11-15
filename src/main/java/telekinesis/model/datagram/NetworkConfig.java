package telekinesis.model.datagram;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class NetworkConfig {

    @JsonProperty("revision")
    private int revision;
    @JsonProperty("lat_lon")
    private Map<String, LatLong> latLon = new HashMap<>();
    @JsonProperty("data_centers")
    private Map<String, DataCenter> dataCenters = new HashMap<>();
    @JsonProperty("routing_clusters")
    private Map<String, RoutingCluster> routingClusters = new HashMap<>();

    public int getRevision() {
        return revision;
    }

    public Map<String, LatLong> getLatLon() {
        return latLon;
    }

    public Map<String, DataCenter> getDataCenters() {
        return dataCenters;
    }

    public Map<String, RoutingCluster> getRoutingClusters() {
        return routingClusters;
    }

    public static void main(String[] args) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        NetworkConfig config = mapper.readValue(NetworkConfig.class.getResourceAsStream("/network_config.json"), NetworkConfig.class);
        System.out.println(config);

        LatLong ostfildern = new LatLong(48.72, 9.25);
        for (Map.Entry<String, LatLong> e : config.getLatLon().entrySet()) {
            System.out.printf("%s -> %f\n", e.getKey(), e.getValue().distance(ostfildern));
        }
    }
}
