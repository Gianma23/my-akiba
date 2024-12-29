package it.unipi.myakiba.DTO;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.springframework.data.annotation.Id;

@Data
public class MonthAnalyticDTO {
    @Id
    private int year;

    @NotBlank
    private int month;

    private int count = 0;
}