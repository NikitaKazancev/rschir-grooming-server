package ru.nk.grooming.admin;

import jakarta.servlet.http.HttpServletRequest;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.nk.grooming.authentication.routes.dto.AuthResponseDTO;
import ru.nk.grooming.types.StatusCode;
import ru.nk.grooming.users.User;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {
    private final AdminService adminService;

    @GetMapping
    public ResponseEntity<StatusCode> home(
            @NonNull HttpServletRequest request
    ) {
        StatusCode statusCode = adminService.home(request);
        return ResponseEntity.status(statusCode.getStatus()).body(statusCode);
    }
}
