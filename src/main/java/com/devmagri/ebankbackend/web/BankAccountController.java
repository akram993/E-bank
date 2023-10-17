package com.devmagri.ebankbackend.web;

import com.devmagri.ebankbackend.dto.*;
import com.devmagri.ebankbackend.exceptions.BankAccountNotFouncException;
import com.devmagri.ebankbackend.exceptions.CustomerNotFoundException;
import com.devmagri.ebankbackend.service.BankAccountService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@CrossOrigin("*")
public class BankAccountController {
    public BankAccountService bankAccountService;

    @GetMapping("/accounts/{id}")
    public BankAccountDTO getBankAccount(@PathVariable(name = "id") String accountId) throws BankAccountNotFouncException {
        return bankAccountService.getBankAccount(accountId);
    }

    @GetMapping("/accounts")
    public List<BankAccountDTO> listAccounts(){
        return bankAccountService.listBankAccounts();
    }

    @PostMapping("/bankAccounts/current")
    public CurrentBankAccountDTO saveCurrentBankAccount(@RequestBody CurrentBankAccountDTO currentBankAccountDTO) throws CustomerNotFoundException {
        return bankAccountService.saveCurrentBankAccount(currentBankAccountDTO.getBalance(),
                currentBankAccountDTO.getOverDraft(),
                currentBankAccountDTO.getCustomerDTO().getId());
    }

    @PostMapping("/bankAccounts/saving")
    public SavingBankAccountDTO saveSavingBankAccount(@RequestBody SavingBankAccountDTO savingBankAccountDTO) throws CustomerNotFoundException {
        return bankAccountService.saveSavingBankAccount(savingBankAccountDTO.getBalance(),
                savingBankAccountDTO.getInterestRate(),
                savingBankAccountDTO.getCustomerDTO().getId());
    }

    @GetMapping("/accounts/{id}/operations")
    public List<AccountOperationDTO> getHistory(@PathVariable(name = "id") String accountId){
        return bankAccountService.accountHistory(accountId);
    }

    @GetMapping("/accounts/{id}/pageOperations/")
    public AccountHistoryDTO getAccountHistory(@PathVariable(name = "id") String accountId,
                                               @RequestParam(name = "page", defaultValue = "0") int page,
                                               @RequestParam(name = "size", defaultValue = "5") int size) throws BankAccountNotFouncException {
        return bankAccountService.getAccountHistory(accountId, page, size);
    }
}