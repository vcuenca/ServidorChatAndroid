package main.java;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

public class DataBase {

	private static Session session;
	private static Transaction t;
	
	public static void startDB(String config){
		Configuration cfg = new Configuration();  
	    cfg.configure("main/hibernate/hibernate.cfg.xml");//populates the data of the configuration file
	      
	    //creating seession factory object  
	    SessionFactory factory=cfg.buildSessionFactory();  
	      
	    //creating session object  
	    session=factory.openSession();  
	      
	    //creating transaction object  
	    t = session.beginTransaction();
	}
	
	public static void storeObject(Object obj){
		session.persist(obj);//persisting the object  
	    t.commit();//transaction is committed

	}
	
	public static List<Object> executeQuery(String query){
		//from mensaje
		List<Object> uList = session.createQuery(query).list();
		return uList;
	}
	
	public static void closeSession(){
		session.close();
	}
}
