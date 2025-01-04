package it.unipi.myakiba.DTO.media;

import it.unipi.myakiba.DTO.ListElementDto;
import lombok.Data;

import java.util.List;

public record MediaListsDto (
     List<ListElementDto> plannedList,
     List<ListElementDto> inProgressList,
     List<ListElementDto> completedList
) {}
