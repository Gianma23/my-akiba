package it.unipi.myakiba.DTO;

import it.unipi.myakiba.DTO.user.UserIdUsernameDto;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.List;

@Data
public class CliqueAnalyticDto {
    @NotBlank
    private String cliqueId;
    private int cliqueSize;
    List<UserIdUsernameDto> userDetails;
}
