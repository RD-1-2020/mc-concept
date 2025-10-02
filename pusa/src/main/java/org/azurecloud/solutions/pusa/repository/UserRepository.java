package org.azurecloud.solutions.pusa.repository;

import org.azurecloud.solutions.pusa.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
