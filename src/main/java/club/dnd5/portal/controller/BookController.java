package club.dnd5.portal.controller;

import javax.naming.directory.InvalidAttributesException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import club.dnd5.portal.model.book.Book;
import club.dnd5.portal.repository.datatable.BookDatatableRepository;

@Controller
public class BookController {
	@Autowired
	private BookDatatableRepository repository;

	@GetMapping("/books")
	public String getRules(Model model) {
		model.addAttribute("metaTitle", "Источники (Books) D&D 5e");
		model.addAttribute("metaUrl", "https://dnd5.club/books");
		return "books";
	}
	
	@GetMapping("/books/{name}")
	public String getRule(Model model, @PathVariable String name) {
		Book book = repository.findByEnglishName(name.replace("_", " "));
		model.addAttribute("metaTitle", String.format("%s - Источники (Books) D&D 5e", book.getName()));
		model.addAttribute("metaUrl", String.format("https://dnd5.club/books/%s", name));
		model.addAttribute("selectedBook", "name");
		return "books";
	}
	
	@GetMapping("/books/fragment/{id}")
	public String getMagicRuleFragmentById(Model model, @PathVariable String id) throws InvalidAttributesException {
		model.addAttribute("book", repository.findById(id).orElseThrow(InvalidAttributesException::new));
		return "fragments/book :: view";
	}
}