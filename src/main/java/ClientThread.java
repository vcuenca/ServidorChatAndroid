package main.java;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.*;

import com.google.android.gcm.server.Message;
import com.google.android.gcm.server.Sender;

public class ClientThread extends Thread {

	private Socket clientSocket = null;
	private ObjectOutputStream out = null;
	private ObjectInputStream in = null;

	public ClientThread(Socket clientSocket, ObjectOutputStream out,
			ObjectInputStream in) {
		this.clientSocket = clientSocket;
		this.out = out;
		this.in = in;
	}

	public void run() {
		Integer mode = 0;
		try {
			while ((mode = (Integer) in.readObject()) != -1) {
				if (mode == Properties.SEND_MY_MESSAGES) {

					System.out.println("LOGIN");
					/*
					 * Cuando le llegue la notificaci�n push al usuario, el
					 * usuario enviar� un mensaje SEND_MY_MESSAGES para que el
					 * servidor le envie todos los mensajes pendientes que tenga
					 * para el.
					 */
					String user = (String) in.readObject();
					// String password = (String) in.readObject();

					// if (isPasswordCorrect(user, password)) {
					// out.writeObject(new String("OK"));
					sendConversations(user);
					// }
				} else if (mode == Properties.REGISTER_USER) {
					registerUser();
				} else if (mode == Properties.NEW_MESSAGE) {
					// Guardamos el mensaje en la BD para su posterior entrega.
					insertarMensaje();
				} else if (mode == Properties.SEARCH_CONTACT) {
					System.out.println("Buscando contactos");
					sendContactsResult();
				} else if (mode == Properties.RECEIVE_CONFIRMATION) {
					receiveConfirmation();
				}
			}
		} catch (ClassNotFoundException cne) {
			// TODO Auto-generated catch block
			cne.printStackTrace();
		} catch (IOException ioe) {

		}
	}

	private void sendContactsResult() throws IOException,
			ClassNotFoundException {
		String contactName = (String) in.readObject();

		List<Object> usuarios = DataBase
				.executeQuery("FROM User WHERE nombre LIKE '%" + contactName
						+ "%'");
		ArrayList<String> contacts = new ArrayList<String>();

		for (Object u : usuarios) {
			contacts.add(((User) u).getUser());
		}

		System.out.println(contacts.size());

		out.writeObject(contacts);

	}

