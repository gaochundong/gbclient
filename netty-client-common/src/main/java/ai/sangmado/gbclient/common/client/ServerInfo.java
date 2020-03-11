package ai.sangmado.gbclient.common.client;

/**
 * 服务器信息
 */
@SuppressWarnings("RedundantIfStatement")
public class ServerInfo {

    public static final String DEFAULT_HOST = "localhost";
    public static final int DEFAULT_PORT = 7200;

    private final String host;
    private final int port;

    public ServerInfo() {
        this(DEFAULT_HOST, DEFAULT_PORT);
    }

    public ServerInfo(String host) {
        this(host, DEFAULT_PORT);
    }

    public ServerInfo(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (host == null ? 0 : host.hashCode());
        result = prime * result + port;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        ServerInfo other = (ServerInfo) obj;
        if (host == null) {
            if (other.host != null) {
                return false;
            }
        } else if (!host.equals(other.host)) {
            return false;
        }
        if (port != other.port) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "ServerInfo{" +
                "host='" + host + '\'' +
                ", port=" + port +
                '}';
    }
}
