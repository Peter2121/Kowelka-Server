/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kowelka.server;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

/**
 *
 * @author peter
 */
public class dbPGSQLRepository implements dbPGSQLStorage {
    
    public final static String DB_DRIVER_NAME = "org.postgresql.Driver";
    public final static String DB_TABLE_NAME_PROD = "public.prod";
    public final static String DB_TABLE_FIELD_ID_PROD = "id";
    public final static String DB_TABLE_FIELD_NAME_PROD = "name";
    public final static String DB_TABLE_FIELD_ID_CAT_PROD = "id_cat";
    public final static Integer MAX_PROD = 1024;
    
    public final static String DB_TABLE_NAME_CAT = "public.cat";
    public final static String DB_TABLE_FIELD_ID_CAT = "id";
    public final static String DB_TABLE_FIELD_NAME_CAT = "name";   
    public final static Integer MAX_CAT = 1024;
    
    public final static String DB_TABLE_NAME_ORDER = "public.order";
    public final static String DB_TABLE_FIELD_ID_ORDER = "id";
    public final static String DB_TABLE_FIELD_NAME_ORDER = "name";   
    public final static Integer MAX_ORDER = 1024;

    public final static String DB_TABLE_NAME_OP = "public.order_prods";
    public final static String DB_TABLE_FIELD_ID_ORDER_OP = "id_order";
    public final static String DB_TABLE_FIELD_ID_PROD_OP = "id_prod";
    public final static String DB_TABLE_FIELD_NUM_OP = "num";
    
    
    private final String dbServerAddr;
    private final String dbUser;
    private final String dbPassword;
    private final String dbName;
    private final Connection conn;
    
    public dbPGSQLRepository (String server, String db, String user, String password) {
        super();
        dbServerAddr=server;
        dbName=db;
        dbUser=user;
        dbPassword=password;
        conn=Init(DB_DRIVER_NAME,"jdbc:postgresql://"+dbServerAddr+"/"+dbName,dbUser,dbPassword);
        if(conn==null) System.out.println("ProdPGSQL: Error DB initialisation");
    }
 
    public Long addProdToDB(Prod prod) {
        return addProdToDB(prod.getName(), prod.getCatid());
    }
    
