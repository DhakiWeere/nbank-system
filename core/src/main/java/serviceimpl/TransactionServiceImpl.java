package serviceimpl;

import annotation.ServiceCallLog;
import config.AppConfig;
import dao.CustomerAccountDAO;
import dao.TransactionDAO;
import dbenum.TransactionType;
import entity.Account;
import entity.Transaction;
import jakarta.ejb.*;
import jakarta.inject.Inject;
import jakarta.transaction.SystemException;
import jakarta.transaction.UserTransaction;
import service.TransactionService;
import timer.ScheduledFundTransferTimer;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Stateless
@ServiceCallLog
@TransactionManagement(TransactionManagementType.BEAN)
public class TransactionServiceImpl implements TransactionService {

    @Inject
    CustomerAccountDAO customerAccountDAO;

    @Inject
    TransactionDAO transactionDAO;

    @EJB
    AppConfig appConfig;

    @Inject
    UserTransaction utx;

    @EJB
    ScheduledFundTransferTimer sFundTransferTimer;

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

                // calculated date
                LocalDate calculatedDate = LocalDate.now();
                // interest record
                transactionDAO.addNewInterestRecord(account, interest, calculatedDate);
                // Update balance records
                transactionDAO.updateBalance(account, interest, TransactionType.INTEREST);
                // Record transaction
                transactionDAO.addCompletedTransactionRecord(account, interest, TransactionType.INTEREST,
                        String.format("Interest Payout for date : %s", calculatedDate.toString()), null, null);

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

    @Override
    public boolean scheduleFundTransfer(
            String fromAccNo,
            String toAccNo,
            BigDecimal amount,
            String description,
            LocalDateTime scheduleTime
    ) {


        // make pending transaction records
        // get accounts
        Account fromAccount = customerAccountDAO.getAccountByAccNo(fromAccNo);
        Account toAccount = customerAccountDAO.getAccountByAccNo(toAccNo);

        try {
            // transaction
            utx.begin();

            // transfer out record
            Transaction fromTX = transactionDAO.addPendingTransactionRecord(
                    fromAccount, amount, TransactionType.TRANSFER_OUT, description, null, toAccount);
            // add scheduled transfer record
            transactionDAO.addNewScheduledTransactionRecord(fromTX, scheduleTime);
            // start timer
            sFundTransferTimer.scheduleTimerByTransaction(fromTX.getId(), scheduleTime);

            utx.commit();
            return true;

        }catch (Exception e){
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
