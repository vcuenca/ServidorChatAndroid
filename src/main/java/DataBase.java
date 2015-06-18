package main.java;

import java.util.List;

import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

public class DataBase {

	private static Configuration cfg;
	private static Session session;

	public static void startDB(String config){
		cfg = new Configuration();
	    cfg.configure("main/hibernate/hibernate.cfg.xml");//populates the data of the configuration file
	      
	    //creating seession factory object  

	}


	private static void openSession(){
		SessionFactory factory=cfg.buildSessionFactory();
		//creating session object
		session = factory.openSession();
	}
	
	public static void storeObject(Object obj){
		openSession();
		Transaction t = session.beginTransaction();
		session.persist(obj);//persisting the object
	    t.commit();//transaction is committed
		session.close();
	}
	
	public static List<Object> executeQuery(String query){
		openSession();
		//from mensaje
		List<Object> uList = session.createQuery(query).list();
		session.close();
		return uList;
	}

	public static void executeUpdate(String query){
		openSession();
		Transaction t = session.beginTransaction();
		SQLQuery sqlQuery = session.createSQLQuery(query);
		sqlQuery.executeUpdate();
		t.commit();
		session.close();
	}
	
	public static void closeSession(){
		session.close();
	}
}
