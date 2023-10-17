package com.devmagri.ebankbackend.web;

import com.devmagri.ebankbackend.dto.CustomerDTO;
import com.devmagri.ebankbackend.entities.AccountOperation;
import com.devmagri.ebankbackend.entities.BankAccount;
import com.devmagri.ebankbackend.entities.Customer;
import com.devmagri.ebankbackend.exceptions.BankAccountNotFouncException;
import com.devmagri.ebankbackend.exceptions.CustomerNotFoundException;
import com.devmagri.ebankbackend.service.BankAccountService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@Slf4j
@CrossOrigin("*")
public class CustomerController {
    private BankAccountService bankAccountService;

    @GetMapping("/customers")
    public List<CustomerDTO> customers(){
        return bankAccountService.listCustomers();
    }

    @GetMapping("/customers/{id}")
    public CustomerDTO getCustomer(@PathVariable(name = "id")  Long customerId) throws CustomerNotFoundException {
        return bankAccountService.getCustomer(customerId);
    }

    @PostMapping("/customers")
    public CustomerDTO saveCustomer(@RequestBody CustomerDTO customerDTO){
        return bankAccountService.saveCustomer(customerDTO);
    }

    @DeleteMapping("/customers/{id}")
    public void deleteCustomer(@PathVariable(name = "id") Long customerId) throws CustomerNotFoundException {
        bankAccountService.deleteCustomer(customerId);
    }

    @PutMapping("/customers/{id}")
    public void modifyCustomer(@PathVariable(name = "id") Long id,
                               @RequestBody CustomerDTO customerDTO) throws CustomerNotFoundException {
        bankAccountService.modifyCustomer(id, customerDTO);
    }
}
