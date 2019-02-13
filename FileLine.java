import java.io.Serializable;

public class FileLine implements Serializable {
    private String time;
    private String eventContext;
    private String component;
    private String eventName;
    private String description;
    private String origin;
    private String IPAddress;

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getEventContext() {
        return eventContext;
    }

    public void setEventContext(String eventContext) {
        this.eventContext = eventContext;
    }

    public String getComponent() {
        return component;
    }

    public void setComponent(String component) {
        this.component = component;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getOrigin() {
        return origin;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public String getIPAddress() {
        return IPAddress;
    }

    public void setIPAddress(String IPAddress) {
        this.IPAddress = IPAddress;
    }

    public FileLine(String time, String eventContext, String component,
                    String eventName, String description, String origin, String IPAddress) {
        this.time = time;
        this.eventContext = eventContext;
        this.component = component;
        this.eventName = eventName;
        this.description = description;
        this.origin = origin;
        this.IPAddress = IPAddress;
    }

    public FileLine() {
        this("", "", "", "", "", "", "");
    }

    @Override
    public String toString() {
        return String.format("time = %s%n" + "event context = %s%n"
                + "component = %s%n" + "event name = %s%n"
                + "description = %s%n" + "origin = %s%n" + "IP address = %s%n",
                time, eventContext, component, eventName,
                description, origin, IPAddress);
    }
}
