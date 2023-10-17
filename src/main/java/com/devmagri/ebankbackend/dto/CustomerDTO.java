package com.devmagri.ebankbackend.dto;

import com.devmagri.ebankbackend.entities.BankAccount;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
public class CustomerDTO {
    private Long id;
    private String name;
    private String email;
}
