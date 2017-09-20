package dao;

import java.math.BigDecimal;
import java.time.Instant;

public class AccountMovementRecord {
    private String id;
    private String accountId;
    private String referenceTransactionId;
    private BigDecimal amount;
    private String currency;
    private AccountMovementType accountMovementType;
    private Instant created;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public AccountMovementType getAccountMovementType() {
        return accountMovementType;
    }

    public void setAccountMovementType(AccountMovementType accountMovementType) {
        this.accountMovementType = accountMovementType;
    }

    public Instant getCreated() {
        return created;
    }

    public void setCreated(Instant created) {
        this.created = created;
    }

    public void setReferenceTransactionId(String referenceTransactionId) {
        this.referenceTransactionId = referenceTransactionId;
    }

    public String getReferenceTransactionId() {
        return referenceTransactionId;
    }
}
