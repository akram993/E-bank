package com.devmagri.ebankbackend.dto;


import com.devmagri.ebankbackend.enums.AccountStatus;
import lombok.Data;
import java.util.Date;
import java.util.List;

@Data
public class SavingBankAccountDTO extends  BankAccountDTO {
    private String id;
    private double balance;
    private Date createdAt;
    private AccountStatus status;
    private CustomerDTO customerDTO;
    private double interestRate;
}
