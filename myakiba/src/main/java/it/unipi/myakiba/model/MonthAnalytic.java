package it.unipi.myakiba.model;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "month_analytics")
@Data
public class MonthAnalytic {
    @Id
    private String id;

    @Indexed(unique = true)
    private int year;

    @NotBlank
    private int month;

    private int count = 0;
}