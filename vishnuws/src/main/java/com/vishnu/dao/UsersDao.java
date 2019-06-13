package com.vishnu.dao;

import com.vishnu.model.Users;
import com.vishnu.requestmodel.LoginRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UsersDao extends JpaRepository<Users, Long> {

    @Query("SELECT user FROM Users user WHERE user.mobileNumber= :#{#request.username} AND user.password= :#{#request.password}")
    public Users validateUser(@Param("request") LoginRequest request);
    
    @Query("SELECT user FROM Users user WHERE user.mobileNumber= :mobile")
    public Users getUserByMobile(@Param("mobile") String mobile);
    
}
