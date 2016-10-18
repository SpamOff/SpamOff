package nldr.spamoff.SMSHandler;

import java.util.Date;

/**
 * Created by lior on 15-Oct-16.
 */
public class SMSMessage {

    public String getAddress() {
        return _address;
    }

    public void setAddress(String _address) {
        this._address = _address;
    }

    public String getTimeStamp() {
        return _timeStamp;
    }

    public void setTimeStamp(String _timeStamp) {
        this._timeStamp = _timeStamp;
    }

    public String getBody() {
        return _body;
    }

    public void setBody(String _body) {
        this._body = _body;
    }

    private String _address;
    private String _timeStamp;
    private String _body;

    public SMSMessage(){

    }
    public SMSMessage(String _address,String _timeStamp, String _body) {
        this._address = _address;
        this._timeStamp = _timeStamp;
        this._body = _body;

    }
}
