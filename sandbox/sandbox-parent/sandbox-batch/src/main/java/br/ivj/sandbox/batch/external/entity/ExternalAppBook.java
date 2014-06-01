package br.ivj.sandbox.batch.external.entity;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "book")
public class ExternalAppBook {
	private String title;
	private Integer author;

	@XmlElement(name = "title")
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	@XmlElement(name = "author")
	public Integer getAuthor() {
		return author;
	}

	public void setAuthor(Integer author) {
		this.author = author;
	}

	@Override
	public String toString() {
		return "ExternalAppBook [title=" + title + ", author=" + author + "]";
	}
}