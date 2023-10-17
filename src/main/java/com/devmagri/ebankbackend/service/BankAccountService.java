package com.devmagri.ebankbackend.service;

import com.devmagri.ebankbackend.dto.*;
import com.devmagri.ebankbackend.exceptions.BalanceNotSufficientException;
import com.devmagri.ebankbackend.exceptions.BankAccountNotFouncException;
import com.devmagri.ebankbackend.exceptions.CustomerNotFoundException;

import java.util.List;

public interface BankAccountService {
    CustomerDTO saveCustomer(CustomerDTO customerDTO);
    CurrentBankAccountDTO saveCurrentBankAccount(double initialBalance, double overDraft, Long customerId) throws CustomerNotFoundException;
    SavingBankAccountDTO saveSavingBankAccount(double initialBalance, double interestRate, Long customerId) throws CustomerNotFoundException;

    List<CustomerDTO> listCustomers();
    BankAccountDTO getBankAccount(String accountId) throws BankAccountNotFouncException;
    void debit(String accountId, double amount, String description) throws BankAccountNotFouncException, BalanceNotSufficientException;
    void credit(String accountId, double amount, String description) throws BankAccountNotFouncException;
    void transfer(String accountIdSource, String accountIdDestination, double amount) throws BankAccountNotFouncException, BalanceNotSufficientException;

    List<BankAccountDTO> listBankAccounts();
    CustomerDTO getCustomer(Long customerId) throws CustomerNotFoundException;

    void deleteCustomer(Long customerId) throws CustomerNotFoundException;

    void modifyCustomer(Long customerId, CustomerDTO newCustomerDTO) throws CustomerNotFoundException;

    List<AccountOperationDTO> accountHistory(String accountId);

    AccountHistoryDTO getAccountHistory(String accountId, int page, int size) throws BankAccountNotFouncException;
}
