package telekinesis.message.simple;

import io.netty.buffer.ByteBuf;
import telekinesis.model.Decodable;
import telekinesis.model.steam.EResult;

import java.io.IOException;

public class ChannelEncryptResult implements Decodable {

    private EResult result = EResult.Invalid;

    public EResult getResult() {
        return result;
    }

    public void setResult(EResult result) {
        this.result = result;
    }

    @Override
    public void decode(ByteBuf in) throws IOException {
        result = EResult.f(in.readInt());
    }

}
