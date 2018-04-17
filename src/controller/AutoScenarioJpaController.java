/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import controller.exceptions.NonexistentEntityException;
import similarity.entity.Execution;
import similarity.entity.Node;
import similarity.entity.Scenario;

import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 *
 * @author Jullian
 */
public class AutoScenarioJpaController implements Serializable {

    public AutoScenarioJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Scenario autoScenario) {
        if (autoScenario.getAutoNodeList() == null) {
            autoScenario.setAutoNodeList(new ArrayList<Node>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Execution executionId = autoScenario.getExecutionId();
            if (executionId != null) {
                executionId = em.getReference(executionId.getClass(), executionId.getId());
                autoScenario.setExecutionId(executionId);
            }
            Node rootId = autoScenario.getRootId();
            if (rootId != null) {
                rootId = em.getReference(rootId.getClass(), rootId.getId());
                autoScenario.setRootId(rootId);
            }
            List<Node> attachedAutoNodeList = new ArrayList<Node>();
            for (Node autoNodeListAutoNodeToAttach : autoScenario.getAutoNodeList()) {
                autoNodeListAutoNodeToAttach = em.getReference(autoNodeListAutoNodeToAttach.getClass(), autoNodeListAutoNodeToAttach.getId());
                attachedAutoNodeList.add(autoNodeListAutoNodeToAttach);
            }
            autoScenario.setAutoNodeList(attachedAutoNodeList);
            em.persist(autoScenario);
            if (executionId != null) {
                executionId.getAutoScenarioList().add(autoScenario);
                executionId = em.merge(executionId);
            }
            if (rootId != null) {
                rootId.getAutoScenarioList().add(autoScenario);
                rootId = em.merge(rootId);
            }
            for (Node autoNodeListAutoNode : autoScenario.getAutoNodeList()) {
                autoNodeListAutoNode.getAutoScenarioList().add(autoScenario);
                autoNodeListAutoNode = em.merge(autoNodeListAutoNode);
            }
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Scenario autoScenario) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Scenario persistentAutoScenario = em.find(Scenario.class, autoScenario.getId());
            Execution executionIdOld = persistentAutoScenario.getExecutionId();
            Execution executionIdNew = autoScenario.getExecutionId();
            Node rootIdOld = persistentAutoScenario.getRootId();
            Node rootIdNew = autoScenario.getRootId();
            List<Node> autoNodeListOld = persistentAutoScenario.getAutoNodeList();
            List<Node> autoNodeListNew = autoScenario.getAutoNodeList();
            if (executionIdNew != null) {
                executionIdNew = em.getReference(executionIdNew.getClass(), executionIdNew.getId());
                autoScenario.setExecutionId(executionIdNew);
            }
            if (rootIdNew != null) {
                rootIdNew = em.getReference(rootIdNew.getClass(), rootIdNew.getId());
                autoScenario.setRootId(rootIdNew);
            }
            List<Node> attachedAutoNodeListNew = new ArrayList<Node>();
            for (Node autoNodeListNewAutoNodeToAttach : autoNodeListNew) {
                autoNodeListNewAutoNodeToAttach = em.getReference(autoNodeListNewAutoNodeToAttach.getClass(), autoNodeListNewAutoNodeToAttach.getId());
                attachedAutoNodeListNew.add(autoNodeListNewAutoNodeToAttach);
            }
            autoNodeListNew = attachedAutoNodeListNew;
            autoScenario.setAutoNodeList(autoNodeListNew);
            autoScenario = em.merge(autoScenario);
            if (executionIdOld != null && !executionIdOld.equals(executionIdNew)) {
                executionIdOld.getAutoScenarioList().remove(autoScenario);
                executionIdOld = em.merge(executionIdOld);
            }
            if (executionIdNew != null && !executionIdNew.equals(executionIdOld)) {
                executionIdNew.getAutoScenarioList().add(autoScenario);
                executionIdNew = em.merge(executionIdNew);
            }
            if (rootIdOld != null && !rootIdOld.equals(rootIdNew)) {
                rootIdOld.getAutoScenarioList().remove(autoScenario);
                rootIdOld = em.merge(rootIdOld);
            }
            if (rootIdNew != null && !rootIdNew.equals(rootIdOld)) {
                rootIdNew.getAutoScenarioList().add(autoScenario);
                rootIdNew = em.merge(rootIdNew);
            }
            for (Node autoNodeListOldAutoNode : autoNodeListOld) {
                if (!autoNodeListNew.contains(autoNodeListOldAutoNode)) {
                    autoNodeListOldAutoNode.getAutoScenarioList().remove(autoScenario);
                    autoNodeListOldAutoNode = em.merge(autoNodeListOldAutoNode);
                }
            }
            for (Node autoNodeListNewAutoNode : autoNodeListNew) {
                if (!autoNodeListOld.contains(autoNodeListNewAutoNode)) {
                    autoNodeListNewAutoNode.getAutoScenarioList().add(autoScenario);
                    autoNodeListNewAutoNode = em.merge(autoNodeListNewAutoNode);
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Long id = autoScenario.getId();
                if (findAutoScenario(id) == null) {
                    throw new NonexistentEntityException("The autoScenario with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(Long id) throws NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Scenario autoScenario;
            try {
                autoScenario = em.getReference(Scenario.class, id);
                autoScenario.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The autoScenario with id " + id + " no longer exists.", enfe);
            }
            Execution executionId = autoScenario.getExecutionId();
            if (executionId != null) {
                executionId.getAutoScenarioList().remove(autoScenario);
                executionId = em.merge(executionId);
            }
            Node rootId = autoScenario.getRootId();
            if (rootId != null) {
                rootId.getAutoScenarioList().remove(autoScenario);
                rootId = em.merge(rootId);
            }
            List<Node> autoNodeList = autoScenario.getAutoNodeList();
            for (Node autoNodeListAutoNode : autoNodeList) {
                autoNodeListAutoNode.getAutoScenarioList().remove(autoScenario);
                autoNodeListAutoNode = em.merge(autoNodeListAutoNode);
            }
            em.remove(autoScenario);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Scenario> findAutoScenarioEntities() {
        return findAutoScenarioEntities(true, -1, -1);
    }

    public List<Scenario> findAutoScenarioEntities(int maxResults, int firstResult) {
        return findAutoScenarioEntities(false, maxResults, firstResult);
    }

    private List<Scenario> findAutoScenarioEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Scenario.class));
            
            Query q = em.createQuery(cq);
            if (!all) {
                q.setMaxResults(maxResults);
                q.setFirstResult(firstResult);
            }
            return q.getResultList();
        } finally {
            em.close();
        }
    }

    public Scenario findAutoScenario(Long id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Scenario.class, id);
        } finally {
            em.close();
        }
    }
    
    public List<Scenario> findAutoScenarioFilter()
    {
        EntityManager em = getEntityManager();
        CriteriaQuery cq = em.getCriteriaBuilder().createQuery(Scenario.class);
        Root c = cq.from(Scenario.class);
        cq.select(c.get("name")).distinct(true);
        cq.select(c.get("requestUrl")).distinct(true);
        Query q = em.createQuery(cq);
        
        return q.getResultList();
    }

    public int getAutoScenarioCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Scenario> rt = cq.from(Scenario.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
