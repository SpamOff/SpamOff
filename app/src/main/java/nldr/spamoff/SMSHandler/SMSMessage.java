package nldr.spamoff.SMSHandler;

import java.util.Date;

/**
 * Created by lior on 15-Oct-16.
 */
public class SMSMessage {

    public String getAddress() {
        return this._address;
    }

    public void setAddress(String address) {
        this._address = address;
    }

    public String getBody() {
        return this._body;
    }

    public void setBody(String body) {
        this._body = body;
    }

    public String getTimeStamp() {
        return this._timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this._timeStamp = timeStamp;
    }

    private String _address;
    private String _body;
    private String _timeStamp;

    public SMSMessage(){

    }

    public SMSMessage(String address, String body, String timeStamp) {

        this._address = address;
        this._body = body;
        this._timeStamp = timeStamp;
    }
}
