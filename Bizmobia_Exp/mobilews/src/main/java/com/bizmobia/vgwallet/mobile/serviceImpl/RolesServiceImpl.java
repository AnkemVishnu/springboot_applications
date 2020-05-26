package com.bizmobia.vgwallet.mobile.serviceImpl;

import com.bizmobia.vgwallet.mobile.dao.RolesDao;
import com.bizmobia.vgwallet.mobile.service.RolesService;
import com.bizmobia.vgwallet.models.Roles;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Vishnu
 */
@Service
@Transactional
public class RolesServiceImpl implements RolesService {

    @Autowired
    private RolesDao dao;

    @Override
    public Roles getRolesByName(String name) {
        Roles role;
        role = dao.getRolesByName(name);
        if (role == null) {
            role = new Roles();
            role.setRoleName(name);
            role = dao.save(role);
        }
        return role;
    }

}
