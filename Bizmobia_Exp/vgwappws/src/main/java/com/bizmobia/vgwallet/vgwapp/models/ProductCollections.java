/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
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
import javax.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;

/**
 *
 * @author BizMobia23
 */
@Entity //automatic creation of table
@Table(name = "ProductCollections", uniqueConstraints = @UniqueConstraint(columnNames = {"productcollectionname"}))
@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties({"createdDate"})
public class ProductCollections implements Serializable {

    private static final long serialVersionUID = 041234567124L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)  //for auto increament of ids
    private Long productCollectionsId;

    @Column(name = "productcollectionname")
    private String productCollectionName;

    @Column(name = "pcimageurl")
    private String pcImageUrl;

    @Column(name = "createddate")
    @JsonDeserialize(using = DateHandler.class)
    @Temporal(TemporalType.TIMESTAMP)
    @CreatedDate
    private Date createdDate;

}
