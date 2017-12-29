package io.sproutmoney.sproutweb.data;

//  Created by Justin on 12/26/17

import io.sproutmoney.sproutweb.models.Transaction;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface TransactionRepository extends CrudRepository<Transaction, Integer> {

    Set<Transaction> findAllByPlaidAccountId(String plaidAccountId);
    Set<Transaction> findAllByAccountId(int accountId);
    Transaction findByPlaidTransactionId(String plaidTransactionId);
}
