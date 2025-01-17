package club.dnd5.portal.controller;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mobile.device.Device;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;
import org.thymeleaf.util.StringUtils;

import club.dnd5.portal.dto.classes.ClassDto;
import club.dnd5.portal.dto.classes.ClassFetureDto;
import club.dnd5.portal.model.book.Book;
import club.dnd5.portal.model.book.TypeBook;
import club.dnd5.portal.model.classes.HeroClass;
import club.dnd5.portal.model.classes.HeroClassTrait;
import club.dnd5.portal.model.classes.archetype.Archetype;
import club.dnd5.portal.model.classes.archetype.ArchetypeTrait;
import club.dnd5.portal.model.image.ImageType;
import club.dnd5.portal.model.splells.MagicSchool;
import club.dnd5.portal.repository.ImageRepository;
import club.dnd5.portal.repository.classes.ArchetypeRepository;
import club.dnd5.portal.repository.classes.ArchetypeTraitRepository;
import club.dnd5.portal.repository.classes.ClassRepository;
import club.dnd5.portal.repository.classes.HeroClassTraitRepository;
import club.dnd5.portal.repository.datatable.OptionDatatableRepository;

@Controller
public class ClassController {
	private static final String[] prerequsitlevels = { "Нет", " 5", " 6", " 7", " 9", "11", "12", "15", "17", "18" };

	@Autowired
	private ClassRepository classRepository;
	@Autowired
	private ArchetypeRepository archetypeRepository;
	@Autowired
	private HeroClassTraitRepository traitRepository;
	@Autowired
	private ArchetypeTraitRepository archetypeTraitRepository;
	@Autowired
	private ImageRepository imageRepository;
	@Autowired
	private OptionDatatableRepository optionRepository;
	
	private Map<TypeBook, Set<Book>> sources;

	@PostConstruct
	public void init() {
		sources = new HashMap<>();
		sources.put(TypeBook.OFFICAL, new LinkedHashSet<>(classRepository.findBook(TypeBook.OFFICAL)));
		sources.put(TypeBook.SETTING, new LinkedHashSet<>(classRepository.findBook(TypeBook.SETTING)));
		sources.put(TypeBook.CUSTOM, new LinkedHashSet<>(classRepository.findBook(TypeBook.CUSTOM)));
		
		sources.computeIfAbsent(TypeBook.OFFICAL, k -> new LinkedHashSet<>()).addAll(archetypeRepository.findBook(TypeBook.OFFICAL));
		sources.computeIfAbsent(TypeBook.SETTING, k -> new LinkedHashSet<>()).addAll(archetypeRepository.findBook(TypeBook.SETTING));
		sources.computeIfAbsent(TypeBook.CUSTOM, k -> new LinkedHashSet<>()).addAll(archetypeRepository.findBook(TypeBook.CUSTOM));
	}
	
	@GetMapping("/classes")
	public String getClasses(Model model) {
		model.addAttribute("books", sources.get(TypeBook.OFFICAL));
		model.addAttribute("settingBooks", sources.get(TypeBook.SETTING));
		model.addAttribute("moduleBooks", sources.get(TypeBook.MODULE));
		model.addAttribute("hombrewBooks", sources.get(TypeBook.CUSTOM));
		model.addAttribute("selectedClass", null);

		model.addAttribute("classes", classRepository.findAllBySidekick(false));
		model.addAttribute("sidekick", classRepository.findAllBySidekick(true));
		model.addAttribute("metaTitle", "Классы (Classes) D&D 5e");
		model.addAttribute("metaUrl", "https://dnd5.club/classes");
		return "classes";
	}
	
