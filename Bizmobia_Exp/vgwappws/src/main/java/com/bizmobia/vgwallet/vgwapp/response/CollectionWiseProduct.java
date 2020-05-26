package com.bizmobia.vgwallet.vgwapp.response;

import com.bizmobia.vgwallet.vgwapp.models.Product;
import com.bizmobia.vgwallet.vgwapp.models.ProductCollections;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *
 * @author Vishnu
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CollectionWiseProduct {

    private ProductCollections collection;
    private List<Product> productList;

}
