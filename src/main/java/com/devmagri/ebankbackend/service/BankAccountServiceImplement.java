package com.devmagri.ebankbackend.service;

import com.devmagri.ebankbackend.dto.*;
import com.devmagri.ebankbackend.entities.*;
import com.devmagri.ebankbackend.enums.AccountStatus;
import com.devmagri.ebankbackend.enums.OperationType;
import com.devmagri.ebankbackend.exceptions.BalanceNotSufficientException;
import com.devmagri.ebankbackend.exceptions.BankAccountNotFouncException;
import com.devmagri.ebankbackend.exceptions.CustomerNotFoundException;
import com.devmagri.ebankbackend.mappers.BankAccountMapperImpl;
import com.devmagri.ebankbackend.repository.AccountOperationRepository;
import com.devmagri.ebankbackend.repository.BankAccountRepository;
import com.devmagri.ebankbackend.repository.CustomerRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
@AllArgsConstructor
@Slf4j
public class BankAccountServiceImplement implements BankAccountService{
    private CustomerRepository customerRepository;
    private BankAccountRepository bankAccountRepository;
    private AccountOperationRepository accountOperationRepository;
    private BankAccountMapperImpl dtoMapper;
    @Override
    public CustomerDTO saveCustomer(CustomerDTO customerDTO) {
        log.info("saving new customer");
        Customer customer = dtoMapper.fromCustomerDTO(customerDTO);
        Customer savedCustomer = customerRepository.save(customer);
        return dtoMapper.fromCustomer(savedCustomer);
    }

    @Override
    public CurrentBankAccountDTO saveCurrentBankAccount(double initialBalance, double overDraft, Long customerId) throws CustomerNotFoundException {
        Customer customer = customerRepository.findById(customerId).orElse(null);
        if(customer==null){
            throw new CustomerNotFoundException("customer not found");
        }
        CurrentAccount currentAccount = new CurrentAccount();
        currentAccount.setId(UUID.randomUUID().toString());
        currentAccount.setBalance(initialBalance);
        currentAccount.setCreatedAt(new Date());
        currentAccount.setCustomer(customer);
        currentAccount.setStatus(AccountStatus.CREATED);
        currentAccount.setOverDraft(overDraft);

        CurrentAccount savedBankAccount = bankAccountRepository.save(currentAccount);
        return dtoMapper.fromCurrentBankAccount(savedBankAccount);

    }

    @Override
    public SavingBankAccountDTO saveSavingBankAccount(double initialBalance, double interestRate, Long customerId) throws CustomerNotFoundException {
        Customer customer = customerRepository.findById(customerId).orElse(null);
        if(customer==null){
            throw new CustomerNotFoundException("customer not found");
        }
        SavingAccount savingAccount = new SavingAccount();
        savingAccount.setId(UUID.randomUUID().toString());
        savingAccount.setBalance(initialBalance);
        savingAccount.setCreatedAt(new Date());
        savingAccount.setCustomer(customer);
        savingAccount.setStatus(AccountStatus.CREATED);
        savingAccount.setInterestRate(interestRate);

        SavingAccount savedBankAccount = bankAccountRepository.save(savingAccount);
        return dtoMapper.fromSavingBankAccount(savedBankAccount);
    }


    @Override
    public List<CustomerDTO> listCustomers() {
        List<Customer> customers = customerRepository.findAll();
        List<CustomerDTO> customerDTOS = customers.stream().
                map(customer -> dtoMapper.fromCustomer(customer)).
                collect(Collectors.toList());
        return customerDTOS;
    }

    @Override
    public BankAccountDTO getBankAccount(String accountId) throws BankAccountNotFouncException {
        BankAccount bankAccount = bankAccountRepository.findById(accountId).orElseThrow(()->
                new BankAccountNotFouncException("bank account not found"));
        if(bankAccount instanceof CurrentAccount){
            CurrentAccount currentAccount = (CurrentAccount) bankAccount;
            return dtoMapper.fromCurrentBankAccount(currentAccount);

        }
        else {
            SavingAccount savingAccount = (SavingAccount) bankAccount;
            return dtoMapper.fromSavingBankAccount(savingAccount);
        }
    }

    @Override
    public void debit(String accountId, double amount, String description) throws BankAccountNotFouncException, BalanceNotSufficientException {
        BankAccount bankAccount = bankAccountRepository.findById(accountId).orElseThrow(()->
                new BankAccountNotFouncException("Bank account not found"));
        if(bankAccount.getBalance()<amount){
            throw new BalanceNotSufficientException("balance not sufficient");
        }
        else{
            AccountOperation accountOperation = new AccountOperation();
            accountOperation.setBankAccount(bankAccount);
            accountOperation.setType(OperationType.DEBIT);
            accountOperation.setAmount(amount);
            accountOperation.setOperationDate(new Date());
            accountOperation.setDescription(description);
            accountOperationRepository.save(accountOperation);
            bankAccount.setBalance(bankAccount.getBalance()-amount);
            bankAccountRepository.save(bankAccount);
        }
    }

