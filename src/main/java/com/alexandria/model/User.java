package com.alexandria.model;

/**
 * Represents a registered user.
 */
public class User {
	private Integer id;
	private String name;
	private String email;
	private String photo;
	private String organization;
	private String password; // ONLY HASHED PASSWORDS!

	public User() {
	}

	public User(String name, String email, String photo,
			String organization, String password) {
		this.name = name;
		this.email = email;
		this.photo = photo;
		this.organization = organization;
		this.password = password;
	}

	public User(Integer id, String name, String email,
			String photo, String organization, String password) {
		this.id = id;
		this.name = name;
		this.email = email;
		this.photo = photo;
		this.organization = organization;
		this.password = password; // ONLY HASHED PASSWORDS!
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPhoto() {
		return photo;
	}

	public void setPhoto(String photo) {
		this.photo = photo;
	}

	public String getOrganization() {
		return organization;
	}

	public void setOrganization(String organization) {
		this.organization = organization;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
}
