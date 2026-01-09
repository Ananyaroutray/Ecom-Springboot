package com.java.ecom.repository;

import com.java.ecom.entity.Address;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface AddressRepo extends JpaRepository<Address,Long> {
    Optional<Address> findByIdAndUser_Id(Long id, UUID userId);
}
