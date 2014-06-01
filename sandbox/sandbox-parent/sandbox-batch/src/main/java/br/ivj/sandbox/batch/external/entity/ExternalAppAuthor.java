package br.ivj.sandbox.batch.external.entity;

import java.util.Date;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import br.ivj.sandbox.batch.utils.xmladapter.JaxbDateAdapter;

@XmlRootElement(name = "author")
public class ExternalAppAuthor {
	private Integer id;
	private String firstName;
	private String lastName;
	private Date birthDate;

	@XmlAttribute(name = "id")
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	@XmlElement(name = "firstName")
	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	@XmlElement(name = "lastName")
	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	@XmlJavaTypeAdapter(JaxbDateAdapter.class)
	@XmlElement(name = "birthDate")
	public Date getBirthDate() {
		return birthDate;
	}

	public void setBirthDate(Date birthDate) {
		this.birthDate = birthDate;
	}

	public String getName() {
		return String.format("%s %s", firstName, lastName);
	}

	@Override
	public String toString() {
		return "ExternalAppAuthor [id=" + id + ", firstName=" + firstName
				+ ", lastName=" + lastName + ", birthDate=" + birthDate + "]";
	}

}