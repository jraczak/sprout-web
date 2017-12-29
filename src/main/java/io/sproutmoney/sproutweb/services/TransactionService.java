package io.sproutmoney.sproutweb.services;

//  Created by Justin on 12/26/17

import io.sproutmoney.sproutweb.data.TransactionRepository;
import io.sproutmoney.sproutweb.models.Account;
import io.sproutmoney.sproutweb.models.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service()
public class TransactionService  {

    private TransactionRepository transactionRepository;

    @Autowired
    public TransactionService(TransactionRepository repository) {
        this.transactionRepository = repository;
    }

    public Set<Transaction> findAllByPlaidAccountId(String plaidAccountId) {
        return transactionRepository.findAllByPlaidAccountId(plaidAccountId);
    }

    public Set<Transaction> findAllByAccountId(int accountId) {
        return transactionRepository.findAllByAccountId(accountId);
    }

    public Transaction findByPlaidTransactionId(String plaidTransactionId) {
        return transactionRepository.findByPlaidTransactionId(plaidTransactionId);
    }

    public void saveTransaction(Transaction transaction) {
        transactionRepository.save(transaction);
    }
}
