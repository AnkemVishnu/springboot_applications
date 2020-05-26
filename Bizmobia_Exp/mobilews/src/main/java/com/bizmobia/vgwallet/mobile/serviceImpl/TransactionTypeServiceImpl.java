package com.bizmobia.vgwallet.mobile.serviceImpl;

import com.bizmobia.vgwallet.mobile.dao.TransactionTypeDao;
import com.bizmobia.vgwallet.mobile.service.TransactionTypeService;
import com.bizmobia.vgwallet.models.TransactionType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Vishnu
 */
@Service
@Transactional
public class TransactionTypeServiceImpl implements TransactionTypeService {

    @Autowired
    private TransactionTypeDao dao;

    @Override
    public TransactionType getTransactionTypeByName(String name) {
        TransactionType obj;
        obj = dao.getTransactionTypeByName(name);
        if (obj == null) {
            obj = new TransactionType();
            obj.setTransactionTypeName(name);
            obj = dao.save(obj);
        }
        return obj;
    }

}
