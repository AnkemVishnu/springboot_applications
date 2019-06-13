package com.vishnu.dao;

import com.vishnu.model.UserFile;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserFileDao extends JpaRepository<UserFile, Long> {

    @Query("SELECT file FROM UserFile file WHERE file.user.mobileNumber= :mobile")
    public List<UserFile> getAllUserFiles(@Param("mobile") String mobile);

}
