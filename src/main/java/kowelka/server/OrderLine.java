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
public class OrderLine {
    
    protected Prod product;
    protected Integer numproducts;
    
    public OrderLine(Prod prod, Integer num) {
        product=prod;
        numproducts=num;
    }
    
    public OrderLine() {
        product=null;
        numproducts=0;
    }

    public Prod getProduct() {
        return product;
    }

    public void setProduct(Prod Product) {
        this.product = Product;
    }

    public Integer getNumproducts() {
        return numproducts;
    }

    public void setNumproducts(Integer numProducts) {
        this.numproducts = numProducts;
    }
    
}
