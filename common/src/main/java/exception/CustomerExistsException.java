package exception;

import jakarta.ejb.ApplicationException;

@ApplicationException(rollback = true)
public class CustomerExistsException extends Exception{
    private static final String MSG = "Customer already registered";

    public String getMSG() {
        return MSG;
    }
}
