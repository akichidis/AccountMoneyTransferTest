package mapper;

import dao.Account;
import dto.AccountDto;

import javax.inject.Singleton;

@Singleton
public class AccountDtoMapper {

    public AccountDto convertTo(Account account) {
        AccountDto accountDto = new AccountDto();

        accountDto.setAccountId(account.getId());
        accountDto.setBalance(account.getBalance());
        accountDto.setCurrency(account.getCurrency());
        accountDto.setOwnerName(account.getOwnerName());
        accountDto.setStatus(account.getStatus());
        accountDto.setCreated(account.getCreated());
        accountDto.setModified(account.getModified());

        return accountDto;
    }
}
