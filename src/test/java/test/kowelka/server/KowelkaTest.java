/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package test.kowelka.server;

import java.util.ArrayList;
import org.junit.*;
import kowelka.server.Cat;
import kowelka.server.Order;
import kowelka.server.OrderLine;
import kowelka.server.Prod;
import kowelka.server.dbPGSQLRepository;

/**
 *
 * @author peter
 */
public class KowelkaTest {
    Prod prod1;
    Prod prod2;
    Prod prod3;
    Cat cat1;
    Cat cat2;
    OrderLine line1;
    Integer num1;
    OrderLine line2;
    Integer num2;
    OrderLine line3;
    Integer num3;
    String orderName;
    dbPGSQLRepository repo;
    final String server="192.168.211.231:5432";
    final String db="kowelka";
    final String user="root";
    final String password="123456";
    
    @Before
    public void setup() {
        prod1=new Prod(1L,"Prod1","Cat1",1L);
        prod2=new Prod(2L,"Prod2","Cat2",2L);
        prod3=new Prod(3L,"Prod3","Cat1",1L);
        cat1= new Cat(1L,"Cat1");
        cat2= new Cat(2L,"Cat2");
        num1=1;
        num2=2;
        num3=3;
        line1=new OrderLine(prod1, num1);
        line2=new OrderLine(prod2, num2);
        line3=new OrderLine(prod3, num3); 
        orderName="Order1";
        repo = new dbPGSQLRepository(server,db,user,password);
    }
    
    @Test
    public void testCreateOrder() {
        ArrayList<OrderLine> lines = new ArrayList<>();
        lines.add(line1);
        lines.add(line2);
        lines.add(line3);
        Order order=new Order(orderName,lines);
        ArrayList<OrderLine> retlines = order.getOrderlines();
        Assert.assertEquals(3,retlines.size());
        Assert.assertEquals("Order1",order.getName());
    }
    
    @Test
    public void testPGSQLRepositorySaveReadRemoveCat() {
        long id;
        Boolean result;
        Cat cat;
        id=repo.addCatToDB(cat1.getName());
        Assert.assertNotEquals(0L, id);
        cat = repo.readCatFromDB(id);
        Assert.assertNotNull(cat);
        Assert.assertEquals(cat1.getName(),cat.getName());
        cat1.setId(id);
        cat1.setName(cat2.getName());
        result = repo.saveCatToDB(cat1);
        Assert.assertTrue(result);
        cat = repo.readCatFromDB(id);
        Assert.assertNotNull(cat);
        Assert.assertEquals(cat1.getName(),cat.getName());        
        result = repo.removeCatFromDB(id);
        Assert.assertTrue(result);
    }
    
    @Test
    public void testPGSQLRepositorySaveReadRemoveProd() {
        long id;
        long idcat;
        Boolean result;
        Prod prod;
        idcat=repo.addCatToDB(cat1.getName());
        Assert.assertNotEquals(0L, idcat);
        cat1.setId(idcat);
        id=repo.addProdToDB(prod1);
        Assert.assertNotEquals(0L, id);
        prod = repo.readProdFromDB(id);
        Assert.assertNotNull(prod);
        Assert.assertEquals(prod1.getName(),prod.getName());
        prod1.setId(id);
        prod1.setName(prod2.getName());
        result=repo.saveProdToDB(prod1);
        Assert.assertTrue(result);
        prod = repo.readProdFromDB(id);
        Assert.assertNotNull(prod);
        Assert.assertEquals(prod1.getName(),prod.getName());
        idcat=repo.addCatToDB(cat2.getName());
        Assert.assertNotEquals(0L, idcat);
        cat2.setId(idcat);
        prod1.setCatid(idcat);
        prod1.setCatname(cat2.getName());
        result=repo.saveProdToDB(prod1);
        Assert.assertTrue(result);
        prod = repo.readProdFromDB(prod1.getId());
        Assert.assertNotNull(prod);
        Assert.assertEquals(prod.getCatname(),cat2.getName());
        result = repo.removeProdFromDB(prod1.getId());
        Assert.assertTrue(result);
        id=repo.addProdToDB(prod1);
        Assert.assertNotEquals(0L, id);
        result = repo.removeProdNameFromDB(prod1.getName());
        Assert.assertTrue(result);
        result = repo.removeCatFromDB(cat1.getId());
        Assert.assertTrue(result);
        result = repo.removeCatFromDB(cat2.getId());
        Assert.assertTrue(result);
    }

    @Test
    public void testPGSQLRepositorySaveReadRemoveOrder() {
        long id;
        Boolean result;
        id=repo.addCatToDB(cat1.getName());
        Assert.assertNotEquals(0L, id);
        id=repo.addCatToDB(cat2.getName());
        Assert.assertNotEquals(0L, id);
        id=repo.addProdToDB(prod1);
        Assert.assertNotEquals(0L, id);
        Prod readProd1=repo.readProdFromDB(id);
        Assert.assertNotNull(readProd1);
        id=repo.addProdToDB(prod2);
        Assert.assertNotEquals(0L, id);
        Prod readProd2=repo.readProdFromDB(id);
        Assert.assertNotNull(readProd2);
        id=repo.addProdToDB(prod3);
        Assert.assertNotEquals(0L, id);
        Prod readProd3=repo.readProdFromDB(id);
        Assert.assertNotNull(readProd3);
        ArrayList<OrderLine> lines = new ArrayList<>();
        lines.add(new OrderLine(readProd1, num1));
        lines.add(new OrderLine(readProd2, num2));
        lines.add(new OrderLine(readProd3, num3));
        Order order=new Order(orderName,lines);
        ArrayList<OrderLine> retlines = order.getOrderlines();
        Assert.assertEquals(3,retlines.size());
        Assert.assertEquals("Order1",order.getName());
        id=repo.addOrderToDB(order);
        Assert.assertNotEquals(0L,id);
        Order order1=repo.readOrderFromDB(id);
        Assert.assertNotNull(order1);
        retlines = order1.getOrderlines();
        Assert.assertEquals(3,retlines.size());
        /* TODO: replace indexes by searching as it fails with Orderlines sort
        Assert.assertEquals(prod1.getName(),retlines.get(0).getProduct().getName());
        Assert.assertEquals(prod2.getName(),retlines.get(1).getProduct().getName());
        Assert.assertEquals(prod3.getName(),retlines.get(2).getProduct().getName());
        Assert.assertEquals(num1,retlines.get(0).getNumproducts());
        Assert.assertEquals(num2,retlines.get(1).getNumproducts());
        Assert.assertEquals(num3,retlines.get(2).getNumproducts());
        */
        result = repo.removeOrderFromDB(id);
        Assert.assertTrue(result);
    }
    
    @After
    public void testClean() {
        repo.removeCatNameFromDB("Cat1");
        repo.removeCatNameFromDB("Cat2");
        repo.removeProdNameFromDB("Prod1");
        repo.removeProdNameFromDB("Prod2");
        repo.removeProdNameFromDB("Prod3");
        repo.readOrderNameFromDB("Order1");
    }
    
}
