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
    smsFieldsMistmatch
}
