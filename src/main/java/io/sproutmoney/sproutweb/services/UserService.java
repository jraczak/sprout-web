package io.sproutmoney.sproutweb.services;

//  Created by Justin on 12/11/17

import io.sproutmoney.sproutweb.data.UserRepository;
import io.sproutmoney.sproutweb.models.User;
import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

@Service("userService")
public class UserService implements UserDetailsService {

    private static final String LOG_TAG = UserService.class.getSimpleName();
    private Logger logger = LoggerFactory.getLogger(LOG_TAG);

    private UserRepository userRepository;



    @Autowired
    SessionFactory sessionFactory;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // Must provide email address as username argument
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        logger.info("Inside the loadUserByUsername method");
        logger.info("Finding user with username " + username);
        User user = userRepository.findByEmail(username);
        if (user == null) {
            logger.error("Username was not found with credentials");
            throw new UsernameNotFoundException("There is no user with this email address.");
        }
        logger.info("Returning new user");
        logger.info("Trying to use password " + user.getPassword());
        logger.info("getAuthority is returning " + getAuthority());
        org.springframework.security.core.userdetails.User springUser = new org.springframework.security.core.userdetails.User(
                user.getEmail(), user.getPassword(), getAuthority());
        logger.info("Spring user is "+ springUser);
        return springUser;
    }

    public User findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public User findByConfirmationToken(String confirmationToken) {
        return userRepository.findByConfirmationToken(confirmationToken);
    }

    @Transactional
    public List<User> findAll() {
        Criteria criteria = sessionFactory.openSession().createCriteria(User.class);
        return (List<User>) criteria.list();
    }

    public void saveUser(User user) {
        userRepository.save(user);
    }

    //TODO: Figure out how to use this properly
    public List getAuthority() {
        return Arrays.asList(new SimpleGrantedAuthority("ROLE_USER"));
    }
}
