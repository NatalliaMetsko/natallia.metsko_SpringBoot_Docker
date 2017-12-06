package com.netcracker.metsko.dao.implementation;

import com.netcracker.metsko.dao.GenericDao;
import com.netcracker.metsko.databasemanager.DatabaseManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;


import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.PersistenceContext;
import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.sql.SQLException;

@EnableTransactionManagement
public class GenericDaoImpl<T, Long extends Serializable> implements GenericDao<T, Long> {

    @PersistenceContext
    public EntityManager entityManager;

    private Class<T> tClass;

    public GenericDaoImpl() {
        ParameterizedType genericSuperclass = (ParameterizedType) getClass()
                .getGenericSuperclass();
        this.tClass = (Class<T>) genericSuperclass.getActualTypeArguments()[0];
    }

    @Override
    public void create(T newObject) throws SQLException {
        try {
            entityManager.persist(newObject);
        } catch (Exception e) {
            System.err.print("Transaction is being rolled back");

        }
    }

    @Override
    public T update(final T objectToUpdate) throws SQLException {
        try {
                T t = entityManager.merge(objectToUpdate);
                return t;
        }catch (Exception e)
        {
            System.err.print("Transaction is being rolled back\n");
        }
        return objectToUpdate;
    }

    @Override
    public T read(Long id) throws SQLException {
        return this.entityManager.find(tClass, id);
    }

    @Override
    public void delete(Long id) throws SQLException {
        try {
                T t = entityManager.find(tClass, id);
                entityManager.remove(t);
        } catch (Exception e) {
              System.err.println("Transaction is being rolled back\n");
        }
    }

    public void close()
    {
        entityManager.close();
    }
}
