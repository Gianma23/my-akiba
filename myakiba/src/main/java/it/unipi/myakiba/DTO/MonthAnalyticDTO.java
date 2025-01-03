package it.unipi.myakiba.DTO;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;

@Data
@Builder
public class MonthAnalyticDTO {
    @Id
    private int year;

    @NotBlank
    private int month;

    @Builder.Default
    private int count = 0;
}