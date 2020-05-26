package com.bizmobia.vgwallet.vgwapp.dao;

import com.bizmobia.vgwallet.vgwapp.models.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 *
 * @author bizmobia1
 */
@Repository
public interface AdminRolesDao extends JpaRepository<AdminRoles, Long> {

}
