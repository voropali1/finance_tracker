package cz.cvut.fel.pm2.UserMicroservice.dao;

import cz.cvut.fel.pm2.UserMicroservice.model.User;
import javax.persistence.NoResultException;
import org.springframework.stereotype.Repository;

@Repository
public class UserDao extends AbstractDao<User>{
    public UserDao() {
        super(User.class);
    }
    public User findByUsername(String username) {
        try {
            return (User) manager.createNamedQuery("User.findByUsername")
                    .setParameter("username", username)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    public User save(User user) {
        persist(user);
        return user;
    }

}