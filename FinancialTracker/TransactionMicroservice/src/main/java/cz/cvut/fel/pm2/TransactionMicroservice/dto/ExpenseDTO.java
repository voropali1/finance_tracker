package cz.cvut.fel.pm2.TransactionMicroservice.dto;

import cz.cvut.fel.pm2.TransactionMicroservice.entity.ExpenseCategory;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class ExpenseDTO {
    private int id;
    private float amount;
    private String name;
    private LocalDate transactionDate;
    private ExpenseCategory expenseCategory;
}

