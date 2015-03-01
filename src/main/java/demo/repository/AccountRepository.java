package demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import demo.domain.Account;

import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, Long> {
    Optional<Account> findByUsername(String username);
}
