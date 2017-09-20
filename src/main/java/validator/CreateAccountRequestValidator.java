package validator;

import dto.CreateAccountRequestDto;

import javax.inject.Singleton;
import javax.ws.rs.BadRequestException;
import java.util.Currency;
import java.util.Optional;

@Singleton
public class CreateAccountRequestValidator implements Validator<CreateAccountRequestDto> {

    @Override
    public void validate(CreateAccountRequestDto createAccountRequest) {
        Optional.ofNullable(createAccountRequest).orElseThrow(() -> new BadRequestException("Invalid request"));

        Optional.ofNullable(createAccountRequest.getOwnerName())
                .filter(name -> name.length() > 5)
                .orElseThrow(() -> new BadRequestException("Invalid owner name"));

        Optional.ofNullable(createAccountRequest.getCurrency())
                .filter(currency -> {
                    try {
                        return Currency.getInstance(currency) != null;
                    } catch (IllegalArgumentException e) {
                        return false;
                    }
                })
                .orElseThrow(() -> new BadRequestException("Invalid currency given"));
    }
}
