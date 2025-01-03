package it.unipi.myakiba.DTO;

import lombok.Data;

import java.util.List;

@Data
public class MediaInListsAnalyticDto {
    private String mediaId;
    private String mediaName;
    private List<AppearancesDto> appearances;
}
