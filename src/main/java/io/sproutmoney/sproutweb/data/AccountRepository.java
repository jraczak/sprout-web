package io.sproutmoney.sproutweb.data;

//  Created by Justin on 12/18/17

import io.sproutmoney.sproutweb.models.Account;
import io.sproutmoney.sproutweb.models.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface AccountRepository extends CrudRepository<Account, Long> {

    Set<Account> findAllByUser(User user);
    Set<Account> findAllByUserOrderByInstitutionNameAsc(User user);
    Account findByPlaidAccountId(String plaidAccountId);
    Account findAccountByPlaidAccountId(String plaidAccountId);
    Account findById(int id);
}
