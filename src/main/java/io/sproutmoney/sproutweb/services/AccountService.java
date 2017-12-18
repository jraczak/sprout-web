package io.sproutmoney.sproutweb.services;

//  Created by Justin on 12/18/17

import io.sproutmoney.sproutweb.data.AccountRepository;
import io.sproutmoney.sproutweb.models.Account;
import io.sproutmoney.sproutweb.models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class AccountService implements AccountRepository {

    private AccountRepository accountRepository;

    @Autowired
    public AccountService(AccountRepository repository) {
        this.accountRepository = accountRepository;
    }

    @Override
    public Account findById(int id) {
        return accountRepository.findById(id);
    }

    @Override
    public Account findByPlaidAccountId(String plaidAccountId) {
        return accountRepository.findByPlaidAccountId(plaidAccountId);
    }

    @Override
    public Iterable<Account> findAll() {
        return accountRepository.findAll();
    }

    @Override
    public Set<Account> findAllByUser(User user) {
        return accountRepository.findAllByUser(user);
    }

    public void saveAccount(Account account) {
        accountRepository.save(account);
    }
}
