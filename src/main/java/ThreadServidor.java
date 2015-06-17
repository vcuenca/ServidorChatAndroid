package main.java;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.ResultSet;
import java.sql.SQLException;


public class ThreadServidor extends Thread{
	
	private int port;
	private ServerSocket server;
	private boolean bStop = false;
	
	public ThreadServidor(int port){
		this.port = port;
	}
	
	public void run(){
		
		try {
			server = new ServerSocket(port);
			System.out.println("Servidor escuchando en el puerto: " + port);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		while (!bStop){
			ObjectInputStream in = null;
			ObjectOutputStream out = null;
			Socket cliente = null;

			try {
				cliente = server.accept();
				in = new ObjectInputStream(cliente.getInputStream());
				out = new ObjectOutputStream(cliente.getOutputStream());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			System.out.println("Cliente conectado al servidor!!!");
			//Ser�a interesante crear el thread una vez se haya autenticado el cliente, DOS !!!
			ClientThread clientThread = new ClientThread(cliente, out, in);
			clientThread.start();
			//Autenticacion
			
			
			/*
			try {
				String imei = (String)in.readObject();
				System.out.println(imei);
				String ip = (String)in.readObject();
				System.out.println(ip);
				
				//Miramos si existe ya el imei en la base de datos
				ResultSet rs = Main.executeSelect("SELECT * FROM IMEI_IP where imei = '" + imei + "';");
				
				try {
					if (!rs.next()){
						//No existe, creamos la correspondiente entrada
						//System.out.println("No existe");
						Main.executeQuery("INSERT INTO IMEI_IP VALUES ('" + imei + "', '" + ip + "')");
					}else{
						//Existe, actualizamos la ip
						//System.out.println("Existe");
						int resultado = Main.executeQuery("UPDATE IMEI_IP set IP = '" + ip + "' where IMEI = '" + imei + "'");
					}
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			*/
			
		}
	}
}
