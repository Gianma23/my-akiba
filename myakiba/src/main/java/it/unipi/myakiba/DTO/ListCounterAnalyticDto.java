package it.unipi.myakiba.DTO;

import it.unipi.myakiba.enumerator.MediaProgress;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.List;

@Data
public class ListCounterAnalyticDto {
    @NotBlank
    private MediaProgress listType;

    private List<TopMediaDto> topMedia;

    public void setListType(String mediaType) {
        this.listType = MediaProgress.valueOf(mediaType);
    }
}
