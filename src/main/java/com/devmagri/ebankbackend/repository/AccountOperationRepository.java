package com.devmagri.ebankbackend.repository;

import com.devmagri.ebankbackend.entities.AccountOperation;
import com.devmagri.ebankbackend.entities.BankAccount;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AccountOperationRepository extends JpaRepository<AccountOperation, Long> {

    List<AccountOperation> findByBankAccountId(String accoundId);

    Page<AccountOperation> findByBankAccountId(String accoundId, Pageable pageable);
}
