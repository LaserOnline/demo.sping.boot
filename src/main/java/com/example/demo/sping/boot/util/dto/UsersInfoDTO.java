package com.example.demo.sping.boot.util.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "set users-info")
@Data
public class UsersInfoDTO {
    @Schema(description = "firstName", example = "james")
    private String firstName;
    @Schema(description = "lastName", example = "example")
    private String lastName;
    @Schema(description = "address", example = "69 Sri Ayutthaya Rd., Dusit Subdistrict, Dusit District, Bangkok 10300")
    private String address;
}
