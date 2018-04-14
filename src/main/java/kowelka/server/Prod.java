/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kowelka.server;

/**
 *
 * @author peter
 */
public class Prod {
    protected Long id;
    protected String name;
    protected String catname;
    protected Long catid;

    public Prod(Long Id, String Name, String catName, Long catId) {
        this.id = Id;
        this.name = Name;
        this.catname = catName;
        this.catid = catId;
    }

    public Prod(String Name, Long catId) {
        this(0L,Name,"",catId);
    }

    public Prod() {
        this(0L,"","",0L);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long Id) {
        this.id = Id;
    }

    public String getName() {
        return name;
    }

    public void setName(String Name) {
        this.name = Name;
    }

    public String getCatname() {
        return catname;
    }

    public void setCatname(String catName) {
        this.catname = catName;
    }

    public Long getCatid() {
        return catid;
    }

    public void setCatid(Long catId) {
        this.catid = catId;
    }

}
