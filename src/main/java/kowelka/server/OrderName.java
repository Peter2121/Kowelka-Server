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
public class OrderName {

    protected Long id;
    protected String name;

    public OrderName() {
        id=0L;
        name="";
    }

    public OrderName(String name) {
        id=0L;
        this.name=name;
    }

    public OrderName(String name, Long id) {
        this.id=id;
        this.name=name;
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

    public void setName(String orderName) {
        this.name = orderName;
    }
    
}
