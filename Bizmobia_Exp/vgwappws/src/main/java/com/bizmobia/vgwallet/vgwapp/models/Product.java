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
import java.util.List;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
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
import org.hibernate.annotations.Type;
import org.springframework.data.annotation.CreatedDate;

/**
 *
 * @author bizmobia27
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@JsonIgnoreProperties({"createdDate"})
@Table(name = "Product")
public class Product implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)  //for auto increament of ids
    private Long productId;

    @Column(name = "productCode")
    private String productCode;

    @Column(name = "productname")
    private String productname;

    @Column(name = "description")
    @Type(type = "text")
    private String description;

    @Column(name = "productIconImage")
    private String productIconImage;

    @ElementCollection
    @CollectionTable(name = "ProductImagesList", joinColumns = @JoinColumn(name = "productId"))
    @Column(name = "productImage")
    private List<String> productImage;

    @Column(name = "price")
    private Double price;

    @Column(name = "lastupdate")
    @Temporal(TemporalType.TIMESTAMP)
    private Date lastupdate;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "category")
    private Category category;

    @Column(name = "releasedate")
    @Temporal(TemporalType.TIMESTAMP)
    private Date releaseDate;

    @Column(name = "createddate")
    @JsonDeserialize(using = DateHandler.class)
    @Temporal(TemporalType.TIMESTAMP)
    @CreatedDate
    private Date createdDate;

}
