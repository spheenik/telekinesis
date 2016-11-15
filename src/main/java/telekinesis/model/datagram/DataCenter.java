package telekinesis.model.datagram;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class DataCenter {

    @JsonProperty("address_ranges")
    private List<AddressRange> addressRanges;

    public List<AddressRange> getAddressRanges() {
        return addressRanges;
    }

    public boolean containsAddress(int ip) {
        for (AddressRange addressRange : addressRanges) {
            if (addressRange.containsAddress(ip)) {
                return true;
            }
        }
        return false;
    }
}
