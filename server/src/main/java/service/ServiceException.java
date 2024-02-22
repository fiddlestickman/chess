package service;

/**
 * Indicates there was a server error (failure to authenticate, improper login/register, missing game)
 */
public class ServiceException extends Exception{
    private int code;
    public ServiceException(String message, int code) {
        super(message);
        this.code = code;
    }
    public int getCode() {
        return code;
    }
}