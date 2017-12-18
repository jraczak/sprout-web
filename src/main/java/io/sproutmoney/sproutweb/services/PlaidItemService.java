package io.sproutmoney.sproutweb.services;

//  Created by Justin on 12/16/17

import io.sproutmoney.sproutweb.data.PlaidItemRepository;
import io.sproutmoney.sproutweb.models.PlaidItem;
import io.sproutmoney.sproutweb.models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service("plaidItemService")
public class PlaidItemService {

    private PlaidItemRepository plaidItemRepository;

    @Autowired
    public PlaidItemService(PlaidItemRepository repository) {
        this.plaidItemRepository = repository;
    }

    public PlaidItem findByAccessToken(String accessToken) {
        return plaidItemRepository.findByAccessToken(accessToken);
    }

    public void savePlaidItem(PlaidItem plaidItem) {
        plaidItemRepository.save(plaidItem);
    }

    public Set<PlaidItem> findAllByUser(User user) {
        return plaidItemRepository.findAllByUser(user);
    }

}
