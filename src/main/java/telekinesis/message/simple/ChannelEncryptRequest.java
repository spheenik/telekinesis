package telekinesis.message.simple;

import io.netty.buffer.ByteBuf;
import telekinesis.model.Decodable;
import telekinesis.model.steam.EUniverse;

public class ChannelEncryptRequest implements Decodable {

    private int protocolVersion = 1;
    private EUniverse universe;

    public int getProtocolVersion() {
        return protocolVersion;
    }

    public void setProtocolVersion(int protocolVersion) {
        this.protocolVersion = protocolVersion;
    }

    public EUniverse getUniverse() {
        return universe;
    }

    public void setUniverse(EUniverse universe) {
        this.universe = universe;
    }

    @Override
    public void decode(ByteBuf in) {
        protocolVersion = in.readInt();
        universe = EUniverse.f(in.readInt());
    }

}
