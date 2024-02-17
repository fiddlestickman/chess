package service;

/**
 * Indicates there was a server error (failure to authenticate, improper login/register, missing game)
 */
public class ServiceException extends Exception{
    public ServiceException(String message) {
        super(message);
    }
}