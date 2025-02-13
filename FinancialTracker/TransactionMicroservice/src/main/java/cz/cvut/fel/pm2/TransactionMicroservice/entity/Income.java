package cz.cvut.fel.pm2.TransactionMicroservice.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name="income")
public class Income extends Transaction{

    @ManyToOne
    @JoinColumn(name = "income_category_id", nullable = false)
    private IncomeCategory incomeCategory;
}

