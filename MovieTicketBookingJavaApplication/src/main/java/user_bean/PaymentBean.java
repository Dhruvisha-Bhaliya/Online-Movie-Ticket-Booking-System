/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/J2EE/EJB40/StatelessEjbClass.java to edit this template
 */
package user_bean;

import entity.Payment;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

/**
 *
 * @author HP
 */
@Stateless
public class PaymentBean implements PaymentBeanLocal {

    @PersistenceContext(unitName = "myMovie")
    private EntityManager em;

    @Override
    public void createPayment(Payment payment) {
        em.persist(payment);
    }

    @Override
    public Payment findByTransctionId(String transactionId) {
        try {
            return em.createQuery(
                    "SELECT p FROM Payment p WHERE p.transactionId = :tx",
                    Payment.class
            ).setParameter("tx", transactionId)
                    .getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public void update(Payment payment) {
        em.merge(payment);
    }

}
