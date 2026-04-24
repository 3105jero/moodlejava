package com.riwi.talent.dao;

import java.util.List;
import java.util.Optional;

/**
 * Interfaz genérica DAO.
 * T  = tipo de entidad  (ej. Empleado)
 * ID = tipo de la clave (ej. Integer)
 *
 * Permite que GenericDAOImpl implemente el CRUD una sola vez
 * y cada DAO concreto solo defina sus queries y mapeo.
 */
public interface GenericDAO<T, ID> {
    T           save(T entity);
    Optional<T> findById(ID id);
    List<T>     findAll();
    boolean     update(T entity);
    boolean     deleteById(ID id);
}
