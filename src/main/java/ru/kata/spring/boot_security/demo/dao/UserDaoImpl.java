package ru.kata.spring.boot_security.demo.dao;

import org.springframework.security.core.userdetails.UsernameNotFoundException;
import ru.kata.spring.boot_security.demo.model.User;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import java.util.List;

@Repository
public class UserDaoImpl implements UserDao {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public void saveUser(User user) {
        entityManager.persist(user);
    }

    @Override
    public void updateUser(User user) {
        if (entityManager.find(User.class, user.getId()) == null) {
            throw new EntityNotFoundException("User with id " + user.getId() + " not found");
        }
        entityManager.merge(user);
    }

    @Override
    public void deleteUser(long id) {
        User someUser = entityManager.find(User.class, id);
        if (someUser == null) {
            throw new EntityNotFoundException("User with id " + id + " not found");
        }
        entityManager.remove(someUser);
    }

    @Override
    public List<User> getAllUsers() {
        return entityManager.createQuery("select u from User u", User.class).getResultList();
    }

    @Override
    public User getUserByLogin(String login) {
        try {
            return entityManager.createQuery("SELECT u FROM User u WHERE u.login = :username", User.class)
                    .setParameter("username", login)
                    .getSingleResult();
        } catch (NoResultException e) {
            throw new UsernameNotFoundException("User with login '" + login + "' not found");
        }
    }

    @Override
    public User getUserById(long id) {
        User user = entityManager.find(User.class, id);
        if (user == null) {
            throw new EntityNotFoundException("User with id " + id + " not found");
        }
        return user;
    }

    @Override
    public boolean isLoginAvailable(String login) {
        try {
            getUserByLogin(login);
            return false;
        } catch (UsernameNotFoundException e) {
            return true;
        }
    }
}
