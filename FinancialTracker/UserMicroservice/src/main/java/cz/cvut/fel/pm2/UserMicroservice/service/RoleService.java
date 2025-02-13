package cz.cvut.fel.pm2.UserMicroservice.service;

import cz.cvut.fel.pm2.UserMicroservice.dao.RoleDao;
import cz.cvut.fel.pm2.UserMicroservice.model.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service(value = "roleService")
public class RoleService {

    @Autowired
    private RoleDao roleDao;

    public Role findByName(String name) {
        Role role = roleDao.findRoleByName(name);
        return role;
    }
}