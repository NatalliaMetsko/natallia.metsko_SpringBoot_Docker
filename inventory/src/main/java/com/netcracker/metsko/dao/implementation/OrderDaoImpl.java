package com.netcracker.metsko.dao.implementation;

import com.netcracker.metsko.dao.OrderDao;
import com.netcracker.metsko.entity.Order;
import com.netcracker.metsko.entity.OrderItem;
import com.sun.xml.internal.bind.v2.model.core.ID;

import java.sql.SQLException;
import java.util.List;

public class OrderDaoImpl extends GenericDaoImpl<Order, Long> implements OrderDao {

    @Override
    public List<Order> findAll() {
        return entityManager.createQuery("SELECT * FROM _order").getResultList();
    }

    @Override
    public List<OrderItem> findOrderItemByOrder(Order order) {
        return null;
    }

}
