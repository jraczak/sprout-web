package io.sproutmoney.sproutweb.data;

//  Created by Justin on 12/11/17

import io.sproutmoney.sproutweb.models.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository("userRepository")
public interface UserRepository extends CrudRepository<User, Long> {
    User findByEmail(String email);
    User findByConfirmationToken(String confirmationToken);
}
