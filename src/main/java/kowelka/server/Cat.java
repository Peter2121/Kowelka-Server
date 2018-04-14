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
public class Cat {
    protected Long id;
    protected String name;

    public Cat() {
        this(0L,"");
    }
    
    public Cat(Long Id, String Name) {
        this.id = Id;
        this.name = Name;
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
    
}

