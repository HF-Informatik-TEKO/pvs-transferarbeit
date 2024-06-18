package Shared;

public class AppSettings {
    public final String rpcName;
    public final String ipAddress;
    public final int port;
    public final int serverMaxMessageHistory;
    public final int clientRefreshRateMs;
    public final int clientResetRateMs;
    public final int clientGetMessageMaxFailCount;

    public AppSettings(
        String rpcName,
        String ipAddress, 
        int port,
        int serverMaxMessageHistory,
        int clientRefreshRateMs,
        int clientResetRateMs,
        int clientGetMessageMaxFailCount
        ) 
    {
        this.rpcName = rpcName;
        this.ipAddress = ipAddress;
        this.port = port;
        this.serverMaxMessageHistory = serverMaxMessageHistory;
        this.clientRefreshRateMs = clientRefreshRateMs;
        this.clientResetRateMs = clientResetRateMs;
        this.clientGetMessageMaxFailCount = clientGetMessageMaxFailCount;
    }

    public AppSettings() {
        this.rpcName = "Chat";
        this.ipAddress = "127.0.0.1";
        this.port = 9000;
        this.serverMaxMessageHistory = 500;
        this.clientRefreshRateMs = 600;
        this.clientResetRateMs = 600_000;
        this.clientGetMessageMaxFailCount = 10;
    }
}
