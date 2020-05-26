package com.bizmobia.vgwallet.mobile.serviceImpl;

import com.bizmobia.vgwallet.mobile.dao.StatusDao;
import com.bizmobia.vgwallet.mobile.service.StatusService;
import com.bizmobia.vgwallet.models.Status;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Vishnu
 */
@Service
@Transactional
public class StatusServiceImpl implements StatusService {

    @Autowired
    private StatusDao dao;

    @Override
    public Status getStatusByName(String name) {
        Status obj;
        obj = dao.getStatusByName(name);
        if (obj == null) {
            obj = new Status();
            obj.setStatusName(name);
            obj = dao.save(obj);
        }
        return obj;
    }

}
