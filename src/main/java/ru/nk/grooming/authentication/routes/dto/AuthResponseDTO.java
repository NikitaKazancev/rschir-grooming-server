package ru.nk.grooming.authentication.routes.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.nk.grooming.users.Role;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthResponseDTO {
    private int status;
    private String jwt;
    private Role role;
}
