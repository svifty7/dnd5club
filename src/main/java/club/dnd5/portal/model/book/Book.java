package club.dnd5.portal.model.book;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter

@Entity
@Table(name = "books")
public class Book implements Serializable, Comparable<Book>{
	private static final long serialVersionUID = 1L;
	
	@Id
	@Column(unique = true, nullable = false)
	private String source;
	private String name;
	private String altName;
	private String englishName;

	@Column(columnDefinition = "TEXT")
	private String description;
	
	@Enumerated(EnumType.STRING) 
	private TypeBook type;
	
	private Integer year;
	
	public Book(String source) {
		this.source = source;
	}

	@Override
	public int compareTo(Book b) {
		return type.compareTo(b.getType());
	}
	
	@Override
	public String toString() {
		return source;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((source == null) ? 0 : source.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Book other = (Book) obj;
		if (source == null) {
			if (other.source != null)
				return false;
		} else if (!source.equals(other.source))
			return false;
		return true;
	}
	
	public String getUrlName() {
		return englishName.replace(' ', '_');
	}
}