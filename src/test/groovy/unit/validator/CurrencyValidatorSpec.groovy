package unit.validator

import spock.lang.Specification
import validator.CurrencyValidator

import javax.ws.rs.BadRequestException

class CurrencyValidatorSpec extends Specification {
    CurrencyValidator currencyValidator

    def setup() {
        currencyValidator = new CurrencyValidator()
    }

    def "Validator works as expected with correct parameters" () {
        when:
            currencyValidator.validate(inputCurrency)

        then:
            noExceptionThrown()

        where:
            inputCurrency << ["EUR", "GBP","CHF"]
    }

    def "Validator works as expected with incorrect parameters" () {
        when:
            currencyValidator.validate(inputCurrency)

        then:
            thrown(BadRequestException)

        where:
            inputCurrency << ["", "AAAA","B", null]
    }
}
