package mapper;

import dao.Transaction;
import dto.Money;
import dto.TransactionDto;

public class TransactionMapper {

    public TransactionDto convertToTransactionDto(Transaction transaction) {
        TransactionDto transactionDto = new TransactionDto();

        transactionDto.setTransactionId(transaction.getTransactionId());
        transactionDto.setTransactionType(transaction.getTransactionType());

        transactionDto.setCreditAccountId(transaction.getCreditAccountId());
        transactionDto.setDebitAccountId(transaction.getDebitAccountId());

        transactionDto.setCreated(transaction.getCreated());
        transactionDto.setMoney(new Money(transaction.getAmount(), transaction.getCurrency()));

        transactionDto.setNote(transaction.getNote());

        return transactionDto;
    }
}
