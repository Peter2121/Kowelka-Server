/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kowelka.server;

import java.util.ArrayList;
import java.util.Iterator;

/**
 *
 * @author peter
 */
public class Order extends OrderName {

    public ArrayList<OrderLine> orderlines;
    
    public Order() {
        super();
        orderlines=new ArrayList<>();
    }
    
    public Order(String name, ArrayList<OrderLine> lines) {
        super(name);
        if(lines!=null) orderlines=lines;
        else orderlines=new ArrayList<>();
    }

    public Order(String name) {
        super(name);
        orderlines=new ArrayList<>();
    }

    public Order(String name, Long id) {
        super(name,id);
        orderlines=new ArrayList<>();
    }

    public Order(OrderName ordername) {
        super(ordername.getName(),ordername.getId());
        orderlines=new ArrayList<>();
    }
    
    public ArrayList<OrderLine> getOrderlines() {
        return orderlines;
    }

    public Iterator<OrderLine> iterator() {
        return orderlines.iterator();
    }

    public boolean addLine(OrderLine line) {
        return orderlines.add(line);
    }
        
}
