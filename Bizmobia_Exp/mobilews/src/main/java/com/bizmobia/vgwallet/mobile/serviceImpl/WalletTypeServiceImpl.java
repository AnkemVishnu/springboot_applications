package com.bizmobia.vgwallet.mobile.serviceImpl;

import com.bizmobia.vgwallet.mobile.dao.WalletTypeDao;
import com.bizmobia.vgwallet.mobile.service.WalletTypeService;
import com.bizmobia.vgwallet.models.Roles;
import com.bizmobia.vgwallet.models.WalletType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Vishnu
 */
@Service
@Transactional
public class WalletTypeServiceImpl implements WalletTypeService {

    @Autowired
    private WalletTypeDao dao;

    @Override
    public WalletType getWalletTypeByName(String name) {
        WalletType obj;
        obj = dao.getWalletTypeByName(name);
        if (obj == null) {
            obj = new WalletType();
            obj.setWalletTypeName(name);
            obj = dao.save(obj);
        }
        return obj;
    }

    @Override
    public WalletType walletTypeByRole(Roles role) {
        String name = null;
        if ("Customer".equals(role.getRoleName())) {
            name = "Classics";
        } else if ("Agent".equals(role.getRoleName())) {
            name = "Agent";
        }
        WalletType obj;
        obj = dao.getWalletTypeByName(name);
        if (obj == null) {
            obj = new WalletType();
            obj.setWalletTypeName(name);
            obj = dao.save(obj);
        }
        return obj;
    }

}
