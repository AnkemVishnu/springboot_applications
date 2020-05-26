package com.bizmobia.vgwallet.mobile.dao;

import com.bizmobia.vgwallet.models.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 *
 * @author bizmobia1
 */
@Repository
public interface UsersDao extends JpaRepository<Users, Long> {
    
}
