/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import controller.exceptions.NonexistentEntityException;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import entity.AutoNode;
import entity.AutoScenario;
import java.util.ArrayList;
import java.util.List;
import entity.AutoQuery;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 *
 * @author Jullian
 */
public class AutoNodeJpaController implements Serializable {

    public AutoNodeJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(AutoNode autoNode) {
        if (autoNode.getAutoScenarioList() == null) {
            autoNode.setAutoScenarioList(new ArrayList<AutoScenario>());
        }
        if (autoNode.getAutoNodeList() == null) {
            autoNode.setAutoNodeList(new ArrayList<AutoNode>());
        }
        if (autoNode.getAutoScenarioList1() == null) {
            autoNode.setAutoScenarioList1(new ArrayList<AutoScenario>());
        }
        if (autoNode.getAutoQueryList() == null) {
            autoNode.setAutoQueryList(new ArrayList<AutoQuery>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            AutoNode parentId = autoNode.getParentId();
            if (parentId != null) {
                parentId = em.getReference(parentId.getClass(), parentId.getId());
                autoNode.setParentId(parentId);
            }
            List<AutoScenario> attachedAutoScenarioList = new ArrayList<AutoScenario>();
            for (AutoScenario autoScenarioListAutoScenarioToAttach : autoNode.getAutoScenarioList()) {
                autoScenarioListAutoScenarioToAttach = em.getReference(autoScenarioListAutoScenarioToAttach.getClass(), autoScenarioListAutoScenarioToAttach.getId());
                attachedAutoScenarioList.add(autoScenarioListAutoScenarioToAttach);
            }
            autoNode.setAutoScenarioList(attachedAutoScenarioList);
            List<AutoNode> attachedAutoNodeList = new ArrayList<AutoNode>();
            for (AutoNode autoNodeListAutoNodeToAttach : autoNode.getAutoNodeList()) {
                autoNodeListAutoNodeToAttach = em.getReference(autoNodeListAutoNodeToAttach.getClass(), autoNodeListAutoNodeToAttach.getId());
                attachedAutoNodeList.add(autoNodeListAutoNodeToAttach);
            }
            autoNode.setAutoNodeList(attachedAutoNodeList);
            List<AutoScenario> attachedAutoScenarioList1 = new ArrayList<AutoScenario>();
            for (AutoScenario autoScenarioList1AutoScenarioToAttach : autoNode.getAutoScenarioList1()) {
                autoScenarioList1AutoScenarioToAttach = em.getReference(autoScenarioList1AutoScenarioToAttach.getClass(), autoScenarioList1AutoScenarioToAttach.getId());
                attachedAutoScenarioList1.add(autoScenarioList1AutoScenarioToAttach);
            }
            autoNode.setAutoScenarioList1(attachedAutoScenarioList1);
            List<AutoQuery> attachedAutoQueryList = new ArrayList<AutoQuery>();
            for (AutoQuery autoQueryListAutoQueryToAttach : autoNode.getAutoQueryList()) {
                autoQueryListAutoQueryToAttach = em.getReference(autoQueryListAutoQueryToAttach.getClass(), autoQueryListAutoQueryToAttach.getId());
                attachedAutoQueryList.add(autoQueryListAutoQueryToAttach);
            }
            autoNode.setAutoQueryList(attachedAutoQueryList);
            em.persist(autoNode);
            if (parentId != null) {
                parentId.getAutoNodeList().add(autoNode);
                parentId = em.merge(parentId);
            }
            for (AutoScenario autoScenarioListAutoScenario : autoNode.getAutoScenarioList()) {
                autoScenarioListAutoScenario.getAutoNodeList().add(autoNode);
                autoScenarioListAutoScenario = em.merge(autoScenarioListAutoScenario);
            }
            for (AutoNode autoNodeListAutoNode : autoNode.getAutoNodeList()) {
                AutoNode oldParentIdOfAutoNodeListAutoNode = autoNodeListAutoNode.getParentId();
                autoNodeListAutoNode.setParentId(autoNode);
                autoNodeListAutoNode = em.merge(autoNodeListAutoNode);
                if (oldParentIdOfAutoNodeListAutoNode != null) {
                    oldParentIdOfAutoNodeListAutoNode.getAutoNodeList().remove(autoNodeListAutoNode);
                    oldParentIdOfAutoNodeListAutoNode = em.merge(oldParentIdOfAutoNodeListAutoNode);
                }
            }
            for (AutoScenario autoScenarioList1AutoScenario : autoNode.getAutoScenarioList1()) {
                AutoNode oldRootIdOfAutoScenarioList1AutoScenario = autoScenarioList1AutoScenario.getRootId();
                autoScenarioList1AutoScenario.setRootId(autoNode);
                autoScenarioList1AutoScenario = em.merge(autoScenarioList1AutoScenario);
                if (oldRootIdOfAutoScenarioList1AutoScenario != null) {
                    oldRootIdOfAutoScenarioList1AutoScenario.getAutoScenarioList1().remove(autoScenarioList1AutoScenario);
                    oldRootIdOfAutoScenarioList1AutoScenario = em.merge(oldRootIdOfAutoScenarioList1AutoScenario);
                }
            }
            for (AutoQuery autoQueryListAutoQuery : autoNode.getAutoQueryList()) {
                AutoNode oldNodeIdOfAutoQueryListAutoQuery = autoQueryListAutoQuery.getNodeId();
                autoQueryListAutoQuery.setNodeId(autoNode);
                autoQueryListAutoQuery = em.merge(autoQueryListAutoQuery);
                if (oldNodeIdOfAutoQueryListAutoQuery != null) {
                    oldNodeIdOfAutoQueryListAutoQuery.getAutoQueryList().remove(autoQueryListAutoQuery);
                    oldNodeIdOfAutoQueryListAutoQuery = em.merge(oldNodeIdOfAutoQueryListAutoQuery);
                }
            }
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(AutoNode autoNode) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            AutoNode persistentAutoNode = em.find(AutoNode.class, autoNode.getId());
            AutoNode parentIdOld = persistentAutoNode.getParentId();
            AutoNode parentIdNew = autoNode.getParentId();
            List<AutoScenario> autoScenarioListOld = persistentAutoNode.getAutoScenarioList();
            List<AutoScenario> autoScenarioListNew = autoNode.getAutoScenarioList();
            List<AutoNode> autoNodeListOld = persistentAutoNode.getAutoNodeList();
            List<AutoNode> autoNodeListNew = autoNode.getAutoNodeList();
            List<AutoScenario> autoScenarioList1Old = persistentAutoNode.getAutoScenarioList1();
            List<AutoScenario> autoScenarioList1New = autoNode.getAutoScenarioList1();
            List<AutoQuery> autoQueryListOld = persistentAutoNode.getAutoQueryList();
            List<AutoQuery> autoQueryListNew = autoNode.getAutoQueryList();
            if (parentIdNew != null) {
                parentIdNew = em.getReference(parentIdNew.getClass(), parentIdNew.getId());
                autoNode.setParentId(parentIdNew);
            }
            List<AutoScenario> attachedAutoScenarioListNew = new ArrayList<AutoScenario>();
            for (AutoScenario autoScenarioListNewAutoScenarioToAttach : autoScenarioListNew) {
                autoScenarioListNewAutoScenarioToAttach = em.getReference(autoScenarioListNewAutoScenarioToAttach.getClass(), autoScenarioListNewAutoScenarioToAttach.getId());
                attachedAutoScenarioListNew.add(autoScenarioListNewAutoScenarioToAttach);
            }
            autoScenarioListNew = attachedAutoScenarioListNew;
            autoNode.setAutoScenarioList(autoScenarioListNew);
            List<AutoNode> attachedAutoNodeListNew = new ArrayList<AutoNode>();
            for (AutoNode autoNodeListNewAutoNodeToAttach : autoNodeListNew) {
                autoNodeListNewAutoNodeToAttach = em.getReference(autoNodeListNewAutoNodeToAttach.getClass(), autoNodeListNewAutoNodeToAttach.getId());
                attachedAutoNodeListNew.add(autoNodeListNewAutoNodeToAttach);
            }
            autoNodeListNew = attachedAutoNodeListNew;
            autoNode.setAutoNodeList(autoNodeListNew);
            List<AutoScenario> attachedAutoScenarioList1New = new ArrayList<AutoScenario>();
            for (AutoScenario autoScenarioList1NewAutoScenarioToAttach : autoScenarioList1New) {
                autoScenarioList1NewAutoScenarioToAttach = em.getReference(autoScenarioList1NewAutoScenarioToAttach.getClass(), autoScenarioList1NewAutoScenarioToAttach.getId());
                attachedAutoScenarioList1New.add(autoScenarioList1NewAutoScenarioToAttach);
            }
            autoScenarioList1New = attachedAutoScenarioList1New;
            autoNode.setAutoScenarioList1(autoScenarioList1New);
            List<AutoQuery> attachedAutoQueryListNew = new ArrayList<AutoQuery>();
            for (AutoQuery autoQueryListNewAutoQueryToAttach : autoQueryListNew) {
                autoQueryListNewAutoQueryToAttach = em.getReference(autoQueryListNewAutoQueryToAttach.getClass(), autoQueryListNewAutoQueryToAttach.getId());
                attachedAutoQueryListNew.add(autoQueryListNewAutoQueryToAttach);
            }
            autoQueryListNew = attachedAutoQueryListNew;
            autoNode.setAutoQueryList(autoQueryListNew);
            autoNode = em.merge(autoNode);
            if (parentIdOld != null && !parentIdOld.equals(parentIdNew)) {
                parentIdOld.getAutoNodeList().remove(autoNode);
                parentIdOld = em.merge(parentIdOld);
            }
            if (parentIdNew != null && !parentIdNew.equals(parentIdOld)) {
                parentIdNew.getAutoNodeList().add(autoNode);
                parentIdNew = em.merge(parentIdNew);
            }
            for (AutoScenario autoScenarioListOldAutoScenario : autoScenarioListOld) {
                if (!autoScenarioListNew.contains(autoScenarioListOldAutoScenario)) {
                    autoScenarioListOldAutoScenario.getAutoNodeList().remove(autoNode);
                    autoScenarioListOldAutoScenario = em.merge(autoScenarioListOldAutoScenario);
                }
            }
            for (AutoScenario autoScenarioListNewAutoScenario : autoScenarioListNew) {
                if (!autoScenarioListOld.contains(autoScenarioListNewAutoScenario)) {
                    autoScenarioListNewAutoScenario.getAutoNodeList().add(autoNode);
                    autoScenarioListNewAutoScenario = em.merge(autoScenarioListNewAutoScenario);
                }
            }
            for (AutoNode autoNodeListOldAutoNode : autoNodeListOld) {
                if (!autoNodeListNew.contains(autoNodeListOldAutoNode)) {
                    autoNodeListOldAutoNode.setParentId(null);
                    autoNodeListOldAutoNode = em.merge(autoNodeListOldAutoNode);
                }
            }
            for (AutoNode autoNodeListNewAutoNode : autoNodeListNew) {
                if (!autoNodeListOld.contains(autoNodeListNewAutoNode)) {
                    AutoNode oldParentIdOfAutoNodeListNewAutoNode = autoNodeListNewAutoNode.getParentId();
                    autoNodeListNewAutoNode.setParentId(autoNode);
                    autoNodeListNewAutoNode = em.merge(autoNodeListNewAutoNode);
                    if (oldParentIdOfAutoNodeListNewAutoNode != null && !oldParentIdOfAutoNodeListNewAutoNode.equals(autoNode)) {
                        oldParentIdOfAutoNodeListNewAutoNode.getAutoNodeList().remove(autoNodeListNewAutoNode);
                        oldParentIdOfAutoNodeListNewAutoNode = em.merge(oldParentIdOfAutoNodeListNewAutoNode);
                    }
                }
            }
            for (AutoScenario autoScenarioList1OldAutoScenario : autoScenarioList1Old) {
                if (!autoScenarioList1New.contains(autoScenarioList1OldAutoScenario)) {
                    autoScenarioList1OldAutoScenario.setRootId(null);
                    autoScenarioList1OldAutoScenario = em.merge(autoScenarioList1OldAutoScenario);
                }
            }
            for (AutoScenario autoScenarioList1NewAutoScenario : autoScenarioList1New) {
                if (!autoScenarioList1Old.contains(autoScenarioList1NewAutoScenario)) {
                    AutoNode oldRootIdOfAutoScenarioList1NewAutoScenario = autoScenarioList1NewAutoScenario.getRootId();
                    autoScenarioList1NewAutoScenario.setRootId(autoNode);
                    autoScenarioList1NewAutoScenario = em.merge(autoScenarioList1NewAutoScenario);
                    if (oldRootIdOfAutoScenarioList1NewAutoScenario != null && !oldRootIdOfAutoScenarioList1NewAutoScenario.equals(autoNode)) {
                        oldRootIdOfAutoScenarioList1NewAutoScenario.getAutoScenarioList1().remove(autoScenarioList1NewAutoScenario);
                        oldRootIdOfAutoScenarioList1NewAutoScenario = em.merge(oldRootIdOfAutoScenarioList1NewAutoScenario);
                    }
                }
            }
            for (AutoQuery autoQueryListOldAutoQuery : autoQueryListOld) {
                if (!autoQueryListNew.contains(autoQueryListOldAutoQuery)) {
                    autoQueryListOldAutoQuery.setNodeId(null);
                    autoQueryListOldAutoQuery = em.merge(autoQueryListOldAutoQuery);
                }
            }
            for (AutoQuery autoQueryListNewAutoQuery : autoQueryListNew) {
                if (!autoQueryListOld.contains(autoQueryListNewAutoQuery)) {
                    AutoNode oldNodeIdOfAutoQueryListNewAutoQuery = autoQueryListNewAutoQuery.getNodeId();
                    autoQueryListNewAutoQuery.setNodeId(autoNode);
                    autoQueryListNewAutoQuery = em.merge(autoQueryListNewAutoQuery);
                    if (oldNodeIdOfAutoQueryListNewAutoQuery != null && !oldNodeIdOfAutoQueryListNewAutoQuery.equals(autoNode)) {
                        oldNodeIdOfAutoQueryListNewAutoQuery.getAutoQueryList().remove(autoQueryListNewAutoQuery);
                        oldNodeIdOfAutoQueryListNewAutoQuery = em.merge(oldNodeIdOfAutoQueryListNewAutoQuery);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Long id = autoNode.getId();
                if (findAutoNode(id) == null) {
                    throw new NonexistentEntityException("The autoNode with id " + id + " no longer exists.");
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
            AutoNode autoNode;
            try {
                autoNode = em.getReference(AutoNode.class, id);
                autoNode.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The autoNode with id " + id + " no longer exists.", enfe);
            }
            AutoNode parentId = autoNode.getParentId();
            if (parentId != null) {
                parentId.getAutoNodeList().remove(autoNode);
                parentId = em.merge(parentId);
            }
            List<AutoScenario> autoScenarioList = autoNode.getAutoScenarioList();
            for (AutoScenario autoScenarioListAutoScenario : autoScenarioList) {
                autoScenarioListAutoScenario.getAutoNodeList().remove(autoNode);
                autoScenarioListAutoScenario = em.merge(autoScenarioListAutoScenario);
            }
            List<AutoNode> autoNodeList = autoNode.getAutoNodeList();
            for (AutoNode autoNodeListAutoNode : autoNodeList) {
                autoNodeListAutoNode.setParentId(null);
                autoNodeListAutoNode = em.merge(autoNodeListAutoNode);
            }
            List<AutoScenario> autoScenarioList1 = autoNode.getAutoScenarioList1();
            for (AutoScenario autoScenarioList1AutoScenario : autoScenarioList1) {
                autoScenarioList1AutoScenario.setRootId(null);
                autoScenarioList1AutoScenario = em.merge(autoScenarioList1AutoScenario);
            }
            List<AutoQuery> autoQueryList = autoNode.getAutoQueryList();
            for (AutoQuery autoQueryListAutoQuery : autoQueryList) {
                autoQueryListAutoQuery.setNodeId(null);
                autoQueryListAutoQuery = em.merge(autoQueryListAutoQuery);
            }
            em.remove(autoNode);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<AutoNode> findAutoNodeEntities() {
        return findAutoNodeEntities(true, -1, -1);
    }

    public List<AutoNode> findAutoNodeEntities(int maxResults, int firstResult) {
        return findAutoNodeEntities(false, maxResults, firstResult);
    }

    private List<AutoNode> findAutoNodeEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(AutoNode.class));
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

    public AutoNode findAutoNode(Long id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(AutoNode.class, id);
        } finally {
            em.close();
        }
    }

    public int getAutoNodeCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<AutoNode> rt = cq.from(AutoNode.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}