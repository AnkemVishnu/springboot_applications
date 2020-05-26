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
public interface RolesDao extends JpaRepository<Roles, Long> {

    @Query("select s from Roles s where s.roleName= :name")
    public Roles getRolesByName(@Param("name") String name);
    
}
