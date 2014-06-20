package ie.imobile.extremepush.util;

public interface ReconnectDelay {
    public long getDelay(long timeout, int numberOfRetry);
}
