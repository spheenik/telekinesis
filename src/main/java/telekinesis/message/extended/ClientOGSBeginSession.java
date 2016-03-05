package telekinesis.message.extended;

import io.netty.buffer.ByteBuf;
import telekinesis.model.Encodable;

public class ClientOGSBeginSession implements Encodable {

    private byte accountType;
    private long accountId;
    private int appId;
    private int timeStarted;

    public byte getAccountType() {
        return accountType;
    }

    public void setAccountType(byte accountType) {
        this.accountType = accountType;
    }

    public long getAccountId() {
        return accountId;
    }

    public void setAccountId(long accountId) {
        this.accountId = accountId;
    }

    public int getAppId() {
        return appId;
    }

    public void setAppId(int appId) {
        this.appId = appId;
    }

    public int getTimeStarted() {
        return timeStarted;
    }

    public void setTimeStarted(int timeStarted) {
        this.timeStarted = timeStarted;
    }

    @Override
    public void encode(ByteBuf out) {
        out.writeByte(accountType);
        out.writeLong(accountId);
        out.writeInt(appId);
        out.writeInt(timeStarted);
    }

    @Override
    public String toString() {
        return "ClientOGSBeginSession{" +
                "accountType=" + accountType +
                ", accountId=" + accountId +
                ", appId=" + appId +
                ", timeStarted=" + timeStarted +
                '}';
    }
}


