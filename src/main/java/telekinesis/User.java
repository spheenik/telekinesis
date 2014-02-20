package telekinesis;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class User {
    
    private final Logger log = LoggerFactory.getLogger(getClass());
    private final SteamClient client;
    
    public User(SteamClient client) {
        this.client = client;
    }

}
