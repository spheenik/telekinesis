package telekinesis.model.datagram;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.Random;

public class RoutingCluster {

    private static final Random RANDOM = new Random();

    @JsonProperty("addresses")
    private List<SocketRange> addresses;
    @JsonProperty("ping_only")
    private boolean pingOnly;
    @JsonProperty("partners")
    private Integer partners;

    public List<SocketRange> getAddresses() {
        return addresses;
    }

    public boolean isPingOnly() {
        return pingOnly;
    }

    public Integer getPartners() {
        return partners;
    }

    @JsonIgnore
    public InetSocketAddress getRandomAddress() {
        int n = 0;
        for (SocketRange address : addresses) {
            n += address.getAddressCount();
        }
        int c = RANDOM.nextInt(n);
        for (SocketRange address : addresses) {
            if (c >= address.getAddressCount()) {
                c -= address.getAddressCount();
                continue;
            }
            return new InetSocketAddress(
                    address.getAddress(),
                    address.getPortFrom() + c
            );
        }
        throw new RuntimeException("Failed to get a random address from routing cluster");
    }

}
