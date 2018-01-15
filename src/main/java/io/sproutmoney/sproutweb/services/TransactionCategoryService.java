package io.sproutmoney.sproutweb.services;

//  Created by Justin on 1/14/18

import io.sproutmoney.sproutweb.data.TransactionCategoryRepository;
import io.sproutmoney.sproutweb.models.TransactionCategory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service//("transactionCategoryService")
public class TransactionCategoryService {

    private TransactionCategoryRepository transactionCategoryRepository;

    @Autowired
    public TransactionCategoryService(TransactionCategoryRepository repository) {
        this.transactionCategoryRepository = repository;
    }

    public void saveTransactionCategory(TransactionCategory transactionCategory) {
        transactionCategoryRepository.save(transactionCategory);
    }

    public TransactionCategory findByPlaidCategoryId(String plaidCategoryId) {
        return transactionCategoryRepository.findByPlaidCategoryId(plaidCategoryId);
    }
}
