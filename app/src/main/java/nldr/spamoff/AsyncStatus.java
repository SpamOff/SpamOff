package nldr.spamoff;

/**
 * Created by Roee on 14/11/2016.
 */
public enum AsyncStatus {
    finished,
    canceled,
    noInternet,
    noNewMessages,
    slowInternet,
    failedWhileSendingToServer,
    smsReadingError,
    smsFieldsMistmatch;

    private static Long time = null;

    public static void setTime(Long value) {
        time = value;
    }

    public static Long getTime() { return time; }
}
