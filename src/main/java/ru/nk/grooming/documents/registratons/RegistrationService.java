package ru.nk.grooming.documents.registratons;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.nk.grooming.authentication.routes.components.AuthService;
import ru.nk.grooming.components.employees.EmployeeEntity;
import ru.nk.grooming.components.employees.EmployeeRepo;
import ru.nk.grooming.components.products.ProductEntity;
import ru.nk.grooming.components.products.ProductRepo;
import ru.nk.grooming.components.salons.SalonEntity;
import ru.nk.grooming.components.salons.SalonRepo;
import ru.nk.grooming.documents.registratons.dto.RegistrationFullData;
import ru.nk.grooming.documents.registratons.dto.RegistrationRequest;
import ru.nk.grooming.general.requests.ServiceFunctions;
import ru.nk.grooming.types.ResponseWithStatus;
import ru.nk.grooming.types.StatusCode;
import ru.nk.grooming.users.User;
import ru.nk.grooming.users.dto.UserDTO;

import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RegistrationService {
    private final ServiceFunctions functions;
    private final AuthService authService;
    private final RegistrationRepo registrationRepo;
    private final SalonRepo salonRepo;
    private final EmployeeRepo employeeRepo;
    private final ProductRepo productRepo;


    private RegistrationFullData fullData(List<Object[]> entitiesArr) {
        Object[] entities = entitiesArr.get(0);
        RegistrationEntity registration = (RegistrationEntity) entities[0];
        SalonEntity salon = (SalonEntity) entities[1];
        EmployeeEntity employee = (EmployeeEntity) entities[2];
        ProductEntity product = (ProductEntity) entities[3];
        UserDTO user = UserDTO.create((User) entities[4]);

        return RegistrationFullData.builder()
                .id(registration.getId())
                .date(registration.getDate())
                .duration(registration.getDuration())
                .price(registration.getPrice())
                .salon(salon)
                .employee(employee)
                .product(product)
                .user(user)
                .comment(registration.getComment())
                .build();
    }
    public boolean fieldsNotExist(RegistrationEntity registration) {
        EmployeeEntity employee = employeeRepo.findById(registration.getEmployeeId()).orElse(null);
        if (employee == null) {
            return true;
        }
        registration.setSalonId(employee.getSalonId());
        ProductEntity product = productRepo.findById(registration.getProductId()).orElse(null);
        if (product == null) {
            return true;
        }
        registration.setPrice(product.getPrice());
        registration.setDuration(product.getDuration());

        return false;
    }

    public ResponseWithStatus<List<RegistrationEntity>> findAll(HttpServletRequest request) {
        return functions.findAllWithAuth(registrationRepo::findAll, request);
    }
    public ResponseWithStatus<RegistrationFullData> findById(Long id, HttpServletRequest request) {
        return functions.findByWithJoinWithAuth(
                id,
                registrationRepo::findByIdWithJoin,
                this::fullData,
                request
        );
    }
    public ResponseWithStatus<List<RegistrationEntity>> findBySalonId(Long salonId, HttpServletRequest request) {
        return functions.findAllByWithAuth(salonId, registrationRepo::findAllBySalonId, request);
//        return functions.findAllByWithAuth(
//                salonId,
//                registrationRepo::findBySalonIdWithJoin,
//                this::fullData,
//                request
//        );
    }
    public StatusCode save(
            RegistrationRequest registration,
            HttpServletRequest request
    ) {
        registration.setDate(new Date());

        User user = authService.getUserByHttpRequest(request);
        if (user != null) {
            RegistrationEntity registrationEntity = registration.registrationEntity(user.getId());

            return functions.saveWithCheckFields(
                    registrationEntity,
                    this::fieldsNotExist,
                    registrationRepo::save
            );
        }

        return StatusCode.create(403);
    }
    public StatusCode deleteById(Long id, HttpServletRequest request) {
        return functions.deleteByWithAuth(
                id,
                registrationRepo::findById,
                registrationRepo::deleteById,
                request
        );
    }
}
