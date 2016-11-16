package nldr.spamoff;

/**
 * Created by Roee on 14/11/2016.
 */
public enum AppStateEnum {
    loading(0),
    didntApprovedTerms(1),
    main(2),
    scanFinished(3),
    resultsReturned(4);

    private final int _state;

    AppStateEnum(int state) {
        this._state = state;
    }

    public int getState() {
        return this._state;
    }
}
