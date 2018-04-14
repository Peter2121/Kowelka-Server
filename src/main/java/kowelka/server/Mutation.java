/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kowelka.server;

import com.coxautodev.graphql.tools.GraphQLRootResolver;

public class Mutation implements GraphQLRootResolver {
    
    private final dbPGSQLRepository repo;

    public Mutation(dbPGSQLRepository repo) {
        this.repo = repo;
    }
           
    public Long addCatToDB(String catName) {
        return repo.addCatToDB(catName);
    }
    /*
    mutation { addCatToDB(catName: "CatVeryNewNew") }
    */
    
    public Boolean saveCatToDB(InCat cat) {
        return repo.saveCatToDB(cat);
    }
    /*
    mutation { saveCatToDB(cat: {id:225,name:"CatChanged"}) }
    */

    public Long addProdToDB(String prodName, Long idCat) {
        return repo.addProdToDB(prodName,idCat);
    }
    /*
    mutation { addProdToDB(prod: {id:0,name:"NewProd",catName:"CatChanged"}) }
    */
    
    public Boolean saveProdToDB(InProd prod) {
        return repo.saveProdToDB(prod);
    }

    public Boolean removeProdFromDB(Long id) {
        return repo.removeProdFromDB(id);
    }
    
    public Boolean removeCatFromDB(Long id) {
        return repo.removeCatFromDB(id);
    }
    
    public Long addOrderNameToDB(String ordername) {
        return repo.addOrderToDB(new Order(ordername));
    }
    
    public Boolean removeOrderFromDB(Long id) {
        return repo.removeOrderFromDB(id);
    }
    
    public Boolean saveOrderNameToDB(InOrdername oname) {
        return repo.saveOrderNameToDB(new Order(oname.getName(),oname.getId()));
    }
    
    public Boolean addLineToOrder(Long idorder, InOrderline ioline) {
        Prod prod = repo.readProdFromDB(ioline.prodid);
        if(prod==null) return false;
        OrderLine oline = new OrderLine(prod,ioline.getNumproducts());
        if(oline==null) return false;
        return repo.addOrderLine(idorder, oline);
    }
    
    public Boolean incOrderLine(Long idorder, Long idprod) {
        return repo.increaseOrderLine(idprod, idorder);
    }
    
    public Boolean decOrderLine(Long idorder, Long idprod) {
        return repo.decreaseOrderLine(idprod, idorder);        
    }
    
    
}
