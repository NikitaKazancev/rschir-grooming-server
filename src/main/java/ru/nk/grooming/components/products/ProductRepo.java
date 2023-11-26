package ru.nk.grooming.components.products;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProductRepo extends JpaRepository<ProductEntity, Long> {
    Optional<ProductEntity> findByName(String name);
}
