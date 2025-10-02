package org.azurecloud.solutions.pusa.service;

import org.azurecloud.solutions.pusa.domain.User;
import org.azurecloud.solutions.pusa.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RefreshScope
public class UserService {

    private final UserRepository userRepository;

    @Value("${user.password.min-length}")
    private int minPasswordLength;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public List<User> findAll() {
        return userRepository.findAll();
    }

    @Transactional
    public User createUser(User user) {
        if (user.getPassword().length() < minPasswordLength) {
            throw new IllegalArgumentException("Password is too short! Minimum length is " + minPasswordLength);
        }
        return userRepository.save(user);
    }
}
