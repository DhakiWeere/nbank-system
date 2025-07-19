package service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public interface TransactionService{
    public boolean interestPayoutCurrentDate(String accountNo);

    public boolean scheduleFundTransfer(String fromAccNo, String toAccNo, BigDecimal amount, String description, LocalDateTime scheduleTime);
}
