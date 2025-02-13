package cz.cvut.fel.pm2.TransactionMicroservice.dto;

import cz.cvut.fel.pm2.TransactionMicroservice.entity.IncomeCategory;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class IncomeDTO {
    private int id;
    private float amount;
    private String name;
    private LocalDate transactionDate;
    private IncomeCategory incomeCategory;
}