	// PROBADA
	private boolean authenticateUser() {
		boolean resultado = false;

		try {
			String user = (String) in.readObject();
			String password = (String) in.readObject();

			System.out.println(user + password);

			if (isPasswordCorrect(user, password)) {
				out.writeObject("OKKKK");
				System.out.println("USUARIO CORRECTO");
			} else
				out.writeObject("NO");

		} catch (ClassNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		return resultado;
	}

	// PROBADA
	private void registerUser() {
		try {
			String user = (String) in.readObject();
			String password = (String) in.readObject();
			String cloudID = (String) in.readObject();
			System.out.println(user + password);

			if (!checkIfUserExists(user)) {
				out.writeObject("OK");
				User user1 = new User();
				user1.setUser(user);
				user1.setPassword(password);
				user1.setGcm(cloudID);
				user1.setPhoneNumber(1234);
				user1.setMail("hhh");

				DataBase.storeObject(user1);
				// Main.executeQuery("INSERT INTO USUARIOS VALUES ('" + user +
				// "','" + password + "', '" + cloudID + "');");
			} else
				out.writeObject("NO");
		} catch (ClassNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}

	// PROBADA
	private boolean checkIfUserExists(String user) {
		boolean resultado = false;
		List<Object> usuarios = DataBase
				.executeQuery("FROM User WHERE nombre = '" + user + "'");
		// ResultSet rs =
		// Main.executeSelect("SELECT * FROM USUARIOS WHERE USUARIO = '" + user
		// + "';");
		// System.out.println("SELECT * FROM USUARIOS WHERE USUARIO = '" + user
		// + "';");

		if (!usuarios.isEmpty())
			resultado = true;

		return resultado;
	}

	// No PROBADA
	private boolean isPasswordCorrect(String user, String password) {
		boolean resultado = false;
		List<Object> usuarios = DataBase
				.executeQuery("from User where nombre = '" + user
						+ "' and pass = '" + password + "'");

		// ResultSet rs =
		// Main.executeSelect("select * from usuarios where usuario = '" + user
		// + "' and password = '" + password + "'");

		if (!usuarios.isEmpty())
			resultado = true;

		return resultado;
	}

	// PROBADA
	private void insertarMensaje() {
		try {
			Mensaje message = (Mensaje) in.readObject();
			System.out.println("Mensaje recibido");
			System.out.println(message.getFrom());
			System.out.println(message.getReceiver());
			System.out.println(message.getMessage());
			System.out.println(message.getFecha());
			DataBase.storeObject(message);
			
			int id = (int) DataBase
					.executeQuery("SELECT id FROM Mensaje WHERE destino = '"
							+ message.getReceiver() + "' and fecha='"+message.getFecha()+"' and origen='"+message.getFrom()+"' and texto = '"+message.getMessage()+"'").get(0);
			message.setId(id);
			// el parametro es el contacto al cual va
			sendPushNotification(message.getReceiver(), message);
		} catch (ClassNotFoundException cne) {
			// TODO Auto-generated catch block
			cne.printStackTrace();
		} catch (IOException ioe) {

		}
	}

	private void sendPushNotification(String receiver, Mensaje msn) {
		// Consultamos de la BD el cloudID
		// ResultSet rs =
		// Main.executeSelect("SELECT CLOUDID FROM USUARIO_IP WHERE USUARIO = '"
		// + receiver + "';");
		List<Object> usuarios = DataBase
				.executeQuery("select gcm from User WHERE userID = '"
						+ receiver + "'");
		System.out.println(usuarios.size());
		System.out.println("GCM: " + usuarios.get(0));

		String cloudID = "";

		if (!usuarios.isEmpty()) {
			// Si existe el cloudID para el destinatario, mandamos la
			// notificacion.
			cloudID = (String) usuarios.get(0);
			// Enviamos la notificacion
			Sender sender = new Sender(Properties.GOOGLE_SERVER_KEY);
			String textMessage = JsonUtilities.objetcToJson(msn);

			// 2419200 timeToLive es el m�ximo de tiempo = 4 semanas
			Message message = new Message.Builder().timeToLive(2419200)
					.delayWhileIdle(true)
					.addData(Properties.MESSAGE_KEY, textMessage).build();

			// Result r = sender.send(message, cloudID, 1);
			// Para obtener el resultado del envio
			try {
				sender.send(message, cloudID, 1);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	}

	// PROBADA
	// Enviar las conversacion pendientes de un usuario
	private void sendConversations(String user) {
		// System.out.println("SELECT COUNT(*) FROM MENSAJES_ENTREGAR WHERE DESTINATARIO = '"
		// + user + "';");
		// ResultSet rs =
		// Main.executeSelect("SELECT COUNT(*) FROM MENSAJES_ENTREGAR WHERE DESTINATARIO = '"
		// + user + "';");
		List<Object> items = DataBase
				.executeQuery("SELECT COUNT(*) FROM Mensaje WHERE destino = '"
						+ user + "'");

		long numberOfMessages = (Long) items.get(0);

		List<Object> messages = DataBase
				.executeQuery("FROM Mensaje WHERE destino = '" + user
						+ "' AND recibido = 0");
		// rs =
		// Main.executeSelect("SELECT * FROM MENSAJES_ENTREGAR WHERE DESTINATARIO = '"
		// + user + "';");

		HashMap<String, Conversation> hConversations = new HashMap<String, Conversation>();

		String from, message;

		for (int i = 0; i < messages.size(); i++) {
			Mensaje m = (Mensaje) messages.get(i);

			if (hConversations.get(m.getFrom()) == null) {
				Conversation c = new Conversation(m.getFrom(),
						new ArrayList<Mensaje>());
				c.addMessage(m);
				hConversations.put(m.getFrom(), c);
			} else {
				Conversation c = hConversations.get(m.getFrom());
				c.addMessage(m);
			}

			System.out.println(m.getFrom() + ":" + m.getMessage());
		}

		System.out.println("Numero de elementos: " + hConversations.size());
		try {
			out.writeObject(hConversations.size());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		Iterator it = hConversations.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry pairs = (Map.Entry) it.next();
			Conversation c = (Conversation) pairs.getValue();

			for (Mensaje s : c.getMessages()) {
				System.out.println("ID: " + s.getId());
			}

			try {
				out.writeObject(c);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

	}

	public void receiveConfirmation() {
		try {
			Integer maxId = (Integer) in.readObject();
			System.out.println("Eliminar mensajes < a " + maxId);

			DataBase.executeUpdate("UPDATE Mensaje SET recibido=1 WHERE id <= "
					+ maxId + " AND recibido = 0");
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

}
