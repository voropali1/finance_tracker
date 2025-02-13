package cz.cvut.fel.pm2.FinanceMicroservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GoalDTO {
    private int id;
    @NotBlank(message = "Name is required")
    private String name;
    @NotNull(message = "Amount is required")
    private float amount;
}
