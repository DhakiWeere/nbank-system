package timer;

import dao.CustomerAccountDAO;
import dao.TransactionDAO;
import dbenum.TransactionType;
import entity.Transaction;
import jakarta.annotation.Resource;
import jakarta.ejb.*;
import jakarta.inject.Inject;

import java.time.LocalDateTime;

@Stateless
public class ScheduledFundTransferTimer {

    @Resource
    private TimerService timerService;

    @Inject
    private CustomerAccountDAO customerAccountDAO;

    @Inject
    private TransactionDAO transactionDAO;

    private ScheduleExpression toScheduleExpression(LocalDateTime dateTime) {
        // localDateTime -> scheduleExpression
        return new ScheduleExpression()
                .year(dateTime.getYear())
                .month(dateTime.getMonthValue())
                .dayOfMonth(dateTime.getDayOfMonth())
                .hour(dateTime.getHour())
                .minute(dateTime.getMinute())
                .second(dateTime.getSecond());
    }

    public void scheduleTimerByTransaction(int txID, LocalDateTime dateTime) {
        ScheduleExpression schedule = toScheduleExpression(dateTime);
        TimerConfig config = new TimerConfig();
        config.setInfo(String.valueOf(txID));
        config.setPersistent(true);

        timerService.createCalendarTimer(schedule, config);
        System.out.println("Transaction Timer set for: " + dateTime);
    }

    @Timeout
    public void transferScheduledFunds(Timer timer) {
        // get transaction
        int transactionID = (int)timer.getInfo();
        Transaction fromTx = transactionDAO.getTransactionByID(transactionID);

        // transfer funds
        transactionDAO.transferFunds(
                fromTx.getAccount(),
                fromTx.getToAccount(),
                fromTx.getAmount(),
                fromTx.getDescription());

        // mark pending transaction completed
        fromTx =  transactionDAO.markTransactionStatusCompleted(fromTx);

        // add new transfer in record
        transactionDAO.addCompletedTransactionRecord(
                fromTx.getToAccount(),
                fromTx.getAmount(),
                TransactionType.TRANSFER_IN,
                fromTx.getDescription(),
                fromTx.getAccount(),
                null
        );






    }


}
