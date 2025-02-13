package cz.cvut.fel.pm2.FinanceMicroservice.entity;

import java.time.LocalDate;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name="debt")
public class Debt extends Finance {
    @Column(name = "nameOfPersonToGiveBack", nullable = false)
    private String nameOfPersonToGiveBack;
    @Column(name = "from_date", nullable = false)
    private LocalDate fromDate;
    @Column(name = "due_date", nullable = false)
    private LocalDate dueDate;
    @Column(name= "interest_rate", nullable = false)
    private int interestRate;
}