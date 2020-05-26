package com.bizmobia.vgwallet.vgwapp.dao;

import com.bizmobia.vgwallet.vgwapp.models.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 *
 * @author bizmobia1
 */
@Repository
public interface CustomerDao extends JpaRepository<Customer, Long> {

    @Query("SELECT count(c) FROM Customer c WHERE c.email = :#{#cust.email} OR c.phoneNumber = :#{#cust.phoneNumber} OR c.customerId= :#{#cust.customerId}")
    public Long getAllCustomersByMobileNo(@Param("cust") Customer customerObj);

    @Query("SELECT c FROM Customer c WHERE c.email = :#{#cust.email} OR c.phoneNumber = :#{#cust.phoneNumber} OR c.customerId= :#{#cust.customerId}")
    public Customer getCustomerByMobileNo(@Param("cust") Customer customers);

    @Query("SELECT c FROM Customer c WHERE (c.phoneNumber = :#{#cust.phoneNumber} OR c.email = :#{#cust.email})")
    public Customer checkUserChredentialForLogin(@Param("cust") Customer customers);

}
