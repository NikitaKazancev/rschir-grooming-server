package ru.nk.grooming.documents.registratons;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.nk.grooming.components.employees.EmployeeEntity;

import java.util.List;

@Repository
public interface RegistrationRepo extends JpaRepository<RegistrationEntity, Long> {
    @Query("select r, s, e, p, u " +
            "from RegistrationEntity r " +
            "left join SalonEntity s on r.salonId = s.id " +
            "left join EmployeeEntity e on r.employeeId = e.id " +
            "left join ProductEntity p on r.productId = p.id " +
            "left join User u on r.userId = u.id " +
            "where r.id = :id")
    List<Object[]> findByIdWithJoin(@Param("id") Long id);
    @Query("select r, s, e, p " +
            "from RegistrationEntity r " +
            "left join SalonEntity s on r.salonId = s.id " +
            "left join EmployeeEntity e on r.employeeId = e.id " +
            "left join ProductEntity p on r.productId = p.id " +
            "where r.userId = :id")
    List<Object[]> findAllByUserIdWithJoinWithoutUser(@Param("id") Long id);
    @Query("select r, s, e, p, u " +
            "from RegistrationEntity r " +
            "left join SalonEntity s on r.salonId = s.id " +
            "left join EmployeeEntity e on r.employeeId = e.id " +
            "left join ProductEntity p on r.productId = p.id " +
            "left join User u on r.userId = u.id " +
            "where s.id = :id")
    List<Object[]> findBySalonIdWithJoin(@Param("id") Long id);
    List<RegistrationEntity> findAllBySalonId(Long salonId);
}
