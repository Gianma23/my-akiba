package it.unipi.myakiba.DTO;

import it.unipi.myakiba.enumerator.MediaStatus;
import lombok.Data;

import java.util.List;

@Data
public class MediaListsDto {
    private List<ListElementDto> plannedList;
    private List<ListElementDto> inProgressList;
    private List<ListElementDto> completedList;
}
