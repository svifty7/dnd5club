package club.dnd5.portal.model.articles;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import club.dnd5.portal.model.user.User;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

@Entity
@Table(name = "articles")
public class Article {
	private static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMMM yyyy");
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	private String title;
	private String description;
	@Column(columnDefinition = "TEXT")
	private String text;
	@Enumerated(EnumType.STRING)
	private AtricleStatus status;
	private String author;
	private String translation;
	private String originalAuthor;
	private String originalUrl;
	private String originalName;
	
	private String cause_canceled;
	
	@ManyToOne
	@JoinColumn(name = "creator_id")
	private User creator;
	@ManyToOne
	@JoinColumn(name = "moderator_id")
	private User moderator;
	
	private LocalDateTime created;
	private LocalDateTime changed;
	private LocalDateTime moderated;
	private LocalDateTime published;
	private LocalDateTime deleted;
	
	public String getShortText() {
		return text.substring(0, 255);
	}
	public String getPublishedDate() {
		return published.format(formatter);
	}
}