package server;

/** indicates that the request was invalid (usually an incorrect data type for the method)
 */
public class RequestException extends Exception {
    private int code;
    public RequestException(String message, int code) {
        super(message);
        this.code = code;
    }
    public int getCode() {
        return code;
    }
}
