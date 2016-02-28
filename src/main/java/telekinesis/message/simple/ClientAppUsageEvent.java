package telekinesis.message.simple;

import io.netty.buffer.ByteBuf;
import telekinesis.model.Encodable;

import java.io.IOException;

public class ClientAppUsageEvent implements Encodable {

    private int usageEvent;
    private long gameId;
    private short offline;

    public int getUsageEvent() {
        return usageEvent;
    }

    public void setUsageEvent(int usageEvent) {
        this.usageEvent = usageEvent;
    }

    public long getGameId() {
        return gameId;
    }

    public void setGameId(long gameId) {
        this.gameId = gameId;
    }

    public short getOffline() {
        return offline;
    }

    public void setOffline(short offline) {
        this.offline = offline;
    }

    @Override
    public void encode(ByteBuf out) throws IOException {
        out.writeInt(usageEvent);
        out.writeLong(gameId);
        out.writeShort(offline);
    }
}
