package it.unipi.myakiba.DTO.analytic;

import it.unipi.myakiba.enumerator.MediaProgress;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.List;

@Data
public class ListCounterAnalyticDto {
    @NotBlank
    private MediaProgress listType;
    private List<TopMediaDto> topMedia;

    public void setListType(String listType) {
        this.listType = MediaProgress.valueOf(listType);
    }
}
