package dao;

import dto.Money;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import java.math.BigDecimal;
import java.time.Instant;

public class Account {
    private String id;
    private String ownerName;
    private BigDecimal balance;
    private String currency;
    private AccountStatus status;
    private Instant created;
    private Instant modified;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public void deposit(Money money) {
        validateCurrency(money.getCurrency());

        balance = balance.add(money.getAmount());
    }

    public void withdraw(Money money) {
        validateCurrency(money.getCurrency());

        if (balance.compareTo(money.getAmount()) >= 0) {
            balance = balance.subtract(money.getAmount());
            return;
        }

        throw new WebApplicationException("Insufficient funds for withdraw", Response.Status.CONFLICT);
    }

    private void validateCurrency(String currency) {
        if (!this.currency.equals(currency)) {
            throw new WebApplicationException("Money with different currency given", Response.Status.CONFLICT);
        }
    }
}
