package entity;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "balance")
public class Balance {

    @Id
    @Column(name = "account_id")
    private int accountId;

    @MapsId
    @OneToOne
    @JoinColumn(name = "account_id")
    private Account account;

    @Column(nullable = false, precision = 20, scale = 2)
    private BigDecimal balance;

    public int getAccountId() {
        return accountId;
    }

    public void setAccountId(int accountId) {
        this.accountId = accountId;
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }
}
