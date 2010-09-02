/*
 * Copyright (c) 2008 Avaya Inc.
 *
 * All rights reserved.
 */

package com.blogspot.javaadventure.end2endtesting.data;

import java.util.Date;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQuery;

/**
 * Created by IntelliJ IDEA.
 *
 * @author connollys
 * @since Sep 2, 2010 12:53:49 PM
 */
@Entity
@NamedQuery(name = Visitor.BY_NAME, query = "select v from Visitor v where v.name = :name")
public class Visitor {
    @Id()
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String name;

    private Date lastVisit;
    public static final String BY_NAME = "VisitorByName";

    public Visitor() {
    }

    public Visitor(String name, Date lastVisit) {
        this.name = name;
        this.lastVisit = lastVisit;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Visitor visitor = (Visitor) o;

        if (id != null ? !id.equals(visitor.id) : visitor.id != null) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("Visitor");
        sb.append("{id=").append(id);
        sb.append(", name='").append(name).append('\'');
        sb.append(", lastVisit=").append(lastVisit);
        sb.append('}');
        return sb.toString();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getLastVisit() {
        return lastVisit == null ? null : (Date) lastVisit.clone();
    }

    public void setLastVisit(Date lastVisit) {
        this.lastVisit = lastVisit == null ? null : (Date) lastVisit.clone();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
