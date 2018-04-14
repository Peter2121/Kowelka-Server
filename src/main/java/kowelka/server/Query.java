/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kowelka.server;

//import com.coxautodev.graphql.tools.GraphQLQueryResolver;
import com.coxautodev.graphql.tools.GraphQLRootResolver;
import java.util.ArrayList;
//import com.coxautodev.graphql.tools.*;
import java.util.List;


public class Query implements GraphQLRootResolver {
    
    private final dbPGSQLRepository repo;

    public Query(dbPGSQLRepository repo) {
        this.repo = repo;
    }
    
    public Cat readCatFromDB(Long id) {
        return repo.readCatFromDB(id);
    }
    /*
    { readCatFromDB(id: 2) { id, Name }}
    */
    
    public Prod readProdFromDB(Long id) {
        return repo.readProdFromDB(id);
    }
    /*
    { readProdFromDB(id: 3) { id, Name, catName }}
    */
    
    public ArrayList<Cat> readAllCatFromDB() {
        return repo.readAllCatFromDB();
    }
    /*
    { readAllCatFromDB { id, Name }}
    */
    
    public ArrayList<Prod> readAllProdFromDB() {
        return repo.readAllProdFromDB();
    }
    /*
    { readAllProdFromDB { id, Name, catName }}
    */
    
    public ArrayList<OrderName> readAllOrderNamesFromDB() {
        return repo.readAllOrderNamesFromDB();
    }
    /*
    { readAllOrderNamesFromDB { id, Name }}
    */

    public Order readOrderFromDB(Long id) {
        return repo.readOrderFromDB(id);
    }

}
