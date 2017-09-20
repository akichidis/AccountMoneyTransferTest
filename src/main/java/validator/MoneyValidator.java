package validator;

import dto.Money;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.BadRequestException;
import java.math.BigDecimal;
import java.util.Optional;

@Singleton
public class MoneyValidator implements Validator<Money> {

    @Inject
    private CurrencyValidator currencyValidator;

    @Override
    public void validate(Money money) {
        Optional.ofNullable(money).orElseThrow(() -> new BadRequestException("Money is null"));

        currencyValidator.validate(money.getCurrency());

        if (money.getAmount() == null || money.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BadRequestException("Given amount is not valid");
        }
    }
}
