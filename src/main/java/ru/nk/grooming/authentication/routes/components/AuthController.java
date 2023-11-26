package ru.nk.grooming.authentication.routes.components;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.nk.grooming.authentication.routes.dto.AuthRequestDTO;
import ru.nk.grooming.authentication.routes.dto.AuthResponseDTO;
import ru.nk.grooming.authentication.routes.dto.RegisterRequestDTO;
import ru.nk.grooming.types.ResponseWithStatus;
import ru.nk.grooming.users.User;
import ru.nk.grooming.users.dto.UserDTO;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponseDTO> register(
            @RequestBody RegisterRequestDTO registerData
    ) {
        AuthResponseDTO response = authService.register(registerData);
        return ResponseEntity.status(response.getStatus()).body(response);
    }
    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> login(
            @RequestBody AuthRequestDTO authData
    ) {
        return ResponseEntity.ok(authService.login(authData));
    }
    @GetMapping(value = "/users", params = {"jwt"})
    public ResponseEntity<ResponseWithStatus<UserDTO>> findUserByJwt(@RequestParam String jwt) {
        ResponseWithStatus<UserDTO> response =  authService.findUserByJwt(jwt);
        return ResponseEntity.status(response.getStatus()).body(response);
    }
}
