package dao;

import config.AppConfig;
import dbenum.TransactionStatus;
import dbenum.TransactionType;
import entity.Account;
import entity.Balance;
import entity.Interest;
import entity.Transaction;
import jakarta.ejb.*;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;

@RequestScoped
public class TransactionDAO {

    @PersistenceContext(name = "NBPers")
    private EntityManager em;

    @EJB
    AppConfig appConfig;


    public void addTransactionRecord(Account account, BigDecimal amount, TransactionType txType) {
        Transaction tx = new Transaction();
        tx.setAccount(account);
        tx.setStatus(TransactionStatus.COMPLETED);
        tx.setAmount(amount);
        tx.setType(txType);

        em.persist(tx);
    }

    public void addCompletedTransactionRecord(Account account, BigDecimal amount, TransactionType txType, String description) {
        Transaction tx = new Transaction();
        tx.setAccount(account);
        tx.setAmount(amount);
        tx.setStatus(TransactionStatus.COMPLETED);
        tx.setType(txType);
        tx.setDescription(description);

        em.persist(tx);
    }

    public boolean isAmountDebitableFromAccount(Account account, BigDecimal amount) {
        Balance balanceEnt = em.find(Balance.class, account.getId());
        if (balanceEnt == null) {
            return false;
        } else {
            return balanceEnt.getBalance().subtract(amount).compareTo(BigDecimal.ZERO) < 0;
        }
    }

    public boolean updateBalance(Account account, BigDecimal amount, TransactionType txType) {
        boolean isNewBalance = false;
        Balance balance = em.find(Balance.class, account.getId());

        // creating new balance record of not exist
        if (balance == null) {
            isNewBalance = true;
            balance = new Balance();
            balance.setAccount(account);
            balance.setBalance(BigDecimal.ZERO);
        }

        // updating balance
        if (txType == TransactionType.TRANSFER_OUT || txType == TransactionType.WITHDRAWAL) {
            // ADDING FUNDS
            balance.setBalance(balance.getBalance().subtract(amount));

        } else if (txType == TransactionType.DEPOSIT || txType == TransactionType.TRANSFER_IN || txType == TransactionType.INTEREST) {
            // SUBTRACTING FUNDS
            balance.setBalance(balance.getBalance().add(amount));
        }

        if (isNewBalance) {
            em.persist(balance);
        } else {
            em.merge(balance);
        }
        return true;
    }

    public BigDecimal getBalance(Account account) {
        return new BigDecimal(0);
    }

    public boolean scheduleTransferFunds(Account fromAccount, Account toAccount, BigDecimal amount, String description, LocalDateTime date) {
        return false;
    }

    public boolean transferFunds(Account fromAccount, Account toAccount, BigDecimal amount, String description) {
        try {
            if(isAmountDebitableFromAccount(fromAccount, amount)){
                // AMOUNT IS DEBITABLE

                // debit transaction tjo fromAcc
                Transaction txDebit = new Transaction();
                txDebit.setAccount(fromAccount);
                txDebit.setType(TransactionType.TRANSFER_OUT);
                txDebit.setStatus(TransactionStatus.COMPLETED);
                txDebit.setAmount(amount);
                txDebit.setDescription(description + " - debit");
                em.persist(txDebit);
                // fromAcc balance update
                updateBalance(fromAccount, amount, TransactionType.TRANSFER_OUT);

                // credit transaction to toAcc
                Transaction creditTxn = new Transaction();
                creditTxn.setAccount(toAccount);
                creditTxn.setType(TransactionType.TRANSFER_IN);
                creditTxn.setStatus(TransactionStatus.COMPLETED);
                creditTxn.setAmount(amount);
                creditTxn.setDescription(description + " - credit");
                em.persist(creditTxn);
                // toAcc balance update
                updateBalance(toAccount, amount, TransactionType.TRANSFER_IN);

                return true;
            }

            return false;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean isTodayInterestPayed(Account account) {
        // check if interest is paid for today's date
        LocalDate today = LocalDate.now();
        int count = em.createQuery("SELECT COUNT(i) FROM Interest i WHERE i.account_id = :accId AND i.calculatedFor = :today", Integer.class)
                .setParameter("accId", account.getId())
                .setParameter("today", today)
                .getSingleResult();

        return count != 0;
    }

    public BigDecimal calculateInterest(BigDecimal startingBalance, BigDecimal annualRate) {
        BigDecimal dailyRate = annualRate.divide(BigDecimal.valueOf(365), 10, RoundingMode.HALF_UP);
        return startingBalance.multiply(dailyRate).setScale(2, RoundingMode.HALF_UP);
    }

    public Interest addNewInterestRecord(Account account, BigDecimal interestAmount, LocalDate calculatedDate) {
        Interest interest = new Interest();
        interest.setAccount(account);
        interest.setAmount(interestAmount);
        interest.setCalculatedFor(calculatedDate);
        return interest;
    }

}
