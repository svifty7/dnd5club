package club.dnd5.portal.controller;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.naming.directory.InvalidAttributesException;
import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import club.dnd5.portal.dto.spell.SpellDto;
import club.dnd5.portal.model.DamageType;
import club.dnd5.portal.model.book.Book;
import club.dnd5.portal.model.book.TypeBook;
import club.dnd5.portal.model.splells.MagicSchool;
import club.dnd5.portal.model.splells.Spell;
import club.dnd5.portal.repository.classes.ArchetypeSpellRepository;
import club.dnd5.portal.repository.datatable.SpellDatatableRepository;

@Controller
public class SpellController {
	private static final String[][] classesMap = { { "1", "Бард" }, { "2", "Волшебник" }, { "3", "Друид" },
			{ "4", "Жрец" }, { "5", "Колдун" }, { "6", "Паладин" }, { "7", "Следопыт" }, { "8", "Чародей" },
			{ "14", "Изобретатель" } };

	private Map<TypeBook, List<Book>> sources;
	
	@Autowired
	private SpellDatatableRepository repository;
	@Autowired
	private ArchetypeSpellRepository archetypeSpellRepository;

	@PostConstruct
	public void init() {
		sources = new HashMap<>();
		sources.put(TypeBook.OFFICAL, repository.findBook(TypeBook.OFFICAL));
		sources.put(TypeBook.SETTING, repository.findBook(TypeBook.SETTING));
		sources.put(TypeBook.MODULE, repository.findBook(TypeBook.MODULE));
		sources.put(TypeBook.CUSTOM, repository.findBook(TypeBook.CUSTOM));
	}
	
	@GetMapping("/spells")
	public String getSpells(Model model) {
		model.addAttribute("classes", classesMap);
		model.addAttribute("schools", MagicSchool.values());
		model.addAttribute("books", sources.get(TypeBook.OFFICAL));
		model.addAttribute("settingBooks", sources.get(TypeBook.SETTING));
		model.addAttribute("adventureBooks", sources.get(TypeBook.MODULE));
		model.addAttribute("hombrewBooks", sources.get(TypeBook.CUSTOM));
		model.addAttribute("damageTypes", DamageType.getSpellDamage());
		model.addAttribute("metaTitle", "Заклинания (Spells) D&D 5e");
		model.addAttribute("metaUrl", "https://dnd5.club/spells");
		model.addAttribute("metaDescription", "Заклинания по D&D 5 редакции");
		return "spells";
	}
	
	@GetMapping("/spells/{name}")
	public String getSpell(Model model, @PathVariable String name, HttpServletRequest request) {
		Spell spell = repository.findByEnglishName(name.replace("_", " "));
		if (spell == null) {
			request.setAttribute(RequestDispatcher.ERROR_STATUS_CODE, "404");
			return "forward: /error";
		}
		model.addAttribute("races", Collections.emptyList());
		model.addAttribute("classes", classesMap);
		model.addAttribute("schools", MagicSchool.values());
		model.addAttribute("damageTypes", DamageType.getSpellDamage());
		model.addAttribute("books", sources.get(TypeBook.OFFICAL));
		model.addAttribute("settingBooks", sources.get(TypeBook.SETTING));
		model.addAttribute("adventureBooks", sources.get(TypeBook.MODULE));
		model.addAttribute("hombrewBooks", sources.get(TypeBook.CUSTOM));
		SpellDto spellDto = new SpellDto(spell);
		model.addAttribute("selectedSpell", spellDto);
		model.addAttribute("metaTitle", String.format("%s (%s)", spellDto.getName(), spellDto.getEnglishName()) + " | Заклинания D&D 5e");
		model.addAttribute("metaUrl", "https://dnd5.club/spells/" + name);
		model.addAttribute("metaDescription", String.format("%s %s, %s", (spellDto.getLevel() == 0 ? "Заговор" : spellDto.getLevel() + " уровень"), spellDto.getName(), spellDto.getSchool()));
		model.addAttribute("metaImage", String.format("https://image.dnd5.club:8089/magic/%s.webp", spell.getSchool().name()));
		return "spells";
	}
	
	@GetMapping("/spells/fragment/{id}")
	public String getSpellFragmentById(Model model, @PathVariable Integer id) throws InvalidAttributesException {
		model.addAttribute("archetypes", archetypeSpellRepository.findAllBySpell(id));
		model.addAttribute("races", Collections.emptyList());
		model.addAttribute("spell", repository.findById(id).orElseThrow(InvalidAttributesException::new));
		return "fragments/spell :: view";
	}
	
	@GetMapping("/spells/id")
	public String getSpell(Model model, Integer id) throws InvalidAttributesException {
		model.addAttribute("archetypes", archetypeSpellRepository.findAllBySpell(id));
		model.addAttribute("races", Collections.emptyList());
		model.addAttribute("spell", repository.findById(id).orElseThrow(InvalidAttributesException::new));
		return "fragments/spell :: view";
	}
}