package it.unipi.myakiba.DTO.analytic;

import it.unipi.myakiba.DTO.user.UserIdUsernameDto;
import lombok.Data;

import java.util.List;

@Data
public class SCCAnalyticDto {
    private int componentId;
    private int componentSize;
    List<UserIdUsernameDto> userDetails;
}