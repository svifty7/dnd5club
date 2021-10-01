package club.dnd5.portal.controller;

import javax.naming.directory.InvalidAttributesException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import club.dnd5.portal.repository.datatable.ItemDatatableRepository;

@Controller
public class ItemController {
	@Autowired
	private ItemDatatableRepository repository;

	@GetMapping("/items")
	public String getItems() {
		return "items";
	}
	
	@GetMapping("/items/{name}")
	public String getItem(Model model, @PathVariable String name) {
		model.addAttribute("selectedItem", "name");
		return "items";
	}
	
	@GetMapping("/items/fragment/{id}")
	public String getMagicItemFragmentById(Model model, @PathVariable Integer id) throws InvalidAttributesException {
		model.addAttribute("item", repository.findById(id).orElseThrow(InvalidAttributesException::new));
		return "fragments/item :: view";
	}
}