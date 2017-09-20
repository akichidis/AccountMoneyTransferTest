package service;

import dao.Transaction;
import dto.DepositTransactionRequestDto;
import dto.TransferTransactionRequestDto;

/**
 * TransactionService is responsible for accessing
 * and updating all the transaction operations.
 */
public interface TransactionService {

    String createDepositTransaction(DepositTransactionRequestDto transactionRequest);

    String createTransferTransaction(TransferTransactionRequestDto transferTransactionRequest);

    Transaction getTransaction(String transactionId);
}
