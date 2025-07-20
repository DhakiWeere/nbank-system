package exception;

import jakarta.ejb.ApplicationException;

@ApplicationException(rollback = true)
public class CustomerLoginFailedException extends Exception{
    private static final String MSG = "Login Failed";

    public String getMSG() {
        return MSG;
    }
}
