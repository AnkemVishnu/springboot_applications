package com.bizmobia.vgwallet.vgwapp.dao;

import com.bizmobia.vgwallet.vgwapp.models.*;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 *
 * @author bizmobia1
 */
@Repository
public interface ProductDao extends JpaRepository<Product, Long> {

    @Query("select p from Product p where p.productCode= :productcode")
    public Product checkDuplicatePcode(@Param("productcode") String productcode);
    
    @Query("select p from Product p where p.category.productCollectionsObj.productCollectionsId= :#{#collection.productCollectionsId}")
    public List<Product> getAllProductCollectionWise(@Param("collection") ProductCollections collection);
    
}
