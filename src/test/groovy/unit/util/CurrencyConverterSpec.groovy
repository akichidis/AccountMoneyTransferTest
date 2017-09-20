package unit.util

import dto.Money
import spock.lang.Specification
import util.CurrencyConverter


class CurrencyConverterSpec extends Specification {
    CurrencyConverter currencyConverter

    def setup() {
        currencyConverter = new CurrencyConverter()
    }

    def "Converter works as expected" () {
        when:
            Money resultMoney = currencyConverter.convertToCurrency(inputMoney, targetCurrency)

        then:
            assert resultMoney == convertedMoney

        where:

            inputMoney                                              |   targetCurrency  | convertedMoney
            new Money(new BigDecimal("10.00"), "EUR")  |    "EUR"          | new Money(new BigDecimal("10.00"), "EUR")
            new Money(new BigDecimal("10.00"), "EUR")  |    "GBP"          | new Money(new BigDecimal("8.79"), "GBP")
            new Money(new BigDecimal("10.00"), "EUR")  |    "USD"          | new Money(new BigDecimal("11.90"), "USD")
            new Money(new BigDecimal("10.00"), "GBP")  |    "EUR"          | new Money(new BigDecimal("11.38"), "EUR")
            new Money(new BigDecimal("10.00"), "USD")  |    "EUR"          | new Money(new BigDecimal("8.40"), "EUR")
    }
}
