package cz.cvut.fel.pm2.UserMicroservice.dao;


import cz.cvut.fel.pm2.UserMicroservice.model.Role;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleDao extends CrudRepository<Role, Long>{

    Role findRoleByName(String name);
}