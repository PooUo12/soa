package com.spo.workerService.repository;

import jakarta.enterprise.context.RequestScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import jakarta.ws.rs.ext.Provider;

@Provider
@RequestScoped
public class EntityManagerProvider {
    @PersistenceContext
    private EntityManager entityManager;
}
