import java.net.InetAddress;
import java.util.Objects;

public class Pair {

    private final InetAddress IPaddress;
    private final Integer port;

    public Pair(InetAddress element0, Integer element1) {
        this.IPaddress = element0;
        this.port = element1;
    }

    public InetAddress getIPaddress() {
        return IPaddress;
    }

    public Integer getPort() {
        return port;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Pair pair = (Pair) o;
        return IPaddress.equals(pair.IPaddress) &&
                port.equals(pair.port);
    }

    @Override
    public int hashCode() {
        return Objects.hash(IPaddress, port);
    }
}