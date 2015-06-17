package main.java;

import java.io.Serializable;


public class Mensaje implements Serializable {

	private String from = null;
	private String receiver = null;
	private String message = null;
	private String fecha = null;

	static final long serialVersionUID = 42L;

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public String getReceiver() {
		return receiver;
	}

	public void setReceiver(String receiver) {
		this.receiver = receiver;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getFecha() {
		return fecha;
	}

	public void setFecha(String fecha) {
		this.fecha = fecha;
	}
}
