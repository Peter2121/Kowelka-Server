/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package kowelka.server;

import com.coxautodev.graphql.tools.SchemaParser;
import javax.servlet.annotation.WebServlet;
import graphql.servlet.SimpleGraphQLServlet;
import graphql.schema.GraphQLSchema;


@WebServlet(urlPatterns = "/graphql/*")
public class GraphQLEndpoint extends SimpleGraphQLServlet {
    /*
    private final static String server="192.168.211.231:5432";
    private final static String db="kowelka";
    private final static String user="root";
    private final static String password="123456";
    */
    private final static String server="192.168.101.45:5432";
    private final static String db="kowelka";
    private final static String user="root";
    private final static String password="********";
    
    public GraphQLEndpoint() {
        super(buildSchema());
    }
    
    private static GraphQLSchema buildSchema() {
        dbPGSQLRepository repo = new dbPGSQLRepository(server,db,user,password);
        return SchemaParser.newParser()
                .file("schema.graphqls")
                .resolvers(new Query(repo), new Mutation(repo))
                .build()
                .makeExecutableSchema();
    }
    
}
