package main.java;

public class Main {

	public static final int OK = 0;
	
	public static void main(String[] args) {
		//Iniciamos la base da datos
		DataBase.startDB("src/main/hibernate.cfg.xml");
		
		ThreadServidor tServer = new ThreadServidor(Properties.SERVER_PORT);
		
		tServer.start();
		
	}
}
