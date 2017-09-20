package dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;

public class Money implements Comparable<Money> {
    private BigDecimal amount;
    private String currency;

    public Money() {
    }

    public Money(BigDecimal amount, String currency) {
        this.amount = amount;
        this.currency = currency;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Money money = (Money) o;

        if (!amount.equals(money.amount)) return false;
        return currency.equals(money.currency);
    }

    /**
     * This method compares to Money objects of the same currency.
     * If Money with different currencies is given, then a @RuntimeException is thrown
     *
     * @param o
     * @throws RuntimeException
     * @return
     */
    @Override
    public int compareTo(Money o) {
        if (o == null) {
            throw new NullPointerException();
        }

        if (!currency.equals(o.currency)) {
            throw new RuntimeException("Money with invalid currencies compared");
        }

        return amount.compareTo(o.amount);
    }
}
