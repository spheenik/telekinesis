package telekinesis.model;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.stream.Stream;

public interface SteamClientDelegate {

    String getAccountName();
    String getPassword();

    Stream<Path> findFile(String pattern) throws IOException;
    void writeFile(String fileName, Integer dstOffset, ByteBuffer data, StandardOpenOption... openOptions) throws IOException;
    ByteBuffer readFile(String fileName, Integer dstOffset, Integer length) throws IOException;
    void deleteFile(String fileName) throws IOException;

    byte[] getSentrySha1() throws IOException;

}
