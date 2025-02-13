package cz.cvut.fel.pm2.FinanceMicroservice.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name="goal")
public class Goal extends Finance {

}
