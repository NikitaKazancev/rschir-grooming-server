package ru.nk.grooming.authentication.routes.components;

import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.nk.grooming.authentication.jwt.JwtService;
import ru.nk.grooming.authentication.routes.dto.AuthRequestDTO;
import ru.nk.grooming.authentication.routes.dto.AuthResponseDTO;
import ru.nk.grooming.authentication.routes.dto.RegisterRequestDTO;
import ru.nk.grooming.components.employees.EmployeeEntity;
import ru.nk.grooming.components.products.ProductEntity;
import ru.nk.grooming.components.salons.SalonEntity;
import ru.nk.grooming.documents.registratons.RegistrationEntity;
import ru.nk.grooming.documents.registratons.RegistrationRepo;
import ru.nk.grooming.documents.registratons.dto.RegistrationFullData;
import ru.nk.grooming.types.ResponseWithStatus;
import ru.nk.grooming.users.Role;
import ru.nk.grooming.users.User;
import ru.nk.grooming.users.UserRepo;
import ru.nk.grooming.users.dto.UserDTO;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class AuthService {
    private final UserRepo userRepo;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authManager;
    private final RegistrationRepo registrationRepo;

    public User getUserByHttpRequest(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return userRepo
                    .findByEmail(jwtService.getEmail(authHeader.substring(7)))
                    .orElse(null);
        }

        return null;
    }
    public boolean isNotAdmin(HttpServletRequest request) {
        User user = getUserByHttpRequest(request);
        return user == null || user.getRole() != Role.ADMIN;
    }
    public AuthResponseDTO register(RegisterRequestDTO requestData) {
        return register(requestData, Role.USER);
    }
    public AuthResponseDTO register(RegisterRequestDTO requestData, Role role) {
        Optional<User> dbUser = userRepo.findByEmail(requestData.getEmail());
        if (dbUser.isPresent()) {
            return AuthResponseDTO.builder().jwt(null).status(409).build();
        }

        User user = User.builder()
                .firstname(requestData.getFirstname())
                .lastname(requestData.getLastname())
                .email(requestData.getEmail())
                .password(passwordEncoder.encode(requestData.getPassword()))
                .role(role)
                .build();

        userRepo.save(user);

        return AuthResponseDTO.builder()
                .jwt(jwtService.createToken(user))
                .status(200)
                .role(role)
                .build();
    }
    public AuthResponseDTO login(AuthRequestDTO authData) {
        authManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        authData.getEmail(),
                        authData.getPassword()
                )
        );

        User user = userRepo.findByEmail(authData.getEmail())
                .orElseThrow();

        return AuthResponseDTO.builder()
                .jwt(jwtService.createToken(user))
                .status(200)
                .role(user.getRole())
                .build();
    }
    private RegistrationFullData getRegistrations(Object[] entities) {
        RegistrationEntity registration = (RegistrationEntity) entities[0];
        SalonEntity salon = (SalonEntity) entities[1];
        EmployeeEntity employee = (EmployeeEntity) entities[2];
        ProductEntity product = (ProductEntity) entities[3];

        return RegistrationFullData.builder()
                .id(registration.getId())
                .date(registration.getDate())
                .duration(registration.getDuration())
                .price(registration.getPrice())
                .salon(salon)
                .employee(employee)
                .product(product)
                .comment(registration.getComment())
                .build();
    }
    public ResponseWithStatus<UserDTO> findUserByJwt(String jwt) {
        User user = userRepo
                .findByEmail(jwtService.getEmail(jwt))
                .orElse(null);
        if (user == null) {
            return ResponseWithStatus.empty(403);
        }

        List<RegistrationFullData> registrations = registrationRepo.
                findAllByUserIdWithJoinWithoutUser(user.getId()).stream()
                .map(this::getRegistrations)
                .toList();
        return ResponseWithStatus.create(200, UserDTO.create(user, registrations));
    }
}





