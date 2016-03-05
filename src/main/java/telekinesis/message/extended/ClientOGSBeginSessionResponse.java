package telekinesis.message.extended;

import io.netty.buffer.ByteBuf;
import telekinesis.model.Decodable;

import java.io.IOException;

public class ClientOGSBeginSessionResponse implements Decodable {

    private int result;
    private boolean collectingAny;
    private boolean collectingDetails;
    private long sessionId;

    public int getResult() {
        return result;
    }

    public void setResult(int result) {
        this.result = result;
    }

    public boolean isCollectingAny() {
        return collectingAny;
    }

    public void setCollectingAny(boolean collectingAny) {
        this.collectingAny = collectingAny;
    }

    public boolean isCollectingDetails() {
        return collectingDetails;
    }

    public void setCollectingDetails(boolean collectingDetails) {
        this.collectingDetails = collectingDetails;
    }

    public long getSessionId() {
        return sessionId;
    }

    public void setSessionId(long sessionId) {
        this.sessionId = sessionId;
    }

    @Override
    public void decode(ByteBuf in) throws IOException {
        result = in.readInt();
        collectingAny = in.readByte() != (byte)0;
        collectingDetails = in.readByte() != (byte)0;
        sessionId = in.readLong();
    }

    @Override
    public String toString() {
        return "ClientOGSBeginSessionResponse{" +
                "result=" + result +
                ", collectingAny=" + collectingAny +
                ", collectingDetails=" + collectingDetails +
                ", sessionId=" + sessionId +
                '}';
    }
}


