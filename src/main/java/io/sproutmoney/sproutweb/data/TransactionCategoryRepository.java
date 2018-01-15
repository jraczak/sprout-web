package io.sproutmoney.sproutweb.data;

//  Created by Justin on 1/14/18

import io.sproutmoney.sproutweb.models.TransactionCategory;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TransactionCategoryRepository extends CrudRepository<TransactionCategory, Integer> {

    TransactionCategory findByPlaidCategoryId(String plaidCategoryId);
}
