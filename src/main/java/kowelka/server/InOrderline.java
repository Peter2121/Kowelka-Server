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
public class InOrderline {
    
    protected Long prodid;
    protected Integer numproducts;
    
    public InOrderline(Long id, Integer num) {
        prodid=id;
        numproducts=num;
    }
    
    public InOrderline() {
        prodid=0L;
        numproducts=0;
    }

    public Integer getNumproducts() {
        return numproducts;
    }

    public void setNumproducts(Integer numProducts) {
        this.numproducts = numProducts;
    }

    public Long getProdid() {
        return prodid;
    }

    public void setProdid(Long prodid) {
        this.prodid = prodid;
    }

}