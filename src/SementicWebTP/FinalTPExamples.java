package SementicWebTP;

/**
 *
 * @author Rehamnia8
 */
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Iterator;
import com.hp.hpl.jena.ontology.DatatypeProperty;
import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.ObjectProperty;
import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.rdf.model.InfModel;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.ModelFactoryBase;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.reasoner.ValidityReport;
import com.hp.hpl.jena.util.FileManager;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;
import java.util.Scanner;

import com.hp.hpl.jena.ontology.*;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.query.ResultSetFormatter;
import com.hp.hpl.jena.rdf.model.*;
import com.hp.hpl.jena.util.*;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;

public class FinalTPExamples {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            System.out.println("Interacting with local owl file (offline):");
            LocalOWLFileInteract("clinic_rdf.owl");
            System.out.println("Interacting with the DBPedia  (online):");
            DBPediaInteract();

        } catch (Exception e) {
            System.err.println("exception : " + e);
        }
    }

    public static void DBPediaInteract() {
        FileManager.get().addLocatorClassLoader(FinalTPExamples.class.getClassLoader());
        Model model = FileManager.get().loadModel("tp.owl");
        String prefixes = "PREFIX dbr: <http://dbpedia.org/resource/> \n"
                + "PREFIX dbo: <http://dbpedia.org/ontology/>\n";
        String q1 = prefixes
                + "select ?hotel where {\n"
                + "?hotel a dbo:Hotel .\n"
                + "?hotel dbo:location dbr:Algeria\n"
                + "}\n"
                + "limit 3";
        String q2 = prefixes
                + "select ?hotel where\n"
                + "{\n"
                + "  ?hotel a dbo:Hotel.\n"
                + "  {\n"
                + "    ?hotel dbo:location dbr:Istanbul.\n"
                + "  }\n"
                + "  UNION\n"
                + "  {\n"
                + "    ?hotel dbo:location dbr:Algeria.\n"
                + "  }\n"
                + "}\n"
                + "  limit 7";
        String q3 = "select ?hotel ?value where { \n"
                + "{\n"
                + "?hotel a <http://dbpedia.org/ontology/Hotel> .\n"
                + "?hotel dbo:location dbr:Algeria\n"
                + "}\n"
                + "{\n"
                + "?hotel  <http://dbpedia.org/ontology/numberOfRooms> ?value .\n"
                + "}\n"
                + "}\n"
                + "ORDER BY DESC(?value)";
        String q5 = "select ?city ?abstract  where {\n"
                + "{\n"
                + "?city a <http://dbpedia.org/ontology/City> .\n"
                + "}\n"
                + "{\n"
                + "?city foaf:isPrimaryTopicOf wikipedia-en:Algiers .\n"
                + "?city dbo:abstract ?abstract .\n"
                + "FILTER (LANGMATCHES(LANG(?abstract), 'de'))\n"
                + "}\n"
                + "UNION\n"
                + "{\n"
                + "?city foaf:isPrimaryTopicOf wikipedia-en:Algiers .\n"
                + "?city dbo:abstract ?abstract .\n"
                + "FILTER (LANGMATCHES(LANG(?abstract), 'it'))\n"
                + "}\n"
                + "}";
        String q6 = "select ?hotel  where {\n"
                + "?hotel a <http://dbpedia.org/ontology/Hotel> .\n"
                + " FILTER regex(?hotel, \"Alger\", \"i\")\n"
                + "}";

        Query query = QueryFactory.create(q1);
        QueryExecution qexec = QueryExecutionFactory.sparqlService("https://dbpedia.org/sparql", query);
        ResultSet results = qexec.execSelect();
        ResultSetFormatter.out(System.out, results, query);
        qexec.close();
    }

    public static void LocalOWLFileInteract(String file_path) {

        FileManager.get().addLocatorClassLoader(FinalTPExamples.class.getClassLoader());
        Model model = FileManager.get().loadModel(file_path);
        String prefixes = "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n"
                + "PREFIX owl: <http://www.w3.org/2002/07/owl#>\n"
                + "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n"
                + "PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>\n"
                + "PREFIX c: <http://www.semanticweb.org/rehamnia8/ontologies/2022/2/clinic#>";

        String q0 = prefixes + "CONSTRUCT { ?y rdf:type ?x } \n"
                + "WHERE { ?u rdfs:subClassOf ?x . ?y rdf:type ?u }";

        String q1 = prefixes + "select  ?d ?m\n"
                + "where \n"
                + "{\n"
                + "?d a c:Doctor.\n"
                + "?m a c:Malade.\n"
                + "?m c:hasdoctor ?d\n"
                + "}\n"
                + "limit 2";
        String q2 = prefixes + "select  ?m ?d ?h\n"
                + "where \n"
                + "{\n"
                + "?d a c:Doctor.\n"
                + "?m a c:Malade.\n"
                + "?h a c:CHU.\n"
                + "?m c:hasdoctor ?d.\n"
                + "?d c:worked ?h\n"
                + "}\n"
                + "limit 2";
        String q3 = "select ?m ?t\n"
                + "where\n"
                + "{\n"
                + "?m a c:Malade.\n"
                + "?d  a c:Doctor.\n"
                + "?t a c:TOWN.\n"
                + "?m c:hasAdress ?t.\n"
                + "?m c:hasdoctor ?d\n"
                + "}";
        System.out.println("Result : ");
        Query query = QueryFactory.create(q2);
        QueryExecution qexec = QueryExecutionFactory.create(query, model);
        ResultSet results = qexec.execSelect();
        ResultSetFormatter.out(System.out, results, query);
    }

}
