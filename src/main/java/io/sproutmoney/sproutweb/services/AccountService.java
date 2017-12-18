package io.sproutmoney.sproutweb.services;

//  Created by Justin on 12/18/17

import io.sproutmoney.sproutweb.data.AccountRepository;
import io.sproutmoney.sproutweb.models.Account;
import io.sproutmoney.sproutweb.models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class AccountService {

    private AccountRepository accountRepository;

    @Autowired
    public AccountService(AccountRepository repository) {
        this.accountRepository = repository;
    }

    public Account findById(int id) {
        return accountRepository.findById(id);
    }

    public Account findByPlaidAccountId(String plaidAccountId) {
        return accountRepository.findByPlaidAccountId(plaidAccountId);
    }

    public Iterable<Account> findAll() {
        return accountRepository.findAll();
    }

    public Set<Account> findAllByUser(User user) {
        return accountRepository.findAllByUser(user);
    }

    public void saveAccount(Account account) {
        accountRepository.save(account);
    }
}
