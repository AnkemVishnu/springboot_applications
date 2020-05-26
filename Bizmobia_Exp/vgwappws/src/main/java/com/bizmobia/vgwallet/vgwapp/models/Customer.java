package com.bizmobia.vgwallet.vgwapp.models;

import com.bizmobia.vgwallet.vgwapp.utilities.DateHandler;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.io.Serializable;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;

/**
 *
 * @author Vaibhav
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@JsonIgnoreProperties({"createdDate"})
@Table(name = "Customer")
public class Customer implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long customerId;

    @Column(name = "fullname")
    private String fullname;

    @Column(name = "cityName")
    private String cityName;

    @Column(name = "email")
    private String email;

    @Column(name = "phoneNumber")
    private String phoneNumber;

    @Column(name = "postalCode")
    private String postalCode;

    @Column(name = "otp")
    private String otp;

    @Column(name = "gender")
    private String gender;

    @Column(name = "street")
    private String street;

    @Column(name = "password")
    private String password;

    @Column(name = "status")
    private Boolean status;

    @Column(name = "createddate", nullable = false, updatable = false)
    @JsonDeserialize(using = DateHandler.class)
    @Temporal(TemporalType.TIMESTAMP)
    @CreatedDate
    private Date createdDate;

}
