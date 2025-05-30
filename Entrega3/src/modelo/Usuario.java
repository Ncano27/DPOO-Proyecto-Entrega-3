package modelo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Usuario implements Serializable {
	private static final long serialVersionUID = 4L;

	private String username;
	private String password;
	private String rol;
	private String email;
	private List<Tiquete> tiquetes;

	public Usuario(String username, String password, String rol, String email) {
		this.username = username;
		this.password = password;
		this.rol = rol;
		this.email = email;
		this.tiquetes = new ArrayList<>();
	}

	public String getUsername() {
		return username;
	}

	public String getPassword() {
		return password;
	}

	public String getRol() {
		return rol;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String nuevoEmail) {
		this.email = nuevoEmail;
	}
	public void setRol(String nuevoRol) {
        this.rol = nuevoRol;
    }
	public boolean autenticar(String intentoPassword) {
		return this.password != null && this.password.equals(intentoPassword);
	}

	public void agregarTiquete(Tiquete t) {
		if (this.tiquetes == null) {
			this.tiquetes = new ArrayList<>();
		}
		this.tiquetes.add(t);
	}

	public List<Tiquete> getTiquetes() {
		if (this.tiquetes == null) {
			this.tiquetes = new ArrayList<>();
		}
		return tiquetes;
	}

	@Override
	public String toString() {
		return "Usuario: " + username + " (Rol: " + rol + ", Email: " + email + ")";
	}
}
