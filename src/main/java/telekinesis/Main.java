package telekinesis;

import java.io.IOException;
import java.util.Properties;

import telekinesis.connection.Connection;
import telekinesis.connection.Util;
import telekinesis.event.Event;
import telekinesis.message.proto.ClientLogon;
import telekinesis.model.EResult;

import com.google.protobuf.ByteString;


public class Main {

    public static void main(String[] args) {

        final SteamClient client = new SteamClient();
        
        Event.register(client, SteamClient.POST_CONSTRUCT.class, new SteamClient.POST_CONSTRUCT() {
            @Override
            public void handle() throws IOException {
                client.connect();
            }
        });

        Event.register(client, SteamClient.PRE_DESTROY.class, new SteamClient.PRE_DESTROY() {
            @Override
            public void handle() throws IOException {
                client.disconnect();
            }
        });
        
        
        Event.register(client, Connection.CONNECTION_ESTABLISHED.class, new Connection.CONNECTION_ESTABLISHED() {
            @Override
            public void handle() throws IOException {
                ClientLogon lm = new ClientLogon();

                // lm.getBody().setObfustucatedPrivateIp(ByteBuffer.wrap(((InetSocketAddress)
                // channel.getLocalAddress()).getAddress().getAddress()).getInt()
                // & 0xBAADF00D);

                Properties p = new Properties();
                try {
                    p.load(getClass().getResourceAsStream("/credentials.properties"));
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                lm.getBody().setAccountName(p.getProperty("user"));
                lm.getBody().setPassword(p.getProperty("pass"));
                String authCode = p.getProperty("authCode");
                if (authCode != null) {
                    lm.getBody().setAuthCode(authCode);
                }
                String sentry = p.getProperty("sentry");
                if (sentry != null) {
                    lm.getBody().setShaSentryfile(ByteString.copyFrom(Util.restoreSHA1(sentry)));
                    lm.getBody().setEresultSentryfile(EResult.OK.v());
                } else {
                    lm.getBody().setEresultSentryfile(EResult.FileNotFound.v());
                }
                lm.getBody().setProtocolVersion(65575);

                // log.info("{}", lm.getHeader().build());
                // log.info("{}", lm.getBody().build());
                //
                // ByteBuffer msgBuf = getNewBuffer();
                // msgBuf.position(4);
                // msgBuf.putInt(MAGIC);
                // //plainTextCodec.toWire(lm, msgBuf);
                // encryptionCodec.toWire(lm, msgBuf);
                // msgBuf.putInt(0, msgBuf.position() - 8);
                // msgBuf.flip();
                // log.info("sending data: {}",
                // Util.convertByteBufferToString(msgBuf, msgBuf.limit()));

                client.send(lm);                
                
            }
        });
        
        
        
        client.run();
    }
}
