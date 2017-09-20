package dto;

import dao.AccountMovementType;

import java.math.BigDecimal;
import java.time.Instant;

public class AccountMovementRecordDto {
    private String id;
    private String accountId;
    private String referenceTransactionId;
    private Money money;
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

    public String getReferenceTransactionId() {
        return referenceTransactionId;
    }

    public void setReferenceTransactionId(String referenceTransactionId) {
        this.referenceTransactionId = referenceTransactionId;
    }

    public Money getMoney() {
        return money;
    }

    public void setMoney(Money money) {
        this.money = money;
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
}
