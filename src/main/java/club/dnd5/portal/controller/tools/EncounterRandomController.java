package club.dnd5.portal.controller.tools;

import java.util.List;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import club.dnd5.portal.model.creature.HabitatType;
import club.dnd5.portal.model.encounters.RandomEncounter;
import club.dnd5.portal.repository.datatable.RandomEncounterRepository;

@Controller
public class EncounterRandomController {
	public static final Random rnd = new Random();
	
	@Autowired
	private RandomEncounterRepository repo;
	
	@GetMapping("/tools/encounters")
	public String getView(Model model) {
		model.addAttribute("types", HabitatType.types());
		return "tools/random_encounters";
	}
	
	@GetMapping("/tools/encounters/table")
	public String getRandomEncounters(Model model, Integer level, HabitatType type) {
		List<RandomEncounter> encounters = repo.findAllByLevelAndType(level, type);
		model.addAttribute("encounters", encounters);
		return "tools/random_encounters :: table";
	}
	
	@GetMapping("/tools/encounters/random")
	@ResponseBody
	public String getRandomEncounter(Integer level, HabitatType type) {
		int index = 1 + rnd.nextInt(100);
		RandomEncounter encounter = repo.findOne(index, level, type);
		return encounter.getDescription();
	}
}