	@GetMapping("/classes/{name}")
	public String getClass(Model model, @PathVariable String name, HttpServletRequest request) {
		model.addAttribute("books", sources.get(TypeBook.OFFICAL));
		model.addAttribute("settingBooks", sources.get(TypeBook.SETTING));
		model.addAttribute("hombrewBooks", sources.get(TypeBook.CUSTOM));
		model.addAttribute("classes", classRepository.findAllBySidekick(false));
		model.addAttribute("sidekick", classRepository.findAllBySidekick(true));
		HeroClass heroClass = classRepository.findByEnglishName(name.replace("_", " "));
		if (heroClass == null) {
			request.setAttribute(RequestDispatcher.ERROR_STATUS_CODE, "404");
			return "forward: /error";
		}
		model.addAttribute("selectedClass", new ClassDto(heroClass));
		model.addAttribute("metaTitle", String.format("%s (%s) | Классы D&D 5e", heroClass.getCapitalazeName(), heroClass.getEnglishName()));
		model.addAttribute("metaUrl", "https://dnd5.club/classes/" + name);
		model.addAttribute("metaDescription", String.format("%s (%s) - описание класса персонажа по D&D 5-редакции", heroClass.getCapitalazeName(), heroClass.getEnglishName()));
		Collection<String> images = imageRepository.findAllByTypeAndRefId(ImageType.CLASS, heroClass.getId());
		if (!images.isEmpty()) {
			model.addAttribute("metaImage", images.iterator().next());
		}
		return "classes";
	}
	
	@GetMapping("/classes/{name}/{archetype}")
	public String getArchetype(Model model, @PathVariable String name, @PathVariable String archetype, HttpServletRequest request) {
		String englishName = name.replace("_", " ");
		HeroClass heroClass = classRepository.findByEnglishName(englishName);
		if (heroClass == null) {
			request.setAttribute(RequestDispatcher.ERROR_STATUS_CODE, "404");
			return "forward: /error";
		}
		Optional<Archetype> selectedArchetype = heroClass.getArchetypes().stream()
				.filter(a -> a.getEnglishName().equalsIgnoreCase(archetype.replace('_', ' ')))
				.findFirst();
		if (!selectedArchetype.isPresent()) {
			request.setAttribute(RequestDispatcher.ERROR_STATUS_CODE, "404");
			return "forward: /error";
		}
		model.addAttribute("books", sources.get(TypeBook.OFFICAL));
		model.addAttribute("settingBooks", sources.get(TypeBook.SETTING));
		model.addAttribute("hombrewBooks", sources.get(TypeBook.CUSTOM));
		model.addAttribute("selectedClass", new ClassDto(heroClass));
		model.addAttribute("selectedArchetype", archetype);
		model.addAttribute("metaTitle", String.format("%s - %s (%s) | Классы | Подклассы D&D 5e",  
				StringUtils.capitalize(selectedArchetype.get().getName().toLowerCase()), heroClass.getCapitalazeName(), heroClass.getEnglishName()));
		model.addAttribute("metaUrl", String.format("https://dnd5.club/classes/%s/%s", name, archetype));
		model.addAttribute("metaDescription", String.format("%s - описание %s класса %s из D&D 5 редакции", 
				selectedArchetype.get().getName(), heroClass.getArchetypeName(), heroClass.getCapitalazeName()));
		Collection<String> images = imageRepository.findAllByTypeAndRefId(ImageType.SUBCLASS, selectedArchetype.get().getId());
		if (!images.isEmpty()) {
			model.addAttribute("metaImage", images.iterator().next());
		}
		return "classes";
	}
	
