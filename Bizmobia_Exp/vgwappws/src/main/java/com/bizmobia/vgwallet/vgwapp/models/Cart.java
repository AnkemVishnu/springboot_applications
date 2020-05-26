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
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
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
@Table(name = "Cart")
public class Cart implements Serializable{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long cartId;

    @ElementCollection
    @CollectionTable(name = "Cart_CartItems", joinColumns = @JoinColumn(name = "cartId"))
    @Column(name = "listCartItems")
    private List<CartItems> listCartItems;

    @Column(name = "cartTotal")
    private Double cartTotal;

    @Column(name = "createddate")
    @JsonDeserialize(using = DateHandler.class)
    @Temporal(TemporalType.TIMESTAMP)
    @CreatedDate
    private Date createdDate;

}
