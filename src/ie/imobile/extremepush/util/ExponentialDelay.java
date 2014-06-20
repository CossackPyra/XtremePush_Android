package ie.imobile.extremepush.util;

public class ExponentialDelay implements ReconnectDelay {
    @Override
    public long getDelay(long timeout, int numberOfRetry) {
        return (long) (timeout * Math.pow(2, numberOfRetry));
    }
}
