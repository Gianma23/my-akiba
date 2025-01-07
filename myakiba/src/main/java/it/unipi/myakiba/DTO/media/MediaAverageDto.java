package it.unipi.myakiba.DTO.media;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import it.unipi.myakiba.enumerator.MediaStatus;
import lombok.Data;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

import java.util.List;

public record MediaAverageDto(
        String id,

        String name,

        double averageScore
) {
}