package service;

import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ServiceException that = (ServiceException) o;
        return code == that.code;
    }
    @Override
    public int hashCode() {
        return Objects.hash(code);
    }
}

