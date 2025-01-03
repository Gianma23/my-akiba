package it.unipi.myakiba.DTO;

import it.unipi.myakiba.enumerator.MediaProgress;
import lombok.Data;

@Data
public class AppearancesDto {
    private MediaProgress listType;
    private int listCount;

    public void setListType(String listType) {
        this.listType = MediaProgress.valueOf(listType);
    }
}
