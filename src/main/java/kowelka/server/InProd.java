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
public class InProd extends Prod {
    
    public InProd() {
        super();
    }
    
    public InProd(Long Id, String Name, String catName, Long catId) {
        super(Id,Name,catName,catId);
    }
    
}