    @Override
    public void credit(String accountId, double amount, String description) throws BankAccountNotFouncException {
        BankAccount bankAccount = bankAccountRepository.findById(accountId).orElseThrow(()->
                new BankAccountNotFouncException("Bank Account not found"));
        AccountOperation accountOperation = new AccountOperation();
        accountOperation.setBankAccount(bankAccount);
        accountOperation.setType(OperationType.CREDIT);
        accountOperation.setAmount(amount);
        accountOperation.setOperationDate(new Date());
        accountOperation.setDescription(description);
        accountOperationRepository.save(accountOperation);
        bankAccount.setBalance(bankAccount.getBalance()+amount);
        bankAccountRepository.save(bankAccount);
    }

    @Override
    public void transfer(String accountIdSource, String accountIdDestination, double amount) throws BankAccountNotFouncException, BalanceNotSufficientException {
        //BankAccount bankAccountSource = getBankAccount(accountIdSource);
        //BankAccount bankAccountDest = getBankAccount(accountIdDestination);
        // two operations : debit --> sourceAccount | credit --> destAccount
        debit(accountIdSource,amount, "desc");
        credit(accountIdDestination, amount, "desc");

    }

    @Override
    public List<BankAccountDTO> listBankAccounts() {
        List<BankAccount> bankAccounts = bankAccountRepository.findAll();
        List<BankAccountDTO> bankAccountDTOS = bankAccounts.stream().map(bankAccount -> {
            if(bankAccount instanceof CurrentAccount){
                CurrentAccount currentAccount = (CurrentAccount) bankAccount;
                return dtoMapper.fromCurrentBankAccount(currentAccount);
            }
            else{
                SavingAccount savingAccount = (SavingAccount) bankAccount;
                return dtoMapper.fromSavingBankAccount(savingAccount);
            }
        }).collect(Collectors.toList());
        return bankAccountDTOS;
    }

    @Override
    public CustomerDTO getCustomer(Long customerId) throws CustomerNotFoundException {
        Customer customer = customerRepository.findById(customerId).orElse(null);
        if(customer == null){
            throw new CustomerNotFoundException("customer not found");
        }
        CustomerDTO customerDTO = dtoMapper.fromCustomer(customer);
        return customerDTO;
    }

    @Override
    public void deleteCustomer(Long customerId) throws CustomerNotFoundException {
        CustomerDTO customerDTO = getCustomer(customerId);
        Customer customer = dtoMapper.fromCustomerDTO(customerDTO);
        customerRepository.delete(customer);
    }

    @Override
    public void modifyCustomer(Long customerId, CustomerDTO newCustomerDTO) throws CustomerNotFoundException {
        CustomerDTO oldCustomerDTO = getCustomer(customerId);
        oldCustomerDTO.setName(newCustomerDTO.getName());
        Customer newCustomer = dtoMapper.fromCustomerDTO(oldCustomerDTO);
        customerRepository.save(newCustomer);
    }


    @Override
    public List<AccountOperationDTO> accountHistory(String accountId){
        List<AccountOperation> accountOperations =  accountOperationRepository.findByBankAccountId(accountId);
        List<AccountOperationDTO> accountOperationDTOS = accountOperations.stream().map(acc ->
             dtoMapper.fromAccountOperation(acc)
        ).collect(Collectors.toList());
        return accountOperationDTOS;
    }

    @Override
    public AccountHistoryDTO getAccountHistory(String accountId, int page, int size) throws BankAccountNotFouncException {
        BankAccount bankAccount = bankAccountRepository.findById(accountId).orElse(null);
        if(bankAccount==null) throw new BankAccountNotFouncException("Account not found");
        Page<AccountOperation> accountOperations = accountOperationRepository.findByBankAccountId(accountId, PageRequest.of(page, size));
        AccountHistoryDTO accountHistoryDTO = new AccountHistoryDTO();
        List<AccountOperationDTO> accountOperationDTOS =  accountOperations.getContent().stream().map(operation ->
                dtoMapper.fromAccountOperation(operation)).collect(Collectors.toList());
        accountHistoryDTO.setAccountOperationDTOS(accountOperationDTOS);
        accountHistoryDTO.setBalance(bankAccount.getBalance());
        accountHistoryDTO.setAccountId(bankAccount.getId());
        accountHistoryDTO.setCurrentPage(page);
        accountHistoryDTO.setPageSize(size);
        accountHistoryDTO.setTotalPages(accountOperations.getTotalPages());

        return accountHistoryDTO;
    }
}

