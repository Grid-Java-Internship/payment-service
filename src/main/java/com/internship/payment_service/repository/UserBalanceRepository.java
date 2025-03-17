package com.internship.payment_service.repository;

import com.internship.payment_service.model.UserBalance;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserBalanceRepository extends JpaRepository<UserBalance, Long> {
    UserBalance findByUserId(@NotNull(message = "User cannot be null!!") Long userId);
}
