package com.bizmobia.vgwallet.mobile.dao;

import com.bizmobia.vgwallet.models.Country;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 *
 * @author bizmobia1
 */
@Repository
public interface CountryDao extends JpaRepository<Country, Long> {

}
