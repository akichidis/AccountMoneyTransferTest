package util;

import dto.Money;

import javax.inject.Singleton;
import javax.xml.ws.WebServiceException;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

/**
 * A converter which is used to convert Money from a base currency
 * to a target currency
 */
@Singleton
public class CurrencyConverter {
    private Set<CurrencyValue> currencyExchange;

    public CurrencyConverter() {
        currencyExchange = new HashSet<>();
        currencyExchange.add(new CurrencyValue("EUR", "USD", new BigDecimal("1.19")));
        currencyExchange.add(new CurrencyValue("EUR", "GBP", new BigDecimal("0.879")));
    }

    public Money convertToCurrency(Money money, String currency) {
        BigDecimal amount;

        if (money.getCurrency().equals(currency)) {
            return money;
        }

        CurrencyValue currencyValue = getCurrencyValue(money.getCurrency(), currency);

        if (currencyValue.getFromCurrency().equals(money.getCurrency())) {
            amount = money.getAmount().multiply(currencyValue.getExchangeRate());
        } else {
            amount = money.getAmount().divide(currencyValue.getExchangeRate(), BigDecimal.ROUND_HALF_EVEN);
        }

        return new Money(amount, currency);
    }

    private CurrencyValue getCurrencyValue(String fromCurrency, String toCurrency) {
        return currencyExchange.stream().filter(currencyValue -> {
            return currencyValue.fromCurrency.equals(fromCurrency) && currencyValue.toCurrency.equals(toCurrency) ||
                    currencyValue.fromCurrency.equals(toCurrency) && currencyValue.toCurrency.equals(fromCurrency);
        }).findFirst()
                .orElseThrow(() -> new WebServiceException("No exchange rate has been found!"));
    }

    private static class CurrencyValue {
        private String fromCurrency;
        private String toCurrency;
        private BigDecimal exchangeRate;

        public CurrencyValue(String fromCurrency, String toCurrency, BigDecimal exchangeRate) {
            this.fromCurrency = fromCurrency;
            this.toCurrency = toCurrency;
            this.exchangeRate = exchangeRate;
        }

        public String getFromCurrency() {
            return fromCurrency;
        }

        public void setFromCurrency(String fromCurrency) {
            this.fromCurrency = fromCurrency;
        }

        public String getToCurrency() {
            return toCurrency;
        }

        public void setToCurrency(String toCurrency) {
            this.toCurrency = toCurrency;
        }

        public BigDecimal getExchangeRate() {
            return exchangeRate;
        }

        public void setExchangeRate(BigDecimal exchangeRate) {
            this.exchangeRate = exchangeRate;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            CurrencyValue that = (CurrencyValue) o;

            if (fromCurrency != null ? !fromCurrency.equals(that.fromCurrency) : that.fromCurrency != null)
                return false;
            if (toCurrency != null ? !toCurrency.equals(that.toCurrency) : that.toCurrency != null) return false;
            return exchangeRate != null ? exchangeRate.equals(that.exchangeRate) : that.exchangeRate == null;
        }

        @Override
        public int hashCode() {
            int result = fromCurrency != null ? fromCurrency.hashCode() : 0;
            result = 31 * result + (toCurrency != null ? toCurrency.hashCode() : 0);
            result = 31 * result + (exchangeRate != null ? exchangeRate.hashCode() : 0);
            return result;
        }
    }
}
