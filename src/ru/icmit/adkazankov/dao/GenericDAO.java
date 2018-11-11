package ru.icmit.adkazankov.dao;


import java.util.List;

public interface GenericDAO<T> {
    T create(T o);
    void update(T o);
    void delete(T o);
    List<T> getAll();
    T getByKey(Long id);
}