	@GetMapping("/classes/fragment/{englishName}")
	public String getFragmentClasses(Model model, Device device, @PathVariable String englishName) {
		model.addAttribute("device", device);
		HeroClass heroClass = classRepository.findByEnglishName(englishName.replace("_", " "));
		List<ClassFetureDto> features = new ArrayList<>();
		heroClass.getTraits().stream()
			.filter(f -> !f.isArchitype())
			.map(f -> new ClassFetureDto(f, heroClass.getName()))
			.forEach(f -> features.add(f));
		Map<Integer, Set<ClassFetureDto>> archetypeTraits = heroClass.getArchetypes()
				.stream().flatMap(a -> a.getFeats().stream())
				.collect(
						Collectors.groupingBy(
								f -> f.getArchetype().getId(),
								Collectors.mapping(f -> new ClassFetureDto(
										f, f.getArchetype().getGenitiveName()), 
										Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(ClassFetureDto::getLevel).thenComparing(ClassFetureDto::getName))))
								)
				);
		Collections.sort(features, Comparator.comparing(ClassFetureDto::getLevel));
		model.addAttribute("features", features);
		model.addAttribute("heroClass", heroClass);
		model.addAttribute("archetypeTraits", archetypeTraits);
		model.addAttribute("order", "[[ 1, 'asc' ]]");
		model.addAttribute("selectedArchetypeName", heroClass.getArchetypeName());
		return "fragments/class :: view";
	}
	
	@GetMapping("/classes/fragment_id/{id}")
	public String getFragmentClassesById(Model model, Device device, @PathVariable Integer id) {
		model.addAttribute("device", device);
		HeroClass heroClass = classRepository.findById(id).orElseThrow(IllegalArgumentException::new);
		List<ClassFetureDto> features = new ArrayList<>();
		heroClass.getTraits().stream()
			.filter(f -> !f.isArchitype())
			.map(f -> new ClassFetureDto(f, heroClass.getName()))
			.forEach(f -> features.add(f));
		Map<Integer, Set<ClassFetureDto>> archetypeTraits = heroClass.getArchetypes()
				.stream().flatMap(a -> a.getFeats().stream())
				.collect(
						Collectors.groupingBy(
								f -> f.getArchetype().getId(),
								Collectors.mapping(f -> new ClassFetureDto(
										f, f.getArchetype().getGenitiveName()), 
										Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(ClassFetureDto::getLevel).thenComparing(ClassFetureDto::getName))))
								)
				);
		Collections.sort(features, Comparator.comparing(ClassFetureDto::getLevel));
		model.addAttribute("features", features);
		model.addAttribute("heroClass", heroClass);
		model.addAttribute("archetypeTraits", archetypeTraits);
		model.addAttribute("order", "[[ 1, 'asc' ]]");
		model.addAttribute("selectedArchetypeName", heroClass.getArchetypeName());
		return "fragments/class :: view";
	}
	
	@GetMapping("/classes/images/{englishName}")
	public String getClassImages(Model model, Device device, @PathVariable String englishName) {
		HeroClass heroClass = classRepository.findByEnglishName(englishName.replace("_", " "));
		model.addAttribute("images", imageRepository.findAllByTypeAndRefId(ImageType.CLASS, heroClass.getId()));
		return "fragments/class :: images";
	}
	
	@GetMapping("/classes/spells/{englishName}")
	public String getClassSpells(Model model, Device device, @PathVariable String englishName) {
		HeroClass heroClass = classRepository.findByEnglishName(englishName.replace("_", " "));
		model.addAttribute("heroClass", heroClass);
		model.addAttribute("schools", MagicSchool.values());
		return "fragments/class_spell :: view";
	}

	@GetMapping("/classes/options/{englishName}")
	public String getClassOption(Model model, Device device, @PathVariable String englishName) {
		HeroClass heroClass = classRepository.findByEnglishName(englishName.replace("_", " "));
		model.addAttribute("heroClass", heroClass);
		model.addAttribute("requirements", optionRepository.findAlldPrerequisite());
		model.addAttribute("levels", prerequsitlevels);
		return "fragments/class_options :: view";
	}

	@GetMapping("/classes/{englishName}/architype/name")
	@ResponseBody
	public String getArchitypeName(@PathVariable String englishName) {
		HeroClass heroClass = classRepository.findByEnglishName(englishName.replace("_", " "));
		return heroClass.getArchetypeName(); 
	}

	@GetMapping("/classes/{englishName}/architypes/list")
	public String getArchitypeList(Model model, Device device, @PathVariable String englishName) {
		model.addAttribute("device", device);
		HeroClass heroClass = classRepository.findByEnglishName(englishName.replace("_", " "));
		model.addAttribute("archetypeName", heroClass.getArchetypeName());
		model.addAttribute("archetypes", heroClass.getArchetypes().stream().sorted(Comparator.comparing(Archetype::getBook)).collect(Collectors.toList()));
		model.addAttribute("images", imageRepository.findAllByTypeAndRefId(ImageType.CLASS, heroClass.getId()));
		return "fragments/archetypes_list :: sub_menu"; 
	}
	
	@GetMapping("/classes/{className}/architypes/{archetypeName}")
	public String getByClassIdAndByArchetypeId(Model model, Device device, @PathVariable String className, @PathVariable String archetypeName) {
		model.addAttribute("device", device);
		HeroClass heroClass = classRepository.findByEnglishName(className.replace("_", " "));
		List<ClassFetureDto> features = new ArrayList<>();
		heroClass.getTraits().stream()
			.filter(f -> !f.isArchitype())
			.map(f -> new ClassFetureDto(f, heroClass.getName()))
			.forEach(f -> features.add(f));
		Archetype archetype = heroClass.getArchetypes()
				.stream()
				.filter(a -> archetypeName.replace('_', ' ').equalsIgnoreCase(a.getEnglishName()))
				.findFirst().orElseGet(Archetype::new);

		ClassFetureDto feature = new ClassFetureDto();
		feature.setId(archetype.getId());
		feature.setLevel(archetype.getLevel());
		feature.setDescription(archetype.getDescription());
		feature.setName(archetype.getName());
		feature.setPrefix("ad");
		if (archetype.getBook() != null) {
			feature.setType(heroClass.getArchetypeName() + ". Источник: " + archetype.getBook().getName()
					+ (archetype.getPage() == null ? "" : ", стр. " + archetype.getPage()));
		}
		features.add(feature);
		archetype.getFeats().stream()
			.map(f -> new ClassFetureDto(f, archetype.getGenitiveName()))
			.forEach(f -> features.add(f));

		Collections.sort(features, Comparator.comparing(ClassFetureDto::getLevel).thenComparing(ClassFetureDto::getOrder));
		model.addAttribute("archetypeName", archetype.getName());
		
		model.addAttribute("heroClass", heroClass);
		model.addAttribute("features", features);
		model.addAttribute("selectedArchetypeId", archetype.getId());
		model.addAttribute("selectedArchetype", archetype);
		model.addAttribute("selectedArchetypeName", archetype.getName());
		model.addAttribute("archetypeSpells", archetype.getSpells().stream().filter(s-> s.getLevel() != 0).collect(Collectors.toList()));
		return "fragments/archetype :: view";
	}

	@GetMapping("/classes/{name}/description")
	@ResponseBody
	public String getClassDescription(@PathVariable String name) {
		HeroClass heroClass = classRepository.findByEnglishName(name.replace("_", " "));
		return heroClass.getDescription();
	}

	@GetMapping("/classes/{className}/archetype/{archetypeName}/description")
	@ResponseBody
	public String getArchetypeDescription(@PathVariable String className, @PathVariable String archetypeName) {
		HeroClass heroClass = classRepository.findByEnglishName(className.replace("_", " "));
		return heroClass.getArchetypes()
			.stream()
			.filter(a -> a.getEnglishName().equalsIgnoreCase(archetypeName.replace("_", " ")))
			.map(Archetype::getDescription)
			.findFirst().orElse("");
	}
	
	@GetMapping("/classes/feature/{id}")
	@ResponseBody
	public String getClassFeatureDescription(@PathVariable Integer id) {
		return traitRepository.findById(id).map(HeroClassTrait::getDescription).orElse("");
	}

	@GetMapping("/classes/archetype/feature/{id}")
	@ResponseBody
	public String getArchetyprFeatureDescription(@PathVariable Integer id) {
		return archetypeTraitRepository.findById(id).map(ArchetypeTrait::getDescription).orElse("");
	}
}