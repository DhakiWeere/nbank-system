package entity;

import dbenum.TransactionStatus;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "scheduled_transaction")
public class ScheduledTransaction {

    @Id
    @Column(name = "transaction_id")
    private Integer transactionId;

    @OneToOne
    @JoinColumn(name = "transaction_id", referencedColumnName = "id", insertable = false, updatable = false)
    private Transaction transaction;

    @Column(name = "scheduledat", nullable = false)
    private LocalDateTime scheduledAt;

    @Column(name = "createdat", nullable = false, insertable = false, updatable = false)
    private LocalDateTime createdAt;


    public Integer getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(Integer transactionId) {
        this.transactionId = transactionId;
    }

    public Transaction getTransaction() {
        return transaction;
    }

    public void setTransaction(Transaction transaction) {
        this.transaction = transaction;
    }

    public LocalDateTime getScheduledAt() {
        return scheduledAt;
    }

    public void setScheduledAt(LocalDateTime scheduledAt) {
        this.scheduledAt = scheduledAt;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
