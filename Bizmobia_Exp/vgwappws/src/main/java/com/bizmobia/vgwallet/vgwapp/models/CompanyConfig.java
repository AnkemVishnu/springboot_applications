package com.bizmobia.vgwallet.vgwapp.models;

import com.bizmobia.vgwallet.vgwapp.utilities.DateHandler;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.io.Serializable;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
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
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

/**
 *
 * @author bizmobia1
 */
@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "CompanyConfig")
@EntityListeners(AuditingEntityListener.class)
@JsonIgnoreProperties(value = {"created_on"})
public class CompanyConfig implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long companyConfigId;

    @Column(name = "idproofpath")
    private String idProofPath;

    @Column(name = "idproofurl")
    private String idProofUrl;

    @Column(name = "addressproofpath")
    private String addressProofPath;

    @Column(name = "addressproofurl")
    private String addressProofUrl;

    @Column(name = "noofattempts")
    private Integer noOfAttempts;

    @Column(name = "otplength")
    private Integer otpLength;

    @Column(name = "maxwrongattempt")
    private Integer maxWrongAttempt;

    @Column(name = "created_on", nullable = false, updatable = false)
    @JsonDeserialize(using = DateHandler.class)
    @Temporal(TemporalType.TIMESTAMP)
    @CreatedDate
    private Date createdDate;

}
