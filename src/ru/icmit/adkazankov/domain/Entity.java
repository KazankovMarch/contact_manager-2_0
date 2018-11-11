package ru.icmit.adkazankov.domain;

import ru.icmit.adkazankov.annotations.*;
import ru.icmit.adkazankov.dao.GenericDAO;

public abstract class Entity {

    @Id
    @Column(name = "id")
    protected Long id;

    public Long getId() { return id; }

    public void setId(Long id) { this.id = id; }

    public abstract GenericDAO getDAO();
}
