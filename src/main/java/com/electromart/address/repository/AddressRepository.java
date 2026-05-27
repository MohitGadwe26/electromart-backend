package com.electromart.address.repository;

import com.electromart.address.entity.Address;
import com.electromart.common.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AddressRepository extends JpaRepository<Address, Long> {
    
    List<Address> findByUserAndDeletedFalse(User user);
    
    Optional<Address> findByIdAndUserAndDeletedFalse(Long id, User user);
    
    Optional<Address> findByUserAndIsDefaultTrueAndDeletedFalse(User user);
    
    @Query("SELECT COUNT(a) FROM Address a WHERE a.user = :user AND a.deleted = false")
    long countByUser(@Param("user") User user);
    
    @Modifying
    @Query("UPDATE Address a SET a.isDefault = false WHERE a.user = :user AND a.isDefault = true")
    void unsetDefaultAddress(@Param("user") User user);
    
    @Query("SELECT a FROM Address a WHERE a.user = :user AND a.deleted = false ORDER BY a.isDefault DESC, a.updatedAt DESC")
    List<Address> findAllActiveByUser(@Param("user") User user);
    
    boolean existsByUserAndIdAndDeletedFalse(User user, Long id);
}