package com.bizmobia.vgwallet.vgwapp.dao;

import com.bizmobia.vgwallet.vgwapp.models.CrossLogin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Vishnu
 */
@Repository
public interface CrossLoginDao extends JpaRepository<CrossLogin, Long> {

    @Query("select cl from CrossLogin cl where cl.uniqueId= :uniqueId")
    public CrossLogin getCrossLoginByUniqueId(@Param("uniqueId") String uniqueId);
    
    @Query("select cl from CrossLogin cl where cl.uniqueId= :uniqueId and cl.verified= TRUE")
    public CrossLogin crossLoginCheck(@Param("uniqueId") String uniqueId);
    
}
