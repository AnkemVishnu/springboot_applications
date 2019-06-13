package com.vishnu.model;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "UserFile")
public class UserFile implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer fileId;

    @Column(name = "fileName")
    private String fileName;
    
    @Column(name = "file")
    private String file;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user")
    private Users user;

    @Column(name = "created_on")
    @Temporal(TemporalType.TIMESTAMP)
    @CreatedDate
    Date created_on;

}
