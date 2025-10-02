package org.azurecloud.solutions.pusa.service;

import org.azurecloud.solutions.pusa.config.PasswordPolicyProperties;
import org.azurecloud.solutions.pusa.domain.User;
import org.azurecloud.solutions.pusa.repository.UserRepository;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordPolicyProperties passwordPolicy;

    public UserService(UserRepository userRepository, PasswordPolicyProperties passwordPolicy) {
        this.userRepository = userRepository;
        this.passwordPolicy = passwordPolicy;
    }

    public List<User> findAll() {
        return userRepository.findAll();
    }

    public User createUser(User user) {
        validatePassword(user.getPassword());
        return userRepository.save(user);
    }

    private void validatePassword(String password) {
        if (password == null || password.length() < passwordPolicy.getMinLength()) {
            throw new IllegalArgumentException("Password must be at least " + passwordPolicy.getMinLength() + " characters long.");
        }
        if (passwordPolicy.isRequireUppercase() && !containsUppercase(password)) {
            throw new IllegalArgumentException("Password must contain at least one uppercase letter.");
        }
        if (passwordPolicy.isRequireLowercase() && !containsLowercase(password)) {
            throw new IllegalArgumentException("Password must contain at least one lowercase letter.");
        }
        if (passwordPolicy.isRequireNumbers() && !containsDigit(password)) {
            throw new IllegalArgumentException("Password must contain at least one number.");
        }
        if (passwordPolicy.isRequireSpecial() && !containsSpecialCharacter(password)) {
            throw new IllegalArgumentException("Password must contain at least one special character.");
        }
    }

    private boolean containsUppercase(String s) {
        return !s.equals(s.toLowerCase());
    }

    private boolean containsLowercase(String s) {
        return !s.equals(s.toUpperCase());
    }

    private boolean containsDigit(String s) {
        return s.matches(".*\\d.*");
    }

    private boolean containsSpecialCharacter(String s) {
        Pattern p = Pattern.compile("[^a-z0-9 ]", Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher(s);
        return m.find();
    }
}
