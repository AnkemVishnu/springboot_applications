package com.bizmobia.vgwallet.mobile.dao;

import com.bizmobia.vgwallet.models.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 *
 * @author bizmobia1
 */
@Repository
public interface StatusDao extends JpaRepository<Status, Long> {

    @Query("select s from Status s where s.statusName= :name")
    public Status getStatusByName(@Param("name") String name);
    
}
