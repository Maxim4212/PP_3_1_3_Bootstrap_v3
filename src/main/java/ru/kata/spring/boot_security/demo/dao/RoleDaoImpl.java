package ru.kata.spring.boot_security.demo.dao;

import ru.kata.spring.boot_security.demo.model.Role;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import java.util.List;

@Repository
public class RoleDaoImpl implements RoleDao {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<Role> getAllRoles() {
        return entityManager.createQuery("select r from Role r", Role.class).getResultList();
    }

    @Override
    public Role getRole(String userRole) {
        try {
            return entityManager.createQuery("select r from Role r where r.userRole =: userRole", Role.class)
                    .setParameter("userRole", userRole)
                    .getSingleResult();
        } catch (NoResultException e) {
            throw new EntityNotFoundException("Role with name '" + userRole + "' not found");
        }
    }

    @Override
    public Role getRoleById(Long id) {
        Role role = entityManager.find(Role.class, id);
        if (role == null) {
            throw new EntityNotFoundException("Role with id " + id + " not found");
        }
        return role;
    }

    @Override
    public void addRole(Role role) {
        try {
            Role existingRole = getRole(role.getUserRole());
            if (existingRole != null) {
                throw new IllegalArgumentException("Role '" + role.getUserRole() + "' already exists");
            }
        } catch (EntityNotFoundException e) {
        }
        entityManager.persist(role);
    }
}
