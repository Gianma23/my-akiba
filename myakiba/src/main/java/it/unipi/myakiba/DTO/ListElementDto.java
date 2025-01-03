package it.unipi.myakiba.DTO;

import lombok.Data;

@Data
public class ListElementDto {
    String id;
    String name;
    int progress;
    int total;
}
