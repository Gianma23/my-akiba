package it.unipi.myakiba.DTO.media;

import it.unipi.myakiba.DTO.ListElementDto;
import lombok.Data;

import java.util.List;

@Data
public class MediaListsDto {
    private List<ListElementDto> plannedList;
    private List<ListElementDto> inProgressList;
    private List<ListElementDto> completedList;
}
