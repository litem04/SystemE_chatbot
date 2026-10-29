package com.da.da.repository;


import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.da.da.entity.Customer;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Integer> {

    @Query("SELECT c FROM Customer c WHERE (cast(:keyword as string) IS NULL " +
        "OR LOWER(c.name) LIKE CONCAT('%', LOWER(cast(:keyword as string)), '%') " +
        "OR LOWER(c.email) LIKE CONCAT('%', LOWER(cast(:keyword as string)), '%') " +
        "OR LOWER(c.phone) LIKE CONCAT('%', LOWER(cast(:keyword as string)), '%'))")
    Page<Customer> searchAdminCustomers(@org.springframework.data.repository.query.Param("keyword") String keyword,
                         Pageable pageable);
    // Hàm tìm khách hàng theo email để check trùng
	Customer findByEmail(String email);
    
    @Query("SELECT c FROM Customer c WHERE c.name LIKE %?1% OR c.email LIKE %?1%")
    List<Customer> searchCustomer(String keyword);
}
