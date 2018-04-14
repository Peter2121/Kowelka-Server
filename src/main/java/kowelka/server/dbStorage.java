/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kowelka.server;

import java.util.ArrayList;

/**
 *
 * @author peter
 */
public interface dbStorage {

    public Long saveToDB(Object object);
    public Boolean readFromDB(Long id);
    public Long readFromDB(String name);
    public ArrayList<Object> readAllFromDB();
    public ArrayList<Object> searchInDB(Long idMin, Long idMax, Integer nmax);
    public ArrayList<Object> searchInDB(String filter, Integer nmax);
    
}
