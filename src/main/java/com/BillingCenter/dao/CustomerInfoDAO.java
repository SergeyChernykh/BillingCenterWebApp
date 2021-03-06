package com.BillingCenter.dao;

import com.BillingCenter.model.CustomerInfo;
import com.BillingCenter.utils.HibernateSessionFactory;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class CustomerInfoDAO {
    @Autowired
    private SessionFactory sessionFactory;

    public CustomerInfo getById(int id){
        Session session = sessionFactory.openSession();
        //Session session = HibernateSessionFactory.getSessionFactory().openSession();
        CustomerInfo action = (CustomerInfo) session.get(CustomerInfo.class, id);
        session.close();
        return action;
    }

    public void save(CustomerInfo custInfo) {
        Session session = sessionFactory.openSession();
        //Session session = HibernateSessionFactory.getSessionFactory().openSession();
        Transaction tx1 = session.beginTransaction();
        session.save(custInfo);
        tx1.commit();
        session.close();
    }

    public void update(CustomerInfo custInfo) {
        Session session = sessionFactory.openSession();
        //Session session = HibernateSessionFactory.getSessionFactory().openSession();
        Transaction tx1 = session.beginTransaction();
        session.update(custInfo);
        tx1.commit();
        session.close();
    }

    public void delete(int id) {
        Session session = sessionFactory.openSession();
        //Session session = HibernateSessionFactory.getSessionFactory().openSession();
        Transaction tx1 = session.beginTransaction();
        CustomerInfo custInfo = session.get(CustomerInfo.class, id);
        session.delete(custInfo);
        tx1.commit();
        session.close();
    }

    public List<CustomerInfo> findAll() {
        Session session = sessionFactory.openSession();
        //Session session = HibernateSessionFactory.getSessionFactory().openSession();
        @SuppressWarnings("unchecked")
        List<CustomerInfo> list = (List<CustomerInfo>)session
                .createQuery("From CustomerInfo").list();
        session.close();
        return list;
    }
}
