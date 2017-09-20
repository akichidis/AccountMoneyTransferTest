package mapper;

import dao.AccountMovementRecord;
import dto.AccountMovementRecordDto;
import dto.AccountMovementsResultDto;
import dto.Money;

import javax.inject.Singleton;
import java.util.List;
import java.util.stream.Collectors;

@Singleton
public class AccountMovementsResultMapper {

    public AccountMovementsResultDto convertTo(List<AccountMovementRecord> accountMovementRecordList) {
        AccountMovementsResultDto accountMovementsResult = new AccountMovementsResultDto();

        List<AccountMovementRecordDto> resultList = accountMovementRecordList.stream()
                                            .map(this::convertTo).collect(Collectors.toList());

        accountMovementsResult.setMovements(resultList);

        return accountMovementsResult;
    }

    private AccountMovementRecordDto convertTo(AccountMovementRecord accountMovementRecord) {
        AccountMovementRecordDto accountMovementRecordDto = new AccountMovementRecordDto();

        accountMovementRecordDto.setId(accountMovementRecord.getId());
        accountMovementRecordDto.setAccountId(accountMovementRecord.getAccountId());
        accountMovementRecordDto.setAccountMovementType(accountMovementRecord.getAccountMovementType());
        accountMovementRecordDto.setMoney(new Money(accountMovementRecord.getAmount(), accountMovementRecord.getCurrency()));
        accountMovementRecordDto.setReferenceTransactionId(accountMovementRecord.getReferenceTransactionId());
        accountMovementRecordDto.setCreated(accountMovementRecord.getCreated());

        return accountMovementRecordDto;
    }
}
