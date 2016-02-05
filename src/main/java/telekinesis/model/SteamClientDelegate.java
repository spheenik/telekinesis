package telekinesis.model;

import com.google.protobuf.ByteString;

import java.io.IOException;

public interface SteamClientDelegate {

    String getAccountName();
    String getPassword();

    void writeSentry(String fileName, int dstOffset, ByteString src) throws IOException;

    byte[] getSentrySha1() throws IOException;

}
