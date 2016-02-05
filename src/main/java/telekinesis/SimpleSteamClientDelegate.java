package telekinesis;

import com.google.protobuf.ByteString;
import telekinesis.model.SteamClientDelegate;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.file.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Optional;
import java.util.Properties;

class SimpleSteamClientDelegate implements SteamClientDelegate {

    private final Path directory;
    private final Properties p = new Properties();

    public SimpleSteamClientDelegate(String directory) throws IOException {
        this.directory = Paths.get(directory);
        loadProperties();
    }

    private Path getPropertyFile() {
        return directory.resolve("credentials.properties");
    }

    private Optional<Path> getSentryFile() throws IOException {
        PathMatcher pathMatcher = FileSystems.getDefault().getPathMatcher("glob:ssfn*");
        return Files.find(directory, 1, (p, a) -> pathMatcher.matches(p.getFileName())).findFirst();
    }

    public void loadProperties() throws IOException {
        BufferedReader reader = Files.newBufferedReader(getPropertyFile());
        p.load(reader);
        reader.close();
    }

    @Override
    public String getAccountName() {
        return p.getProperty("user");
    }

    @Override
    public String getPassword() {
        return p.getProperty("pass");
    }

    @Override
    public void writeSentry(String fileName, int dstOffset, ByteString src) throws IOException {
        Path sentryFile = directory.resolve(fileName);
        try (FileChannel fc = (FileChannel.open(sentryFile, StandardOpenOption.READ, StandardOpenOption.WRITE))) {
            fc.position(dstOffset);
            fc.write(src.asReadOnlyByteBuffer());
            fc.close();
        }
    }

    @Override
    public byte[] getSentrySha1() throws IOException {
        Optional<Path> sentryFile = getSentryFile();
        if (sentryFile.isPresent()) {
            try {
                MessageDigest md = MessageDigest.getInstance("SHA-1");
                return md.digest(Files.readAllBytes(sentryFile.get()));
            } catch (NoSuchAlgorithmException e) {
                throw new IOException(e);
            }
        }
        return null;
    }

}
