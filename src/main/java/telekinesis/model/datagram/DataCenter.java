package telekinesis.model.datagram;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class DataCenter {

    @JsonProperty("address_ranges")
    private List<AddressRange> addressRanges;
    @JsonProperty("partners")
    private Integer partners;

    public List<AddressRange> getAddressRanges() {
        return addressRanges;
    }

    public Integer getPartners() {
        return partners;
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
