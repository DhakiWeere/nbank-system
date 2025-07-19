package timer;

import annotation.ServiceCallLog;
import dao.CustomerAccountDAO;
import dao.TransactionDAO;
import entity.Account;
import entity.Transaction;
import jakarta.ejb.*;
import jakarta.inject.Inject;
import service.TransactionService;

import java.util.List;

@Singleton
@Startup
@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
public class InterestTimer {

    @Inject
    CustomerAccountDAO customerAccountDAO;

    @EJB
    TransactionService transactionService;

    @Schedule(hour = "2", minute = "0", second = "0", persistent = false)
    @ServiceCallLog
    public void executeDailyInterestPayout() {
        // get all interest-eligible accounts
        List<Account> accountList = customerAccountDAO.getAccountsByInterestEnabled();

        for(Account account : accountList){
            // interest payout
            transactionService.interestPayoutCurrentDate(account.getAccNo());
        }
    }


}
