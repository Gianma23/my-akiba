package it.unipi.myakiba.DTO;

public class UserLoginResponseDto {
    private final String accessToken;

    public UserLoginResponseDto(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getAccessToken() {
        return accessToken;
    }
}
