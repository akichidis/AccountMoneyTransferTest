package unit.validator

import dto.Money
import spock.lang.Specification
import validator.CurrencyValidator
import validator.MoneyValidator

import javax.ws.rs.BadRequestException

class MoneyValidatorSpec extends Specification {
    MoneyValidator moneyValidator
    CurrencyValidator currencyValidator

    def setup() {
        currencyValidator = new CurrencyValidator()
        moneyValidator = new MoneyValidator(currencyValidator: currencyValidator)
    }

    def "Validator works as expected with correct parameters" () {
        when:
            moneyValidator.validate(inputMoney)

        then:
            noExceptionThrown()

        where:
            inputMoney << [new Money(new BigDecimal("20.00"), "EUR"),
                           new Money(new BigDecimal("10.00"), "GBP")]
    }

    def "Validator works as expected with incorrect parameters" () {
        when:
            moneyValidator.validate(inputMoney)

        then:
            thrown(BadRequestException)

        where:
            inputMoney << [new Money(new BigDecimal("-20.00"), "EUR"),
                           new Money(new BigDecimal("10.00"), "AAAA"),
                           new Money(null, "AAAA"),
                           new Money(new BigDecimal("10.00"), null),
                           new Money(new BigDecimal("0.00"), "EUR"),
                           new Money(new BigDecimal("0"), "EUR")]
    }
}