    public Long addProdToDB(String prodName, Long idCat) {
        Long prodId=0L;
        String sqlInsert = "INSERT INTO "+DB_TABLE_NAME_PROD+"("+DB_TABLE_FIELD_NAME_PROD+","+DB_TABLE_FIELD_ID_CAT_PROD+") VALUES(?,?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sqlInsert, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, prodName);
            pstmt.setLong(2, idCat);
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet rs = pstmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        prodId=rs.getLong(1);
                    }
                    else prodId=0L;
                } catch(SQLException se) {
                    prodId=0L;
                }
            }
            else prodId=0L;
        } catch(SQLException se) {
            prodId=0L;
        }
        return prodId;
    }

    public Boolean saveProdToDB(Prod prod) {
        Long prodId=0L;
        String sqlInsert = "UPDATE "+DB_TABLE_NAME_PROD+" SET "+DB_TABLE_FIELD_NAME_PROD+"=?, "+
                                DB_TABLE_FIELD_ID_CAT_PROD+"=? WHERE "+DB_TABLE_FIELD_ID_PROD+"=?";        
        try (PreparedStatement pstmt = conn.prepareStatement(sqlInsert)) {
            pstmt.setString(1, prod.getName());
            pstmt.setLong(2, prod.getCatid());
            pstmt.setLong(3, prod.getId());
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                return true;
            }
            else return false;
        } catch(SQLException se) {
            return false;
        }
    }

    public Boolean removeProdFromDB(Long id) {
        String sqlDelete="DELETE FROM "+DB_TABLE_NAME_PROD+" WHERE "+DB_TABLE_NAME_PROD+"."+DB_TABLE_FIELD_ID_PROD+"=?";
        Integer affectedRows;
        try (PreparedStatement pstmt = conn.prepareStatement(sqlDelete)) {
            pstmt.setLong(1, id);
            affectedRows = pstmt.executeUpdate();
        } catch(SQLException se) {
            return false;
        }
        if(affectedRows>0) return true;
        else return false;
    }
    
    public Boolean removeProdNameFromDB(String name) {
        String sqlDelete="DELETE FROM "+DB_TABLE_NAME_PROD+" WHERE "+DB_TABLE_NAME_PROD+"."+DB_TABLE_FIELD_NAME_PROD+"=?";
        Integer affectedRows;
        try (PreparedStatement pstmt = conn.prepareStatement(sqlDelete)) {
            pstmt.setString(1, name);
            affectedRows = pstmt.executeUpdate();
        } catch(SQLException se) {
            return false;
        }
        if(affectedRows>0) return true;
        else return false;
    }
    
    public Prod readProdFromDB(Long id) {
        String sqlSelect="SELECT "+DB_TABLE_NAME_PROD+"."+DB_TABLE_FIELD_ID_PROD+" as prod_id, "+
                            DB_TABLE_NAME_PROD+"."+DB_TABLE_FIELD_NAME_PROD+" as prod_name, "+
                            DB_TABLE_NAME_PROD+"."+DB_TABLE_FIELD_ID_CAT_PROD+" as cat_id, "+
                            DB_TABLE_NAME_CAT+"."+DB_TABLE_FIELD_NAME_CAT+" as cat_name"+
                            " FROM "+DB_TABLE_NAME_PROD+","+DB_TABLE_NAME_CAT +
                            " WHERE "+DB_TABLE_NAME_PROD+"."+DB_TABLE_FIELD_ID_CAT_PROD+"="+DB_TABLE_NAME_CAT+"."+DB_TABLE_FIELD_ID_CAT+
                            " AND "+DB_TABLE_NAME_PROD+"."+DB_TABLE_FIELD_ID_PROD+"=?";
        Prod prod = new Prod();
        try (PreparedStatement pstmt = conn.prepareStatement(sqlSelect)) {
            pstmt.setLong(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    prod.setId(rs.getLong("prod_id"));
                    prod.setName(rs.getString ("prod_name"));
                    prod.setCatname(rs.getString ("cat_name"));
                    prod.setCatid(rs.getLong ("cat_id"));
                    return prod;
                }
                else return null;
            }  catch(SQLException se) {
            return null;
            }
        } catch(SQLException se) {
            return null;
        }
    }
    
    public Prod readProdNameFromDB(String name) {
        String sqlSelect="SELECT "+DB_TABLE_NAME_PROD+"."+DB_TABLE_FIELD_ID_PROD+" as prod_id, "+
                            DB_TABLE_NAME_PROD+"."+DB_TABLE_FIELD_NAME_PROD+" as prod_name, "+
                            DB_TABLE_NAME_PROD+"."+DB_TABLE_FIELD_ID_CAT_PROD+" as cat_id, "+
                            DB_TABLE_NAME_CAT+"."+DB_TABLE_FIELD_NAME_CAT+" as cat_name"+
                            " FROM "+DB_TABLE_NAME_PROD+","+DB_TABLE_NAME_CAT +
                            " WHERE "+DB_TABLE_NAME_PROD+"."+DB_TABLE_FIELD_ID_CAT_PROD+"="+DB_TABLE_NAME_CAT+"."+DB_TABLE_FIELD_ID_CAT+
                            " AND "+DB_TABLE_NAME_PROD+"."+DB_TABLE_FIELD_NAME_PROD+"=?";
        Prod prod = new Prod();
        try (PreparedStatement pstmt = conn.prepareStatement(sqlSelect)) {
            pstmt.setString(1, name);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    prod.setId(rs.getLong("prod_id"));
                    prod.setName(rs.getString ("prod_name"));
                    prod.setCatname(rs.getString ("cat_name"));
                    prod.setCatid(rs.getLong("cat_id"));
                    return prod;
                }
                else return null;
            }  catch(SQLException se) {
            return null;
            }
        } catch(SQLException se) {
            return null;
        }        
    }

    public ArrayList<Prod> readAllProdFromDB() {
        String sqlSelect="SELECT "+DB_TABLE_NAME_PROD+"."+DB_TABLE_FIELD_ID_PROD+" as prod_id, "+
                            DB_TABLE_NAME_PROD+"."+DB_TABLE_FIELD_NAME_PROD+" as prod_name, "+
                            DB_TABLE_NAME_PROD+"."+DB_TABLE_FIELD_ID_CAT_PROD+" as cat_id, "+
                            DB_TABLE_NAME_CAT+"."+DB_TABLE_FIELD_NAME_CAT+" as cat_name"+
                            " FROM "+DB_TABLE_NAME_PROD+","+DB_TABLE_NAME_CAT +
                            " WHERE "+DB_TABLE_NAME_PROD+"."+DB_TABLE_FIELD_ID_CAT_PROD+"="+DB_TABLE_NAME_CAT+"."+DB_TABLE_FIELD_ID_CAT+
                            " ORDER BY "+DB_TABLE_NAME_PROD+"."+DB_TABLE_FIELD_NAME_PROD;

        int i=0;
        ArrayList<Prod> Products = new ArrayList<>();        
        try (   PreparedStatement pstmt = conn.prepareStatement(sqlSelect);
                ResultSet rs = pstmt.executeQuery() ) {            
            while ( rs.next() && i<MAX_PROD ) {
                Prod prod = new Prod();
                prod.setId(rs.getLong("prod_id"));
                prod.setName(rs.getString ("prod_name"));
                prod.setCatname(rs.getString ("cat_name"));
                prod.setCatid(rs.getLong("cat_id"));
                Products.add(prod);
                i++;
            }            
        }
        catch(SQLException se) {
            return null;
        }        
        return Products;
    }
    
    public ArrayList<Prod> searchProdInDB(Long idMin, Long idMax, Integer nmax) {
        String sqlSelect="SELECT TOP ? "+DB_TABLE_NAME_PROD+"."+DB_TABLE_FIELD_ID_PROD+" as prod_id, "+
                            DB_TABLE_NAME_PROD+"."+DB_TABLE_FIELD_NAME_PROD+" as prod_name, "+
                            DB_TABLE_NAME_PROD+"."+DB_TABLE_FIELD_ID_CAT_PROD+" as cat_id, "+
                            DB_TABLE_NAME_CAT+"."+DB_TABLE_FIELD_NAME_CAT+" as cat_name"+
                            " FROM "+DB_TABLE_NAME_PROD+","+DB_TABLE_NAME_CAT +
                            " WHERE "+DB_TABLE_NAME_PROD+"."+DB_TABLE_FIELD_ID_CAT_PROD+"="+DB_TABLE_NAME_CAT+"."+DB_TABLE_FIELD_ID_CAT+
                            " AND "+DB_TABLE_NAME_PROD+"."+DB_TABLE_FIELD_ID_PROD+">=? AND "+DB_TABLE_NAME_PROD+"."+DB_TABLE_FIELD_ID_PROD+"<?"+
                            " ORDER BY "+DB_TABLE_NAME_PROD+"."+DB_TABLE_FIELD_ID_PROD;
        int i=0;
        ArrayList<Prod> Products = new ArrayList<>();        
        try (PreparedStatement pstmt = conn.prepareStatement(sqlSelect)) {            
            pstmt.setInt(1,nmax);
            pstmt.setLong(2,idMin);
            pstmt.setLong(3,idMax);
            ResultSet rs = pstmt.executeQuery();
            while ( rs.next() && i<MAX_PROD ) {
                Prod prod = new Prod();
                prod.setId(rs.getLong("prod_id"));
                prod.setName(rs.getString ("prod_name"));
                prod.setCatname(rs.getString ("cat_name"));
                prod.setCatid(rs.getLong("cat_id"));
                Products.add(prod);
                i++;
            }            
        }
        catch(SQLException se) {
            return null;
        }        
        return Products;        
    }
    
    public ArrayList<Prod> searchProdNameInDB(String filter, Integer nmax) {
        String sqlSelect="SELECT TOP ? "+DB_TABLE_NAME_PROD+"."+DB_TABLE_FIELD_ID_PROD+" as prod_id, "+
                            DB_TABLE_NAME_PROD+"."+DB_TABLE_FIELD_NAME_PROD+" as prod_name, "+
                            DB_TABLE_NAME_PROD+"."+DB_TABLE_FIELD_ID_CAT_PROD+" as cat_id, "+
                            DB_TABLE_NAME_CAT+"."+DB_TABLE_FIELD_NAME_CAT+" as cat_name"+
                            " FROM "+DB_TABLE_NAME_PROD+","+DB_TABLE_NAME_CAT +
                            " WHERE "+DB_TABLE_NAME_PROD+"."+DB_TABLE_FIELD_ID_CAT_PROD+"="+DB_TABLE_NAME_CAT+"."+DB_TABLE_FIELD_ID_CAT+
                            " AND "+DB_TABLE_NAME_PROD+"."+DB_TABLE_FIELD_NAME_PROD+" LIKE %?%"+
                            " ORDER BY "+DB_TABLE_NAME_PROD+"."+DB_TABLE_FIELD_NAME_PROD;
        int i=0;
        ArrayList<Prod> Products = new ArrayList<>();        
        try (PreparedStatement pstmt = conn.prepareStatement(sqlSelect)) {            
            pstmt.setInt(1,nmax);
            pstmt.setString(2,filter);
            ResultSet rs = pstmt.executeQuery();
            while ( rs.next() && i<MAX_PROD ) {
                Prod prod = new Prod();
                prod.setId(rs.getLong("prod_id"));
                prod.setName(rs.getString ("prod_name"));
                prod.setCatname(rs.getString ("cat_name"));
                prod.setCatid(rs.getLong("cat_id"));
                Products.add(prod);
                i++;
            }            
        }
        catch(SQLException se) {
            return null;
        }        
        return Products;                
    }

    public Cat readCatFromDB(Long id) {
        String sqlSelect="SELECT "+DB_TABLE_NAME_CAT+"."+DB_TABLE_FIELD_ID_CAT+" as cat_id, "+
                            DB_TABLE_NAME_CAT+"."+DB_TABLE_FIELD_NAME_CAT+" as cat_name "+
                            " FROM "+DB_TABLE_NAME_CAT +
                            " WHERE "+DB_TABLE_NAME_CAT+"."+DB_TABLE_FIELD_ID_CAT+"=?";
        Cat cat=new Cat();
        try (PreparedStatement pstmt = conn.prepareStatement(sqlSelect)) {
            pstmt.setLong(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    cat.setId(rs.getLong("cat_id"));
                    cat.setName(rs.getString ("cat_name"));
                    return cat;
                }
                else return null;
            }  catch(SQLException se) {
            return null;
            }
        } catch(SQLException se) {
            return null;
        }        
    }
    
    public Cat readCatNameFromDB(String name) {
        String sqlSelect="SELECT "+DB_TABLE_NAME_CAT+"."+DB_TABLE_FIELD_ID_CAT+" as cat_id, "+
                            DB_TABLE_NAME_CAT+"."+DB_TABLE_FIELD_NAME_CAT+" as cat_name "+
                            " FROM "+DB_TABLE_NAME_CAT +
                            " WHERE "+DB_TABLE_NAME_CAT+"."+DB_TABLE_FIELD_NAME_CAT+"=?";
        Cat cat=new Cat();
        
        try (PreparedStatement pstmt = conn.prepareStatement(sqlSelect)) {
            pstmt.setString(1, name);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    cat.setId(rs.getLong("cat_id"));
                    cat.setName(rs.getString ("cat_name"));
                    return cat;
                }
                else return null;
            }  catch(SQLException se) {
            return null;
            }
        } catch(SQLException se) {
            return null;
        }                
    }
    
    public Boolean saveCatToDB(Cat cat) {
//        Long catId=0L;
        if(!isValid(cat)) return false;
//        String sqlInsert = "INSERT INTO "+DB_TABLE_NAME_CAT+"("+DB_TABLE_FIELD_NAME_CAT+") VALUES(?)";
        String sqlUpdate = "UPDATE "+DB_TABLE_NAME_CAT+" SET "+DB_TABLE_FIELD_NAME_CAT+"=? WHERE "+DB_TABLE_FIELD_ID_CAT+"=?";
        try (PreparedStatement pstmt = conn.prepareStatement(sqlUpdate)) {
            pstmt.setString(1, cat.getName());
            pstmt.setLong(2, cat.getId());
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                return true;
            }
            else return false;
        } catch(SQLException se) {
            return false;
        }
    }
    
    public Long addCatToDB(String catName) {
        Long catId=0L;
        if(catName.isEmpty()) return catId;
        String sqlInsert = "INSERT INTO "+DB_TABLE_NAME_CAT+" ("+DB_TABLE_FIELD_NAME_CAT+") VALUES(?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sqlInsert, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, catName);
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet rs = pstmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        catId=rs.getLong(1);
                    }
                    else catId=0L;
                } catch(SQLException se) {
                    catId=0L;
                }
            }
            else catId=0L;
        } catch(SQLException se) {
            catId=0L;
        }
        return catId;
    }

    public Boolean removeCatFromDB(Long id) {
        String sqlDelete="DELETE FROM "+DB_TABLE_NAME_CAT+" WHERE "+DB_TABLE_NAME_CAT+"."+DB_TABLE_FIELD_ID_CAT+"=?";
        Integer affectedRows;
        try (PreparedStatement pstmt = conn.prepareStatement(sqlDelete)) {
            pstmt.setLong(1, id);
            affectedRows = pstmt.executeUpdate();
        } catch(SQLException se) {
            return false;
        }
        if(affectedRows>0) return true;
        else return false;
    }

    public Boolean removeCatNameFromDB(String name) {
        String sqlDelete="DELETE FROM "+DB_TABLE_NAME_CAT+" WHERE "+DB_TABLE_NAME_CAT+"."+DB_TABLE_FIELD_NAME_CAT+"=?";
        Integer affectedRows;
        try (PreparedStatement pstmt = conn.prepareStatement(sqlDelete)) {
            pstmt.setString(1, name);
            affectedRows = pstmt.executeUpdate();
        } catch(SQLException se) {
            return false;
        }
        if(affectedRows>0) return true;
        else return false;
    }

    public ArrayList<Cat> readAllCatFromDB() {
        String sqlSelect="SELECT "+DB_TABLE_NAME_CAT+"."+DB_TABLE_FIELD_ID_CAT+" as cat_id, "+
                            DB_TABLE_NAME_CAT+"."+DB_TABLE_FIELD_NAME_CAT+" as cat_name "+
                            " FROM "+DB_TABLE_NAME_CAT+
                            " ORDER BY "+DB_TABLE_NAME_CAT+"."+DB_TABLE_FIELD_NAME_CAT;
        int i=0;
        ArrayList<Cat> Cats = new ArrayList<>();        
        try (   PreparedStatement pstmt = conn.prepareStatement(sqlSelect);
                ResultSet rs = pstmt.executeQuery() ) {            
            while ( rs.next() && i<MAX_CAT ) {
                Cat cat = new Cat();
                cat.setId(rs.getLong("cat_id"));
                cat.setName(rs.getString ("cat_name"));
                Cats.add(cat);
                i++;
            }            
        }
        catch(SQLException se) {
            return null;
        }        
        return Cats;
        
    }
    
    public ArrayList<Cat> searchCatInDB(Long idMin, Long idMax, Integer nmax) {
        String sqlSelect="SELECT TOP ? "+DB_TABLE_NAME_CAT+"."+DB_TABLE_FIELD_ID_CAT+" as cat_id, "+
                            DB_TABLE_NAME_CAT+"."+DB_TABLE_FIELD_NAME_CAT+" as cat_name, "+
                            " FROM "+DB_TABLE_NAME_CAT +
                            " WHERE "+DB_TABLE_NAME_CAT+"."+DB_TABLE_FIELD_ID_CAT+">=? AND "+DB_TABLE_NAME_CAT+"."+DB_TABLE_FIELD_ID_CAT+"<?"+
                            " ORDER BY "+DB_TABLE_NAME_CAT+"."+DB_TABLE_FIELD_ID_CAT;
        int i=0;
        ArrayList<Cat> Cats = new ArrayList<>();        
        try (PreparedStatement pstmt = conn.prepareStatement(sqlSelect)) {            
            pstmt.setInt(1,nmax);
            pstmt.setLong(2,idMin);
            pstmt.setLong(3,idMax);
            ResultSet rs = pstmt.executeQuery();
            while ( rs.next() && i<MAX_CAT ) {
                Cat cat = new Cat();
                cat.setId(rs.getLong("prod_id"));
                cat.setName(rs.getString ("prod_name"));
                Cats.add(cat);
                i++;
            }            
        }
        catch(SQLException se) {
            return null;
        }        
        return Cats;                
    }
    
    public ArrayList<Cat> searchCatNameInDB(String filter, Integer nmax) {
        String sqlSelect="SELECT TOP ? "+DB_TABLE_NAME_CAT+"."+DB_TABLE_FIELD_ID_CAT+" as cat_id, "+
                            DB_TABLE_NAME_CAT+"."+DB_TABLE_FIELD_NAME_CAT+" as cat_name, "+
                            " FROM "+DB_TABLE_NAME_CAT +
                            " WHERE "+DB_TABLE_NAME_CAT+"."+DB_TABLE_FIELD_NAME_CAT+" LIKE %?%"+
                            " ORDER BY "+DB_TABLE_NAME_CAT+"."+DB_TABLE_FIELD_NAME_CAT;
        int i=0;
        ArrayList<Cat> Cats = new ArrayList<>();        
        try (PreparedStatement pstmt = conn.prepareStatement(sqlSelect)) {            
            pstmt.setInt(1,nmax);
            pstmt.setString(2,filter);
            ResultSet rs = pstmt.executeQuery();
            while ( rs.next() && i<MAX_CAT ) {
                Cat cat = new Cat();
                cat.setId(rs.getLong("prod_id"));
                cat.setName(rs.getString ("prod_name"));
                Cats.add(cat);
                i++;
            }            
        }
        catch(SQLException se) {
            return null;
        }        
        return Cats;                        
    }

    public Long addOrderToDB(Order order) {
        if(!isValid(order)) return 0L;
        Long orderId=0L;
        String sqlInsert = "INSERT INTO "+DB_TABLE_NAME_ORDER+"("+DB_TABLE_FIELD_NAME_ORDER+") VALUES(?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sqlInsert, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, order.getName());
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet rs = pstmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        orderId=rs.getLong(1);
                    }
                    else orderId=0L;
                } catch(SQLException se) {
                    orderId=0L;
                }
            }
            else orderId=0L;
        } catch(SQLException se) {
            orderId=0L;
        }
        if(orderId==0L) return 0L;
        else order.setId(orderId);
        
        String sqlDelete = "DELETE FROM "+DB_TABLE_NAME_OP+" WHERE "+DB_TABLE_FIELD_ID_ORDER_OP+"=?";
        try (PreparedStatement pstmt = conn.prepareStatement(sqlDelete)) {
            pstmt.setLong(1, order.getId());
            pstmt.executeUpdate();
        } catch(SQLException se) {
        }
        
        if(order.getOrderlines().isEmpty()) return orderId;
        
        for(OrderLine ol : order.getOrderlines()) {
            addOrderLine(order.getId(), ol);
        }        
        return orderId;
    }
    
    public Boolean addOrderLine(Long idorder, OrderLine oline) {
        String sqlInsert = "INSERT INTO "+DB_TABLE_NAME_OP+
                    "("+DB_TABLE_FIELD_ID_ORDER_OP+","+DB_TABLE_FIELD_ID_PROD_OP+","+DB_TABLE_FIELD_NUM_OP+") VALUES(?,?,?)";
        int affectedRows=0;
        try (PreparedStatement pstmt = conn.prepareStatement(sqlInsert, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setLong(1, idorder);
            pstmt.setLong(2, oline.getProduct().getId());
            pstmt.setInt(3, oline.getNumproducts());
            affectedRows = pstmt.executeUpdate();
        } catch(SQLException se) {
            return false;
        }
        if(affectedRows>0) return true;
        else return false;
    }
    
    public Integer getNumProdInOrder(Long idprod, Long idorder) {
        String sqlSelect =  "SELECT "+DB_TABLE_FIELD_NUM_OP+" FROM "+DB_TABLE_NAME_OP+
                            " WHERE "+DB_TABLE_FIELD_ID_ORDER_OP+"=?"+" AND "+DB_TABLE_FIELD_ID_PROD_OP+"=?";
        Integer result=0;
        try (PreparedStatement pstmt = conn.prepareStatement(sqlSelect)) {
            pstmt.setLong(1, idorder);
            pstmt.setLong(2, idprod);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    result=rs.getInt("num");
                }
                rs.close();
            }  catch(SQLException se) {
                result=-1;
            }
        } catch(SQLException se) {
            result=-1;
        }
        return result;
    }
    
    public Boolean incdecOrderLine(Long idprod, Long idorder, Integer step) {
        if(step==0) return false;
        Integer curNum=getNumProdInOrder(idprod,idorder);
        if(curNum<=0) return false;
        if(step<0 && curNum==1) return removeOrderLineFromDB(idprod,idorder);
        String strStep = step>0 ? "+"+step.toString() : step.toString();
        String sqlUpdate = "UPDATE "+DB_TABLE_NAME_OP+" SET "+DB_TABLE_FIELD_NUM_OP+"="+DB_TABLE_FIELD_NUM_OP+strStep+
                           " WHERE "+DB_TABLE_FIELD_ID_ORDER_OP+"=?"+" AND "+DB_TABLE_FIELD_ID_PROD_OP+"=?";
        try (PreparedStatement pstmt = conn.prepareStatement(sqlUpdate)) {
            pstmt.setLong(1, idorder);
            pstmt.setLong(2, idprod);
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                return true;
            }
            else return false;
        } catch(SQLException se) {
            return false;
        }
    }
    
    public Boolean removeOrderLineFromDB(Long idprod, Long idorder) {
        String sqlDelete="DELETE FROM "+DB_TABLE_NAME_OP+
                                      " WHERE "+DB_TABLE_FIELD_ID_ORDER_OP+"=?"+
                                      " AND "+DB_TABLE_FIELD_ID_PROD_OP+"=?";
        Integer affectedRows;
        try (PreparedStatement pstmt = conn.prepareStatement(sqlDelete)) {
            pstmt.setLong(1, idorder);
            pstmt.setLong(2, idprod);
            affectedRows = pstmt.executeUpdate();
        } catch(SQLException se) {
            return false;
        }
        if(affectedRows>0) return true;
        else return false;
    }
    
    public Boolean decreaseOrderLine(Long idprod, Long idorder) {
        return incdecOrderLine(idprod, idorder, -1);
    }
    
    public Boolean increaseOrderLine(Long idprod, Long idorder) {
        return incdecOrderLine(idprod, idorder, 1);        
    }
            
    public Order readOrderFromDB(Long id) {
        String sqlSelectOrder = "SELECT "+DB_TABLE_NAME_ORDER+"."+DB_TABLE_FIELD_ID_ORDER+" as order_id, "+
                            DB_TABLE_NAME_ORDER+"."+DB_TABLE_FIELD_NAME_ORDER+" as order_name "+
                            " FROM "+DB_TABLE_NAME_ORDER +
                            " WHERE "+DB_TABLE_NAME_ORDER+"."+DB_TABLE_FIELD_ID_ORDER+"=?";
        Order order=new Order();
        
        try (PreparedStatement pstmt = conn.prepareStatement(sqlSelectOrder)) {
            pstmt.setLong(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    order.setId(rs.getLong("order_id"));
                    order.setName(rs.getString ("order_name"));
                }
                else return null;
            }  catch(SQLException se) {
            return null;
            }
        } catch(SQLException se) {
            return null;
        }
        
        String sqlSelectLines="SELECT "+DB_TABLE_NAME_PROD+"."+DB_TABLE_FIELD_ID_PROD+" as prod_id, " +
                DB_TABLE_NAME_PROD+"."+DB_TABLE_FIELD_NAME_PROD+" as prod_name, " +
                DB_TABLE_NAME_PROD+"."+DB_TABLE_FIELD_ID_CAT_PROD+" as cat_id, "+
                DB_TABLE_NAME_CAT+"."+DB_TABLE_FIELD_NAME_CAT+" as cat_name, " +
                DB_TABLE_NAME_OP+"."+DB_TABLE_FIELD_NUM_OP+" as prod_num" +
                " FROM "+DB_TABLE_NAME_PROD+","+DB_TABLE_NAME_CAT+","+DB_TABLE_NAME_OP +
                " WHERE "+DB_TABLE_NAME_PROD+"."+DB_TABLE_FIELD_ID_CAT_PROD+"="+DB_TABLE_NAME_CAT+"."+DB_TABLE_FIELD_ID_CAT +
                " AND "+DB_TABLE_NAME_PROD+"."+DB_TABLE_FIELD_ID_PROD+"="+DB_TABLE_NAME_OP+"."+DB_TABLE_FIELD_ID_PROD_OP +
                " AND "+DB_TABLE_NAME_OP+"."+DB_TABLE_FIELD_ID_ORDER_OP+"=?" +
                " ORDER BY "+DB_TABLE_NAME_CAT+"."+DB_TABLE_FIELD_NAME_CAT+", "+DB_TABLE_NAME_PROD+"."+DB_TABLE_FIELD_NAME_PROD;

        try (PreparedStatement pstmt = conn.prepareStatement(sqlSelectLines)) {            
            pstmt.setLong(1,id);
            ResultSet rs = pstmt.executeQuery();
            while ( rs.next() ) {
                Prod prod = new Prod();
                prod.setId(rs.getLong("prod_id"));
                prod.setName(rs.getString ("prod_name"));
                prod.setCatname(rs.getString ("cat_name"));
                prod.setCatid(rs.getLong("cat_id"));
                OrderLine ol=new OrderLine(prod,rs.getInt("prod_num"));
                order.addLine(ol);
            }
            rs.close();
        }
        catch(SQLException se) {
            return null;
        }        
        return order;
    }    

    public Order readOrderNameFromDB(String name) {
        String sqlSelect="SELECT "+DB_TABLE_NAME_ORDER+"."+DB_TABLE_FIELD_ID_ORDER+" as order_id, "+
                            DB_TABLE_NAME_ORDER+"."+DB_TABLE_FIELD_NAME_ORDER+" as order_name "+
                            " FROM "+DB_TABLE_NAME_ORDER +
                            " WHERE "+DB_TABLE_NAME_ORDER+"."+DB_TABLE_FIELD_NAME_ORDER+"=?";
        Order order=new Order();
        
        try (PreparedStatement pstmt = conn.prepareStatement(sqlSelect)) {
            pstmt.setString(1, name);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    order.setId(rs.getLong("order_id"));
                    order.setName(rs.getString ("order_name"));
                    return order;
                }
                else return null;
            }  catch(SQLException se) {
            return null;
            }
        } catch(SQLException se) {
            return null;
        }                
    }
    
    public Boolean removeOrderFromDB(Long id) {
        String sqlDeleteOrder="DELETE FROM "+DB_TABLE_NAME_ORDER+" WHERE "+DB_TABLE_NAME_ORDER+"."+DB_TABLE_FIELD_ID_ORDER+"=?";
        String sqlDeleteOrderProducts="DELETE FROM "+DB_TABLE_NAME_OP+" WHERE "+DB_TABLE_NAME_OP+"."+DB_TABLE_FIELD_ID_ORDER_OP+"=?";
        Integer affectedRowsOrder=0;
        Integer affectedRowsOrderProducts=0;
        try (PreparedStatement pstmt = conn.prepareStatement(sqlDeleteOrderProducts)) {
            pstmt.setLong(1, id);
            affectedRowsOrderProducts = pstmt.executeUpdate();
        } catch(SQLException se) {
        }
        try (PreparedStatement pstmt = conn.prepareStatement(sqlDeleteOrder)) {
            pstmt.setLong(1, id);
            affectedRowsOrder = pstmt.executeUpdate();
        } catch(SQLException se) {
        }
        if( (affectedRowsOrder+affectedRowsOrderProducts)>0 ) return true;
        else return false;        
    }
    
    public Boolean removeOrderNameFromDB(String name) {
        Order order = readOrderNameFromDB(name);
        if(order==null) return false;
        else return removeOrderFromDB(order.getId());
    }
    
    public Boolean saveOrderNameToDB(Order ord) {
        String sqlUpdate = "UPDATE "+DB_TABLE_NAME_ORDER+" SET "+DB_TABLE_FIELD_NAME_ORDER+"=? WHERE "+DB_TABLE_FIELD_ID_ORDER+"=?";
        try (PreparedStatement pstmt = conn.prepareStatement(sqlUpdate)) {
            pstmt.setString(1, ord.getName());
            pstmt.setLong(2, ord.getId());
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                return true;
            }
            else return false;
        } catch(SQLException se) {
            return false;
        }        
    }
    
    public ArrayList<Order> readAllOrdersFromDB() {
        String sqlSelect="SELECT "+DB_TABLE_NAME_ORDER+"."+DB_TABLE_FIELD_ID_ORDER+" as order_id "+
                            " FROM "+DB_TABLE_NAME_ORDER;
        ArrayList<Long> orderIds = new ArrayList<>();
        ArrayList<Order> orders = new ArrayList<>();
        try (PreparedStatement pstmt = conn.prepareStatement(sqlSelect)) {
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    orderIds.add(rs.getLong("order_id"));
                }
            }  catch(SQLException se) {
            return null;
            }
        } catch(SQLException se) {
            return null;
        }
        if(orderIds.isEmpty()) return null;
        for(Long id : orderIds) {
            Order order=readOrderFromDB(id);
            if(order!=null) orders.add(order);
        }
        return orders;
    }
    
    public ArrayList<Order> searchOrdersInDB(Long idMin, Long idMax, Integer nmax) {
        String sqlSelect="SELECT TOP ? "+DB_TABLE_NAME_ORDER+"."+DB_TABLE_FIELD_ID_ORDER+" as order_id "+
                            " FROM "+DB_TABLE_NAME_ORDER +
                            " WHERE "+DB_TABLE_NAME_ORDER+"."+DB_TABLE_FIELD_ID_ORDER+">=? AND "+
                            DB_TABLE_NAME_ORDER+"."+DB_TABLE_FIELD_ID_ORDER+"<?"+
                            " ORDER BY "+DB_TABLE_NAME_ORDER+"."+DB_TABLE_FIELD_ID_ORDER;
        ArrayList<Long> orderIds = new ArrayList<>();
        ArrayList<Order> orders = new ArrayList<>();
        try (PreparedStatement pstmt = conn.prepareStatement(sqlSelect)) {
            pstmt.setInt(1,nmax);
            pstmt.setLong(2,idMin);
            pstmt.setLong(3,idMax);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    orderIds.add(rs.getLong("order_id"));
                }
            }  catch(SQLException se) {
            return null;
            }
        } catch(SQLException se) {
            return null;
        }
        if(orderIds.isEmpty()) return null;
        for(Long id : orderIds) {
            Order order=readOrderFromDB(id);
            if(order!=null) orders.add(order);
        }
        return orders;        
    }
    
    protected Boolean isValid(Prod prod) {
        if(prod==null) return false;
        if(prod.getName()==null) return false;
        if(prod.getCatname()==null) return false;
        if(prod.getCatid()==0) return false;
        if(prod.getName().isEmpty()) return false;
        if(prod.getCatname().isEmpty()) return false;
        return true;
    }
    
    protected Boolean isValid(Cat cat) {
        if(cat==null) return false;
        if(cat.getName()==null) return false;
        if(cat.getName().isEmpty()) return false;        
        return true;
    }

    protected Boolean isValid(Order order) {
        if(order==null) return false;
        if(order.getName()==null) return false;
        if(order.getName().isEmpty()) return false;        
        return true;
    }

    public ArrayList<OrderName> readAllOrderNamesFromDB() {
        String sqlSelect="SELECT "+DB_TABLE_NAME_ORDER+"."+DB_TABLE_FIELD_ID_ORDER+" as order_id, "+
                            DB_TABLE_NAME_ORDER+"."+DB_TABLE_FIELD_NAME_ORDER+" as order_name "+
                            " FROM "+DB_TABLE_NAME_ORDER+
                            " ORDER BY "+DB_TABLE_NAME_ORDER+"."+DB_TABLE_FIELD_NAME_ORDER;
        int i=0;
        ArrayList<OrderName> OrderNames = new ArrayList<>();        
        try (   PreparedStatement pstmt = conn.prepareStatement(sqlSelect);
                ResultSet rs = pstmt.executeQuery() ) {            
            while ( rs.next() && i<MAX_ORDER ) {
                OrderName ordername = new OrderName();
                ordername.setId(rs.getLong("order_id"));
                ordername.setName(rs.getString ("order_name"));
                OrderNames.add(ordername);
                i++;
            }            
        }
        catch(SQLException se) {
            return null;
        }        
        return OrderNames;
        
    }
    
}
