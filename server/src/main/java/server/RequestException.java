package server;

/** indicates that the request was invalid (usually an incorrect data type for the method)
 */
public class RequestException extends Exception {
    public RequestException(String message) {super(message);}
}
