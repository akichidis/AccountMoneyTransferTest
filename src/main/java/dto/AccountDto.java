package dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import dao.AccountStatus;

import java.math.BigDecimal;
import java.time.Instant;

public class AccountDto {
    @JsonProperty
    private String accountId;

    @JsonProperty
    private String ownerName;

    @JsonProperty
    private BigDecimal balance;

    @JsonProperty
    private String currency;

    @JsonProperty
    private AccountStatus status;

    @JsonProperty
    private Instant created;

    @JsonProperty
    private Instant modified;


    public AccountDto() {}

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public AccountStatus getStatus() {
        return status;
    }

    public void setStatus(AccountStatus status) {
        this.status = status;
    }

    public Instant getCreated() {
        return created;
    }

    public void setCreated(Instant created) {
        this.created = created;
    }

    public Instant getModified() {
        return modified;
    }

    public void setModified(Instant modified) {
        this.modified = modified;
    }
}
