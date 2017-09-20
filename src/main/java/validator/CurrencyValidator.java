package validator;

import javax.inject.Singleton;
import javax.ws.rs.BadRequestException;
import java.util.Currency;
import java.util.Optional;

@Singleton
public class CurrencyValidator implements Validator<String> {

    public void validate(String currency) {
        Optional.ofNullable(currency)
                .filter(c -> {
                    try {
                        return Currency.getInstance(c) != null;
                    } catch (IllegalArgumentException e) {
                        return  false;
                    }
                })
                .orElseThrow(() -> new BadRequestException("Invalid currency given"));
    }
}
