package serviceimpl;

import annotation.ServiceCallLog;
import config.AppConfig;
import dao.CustomerAccountDAO;
import dao.TransactionDAO;
import dbenum.TransactionStatus;
import dbenum.TransactionType;
import entity.Account;
import entity.Balance;
import entity.Transaction;
import jakarta.ejb.*;
import jakarta.inject.Inject;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.SystemException;
import jakarta.transaction.UserTransaction;
import service.TransactionService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Stateless
@TransactionManagement(TransactionManagementType.BEAN)
@ServiceCallLog
public class TransactionServiceImpl implements TransactionService {

    @Inject
    CustomerAccountDAO customerAccountDAO;

    @Inject
    TransactionDAO transactionDAO;

    @EJB
    AppConfig appConfig;

    @Inject
    UserTransaction utx;

    public boolean interestPayoutCurrentDate(String accountNo) {

            // get account
            Account account = customerAccountDAO.getAccountByAccNo(accountNo);
            // get balance
            BigDecimal currentBalanceToDate = transactionDAO.getBalance(account);
            // no balance  || balance <= 0 --> no interest
            if (currentBalanceToDate == null || currentBalanceToDate.compareTo(BigDecimal.ZERO) <= 0) {
                return false;
            }
            // calculate interest
            BigDecimal interest = transactionDAO.calculateInterest(
                    currentBalanceToDate,
                    appConfig.getANNUAL_INTEREST_RATE());

            try {
                // transaction begin
                utx.begin();

                // interest record
                transactionDAO.addNewInterestRecord(account, interest, LocalDate.now());
                // Update balance records
                transactionDAO.updateBalance(account, interest, TransactionType.INTEREST);
                // Record transaction
                transactionDAO.addTransactionRecord(account, interest, TransactionType.INTEREST);

                utx.commit();
                return true;

            } catch (Exception e) {
                e.printStackTrace();
                try {
                    utx.rollback();
                } catch (SystemException ex) {
                    throw new RuntimeException(ex);
                }
                return false;

            }

    }

}
