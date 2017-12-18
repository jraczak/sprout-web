package io.sproutmoney.sproutweb.data;

//  Created by Justin on 12/16/17

import io.sproutmoney.sproutweb.models.PlaidItem;
import io.sproutmoney.sproutweb.models.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository("plaidItemRepository")
public interface PlaidItemRepository extends CrudRepository<PlaidItem, Long> {
    PlaidItem findByAccessToken(String accessToken);
    Set<PlaidItem> findAllByUser(User user);
}
