package club.dnd5.portal.dto.api.classes;

import java.util.ArrayList;
import java.util.Collection;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import club.dnd5.portal.model.SpellcasterType;
import club.dnd5.portal.model.classes.HeroClass;
import club.dnd5.portal.model.classes.archetype.Archetype;
import lombok.Getter;
import lombok.Setter;

@JsonInclude(Include.NON_NULL)

@Getter
@Setter
public class ClassInfoApiDto {
	private NameApi name;
	private Collection<ClassTabApiDto> tabs = new ArrayList<>(5);
	private Collection<String> images;

	public ClassInfoApiDto(HeroClass heroClass, Collection<String> images) {
		name = new NameApi(heroClass.getCapitalazeName(), heroClass.getEnglishName());
		this.images = images;
		tabs.add(new ClassTabApiDto("Навыки", String.format("/classes/fragment/%s", heroClass.getUrlName()), "tab-traits", 0, true));
		tabs.add(new ClassTabApiDto("Описание", String.format("/classes/%s/description", heroClass.getUrlName()), "tab-description", 1, true));
		if (heroClass.getSpellcasterType() != SpellcasterType.NONE) {
			tabs.add(new ClassTabApiDto("Заклинания", String.format("/classes/%s/spells", heroClass.getUrlName()), "tab-spells", 2, false));	
		}
		if (heroClass.getOptionType() != null) {
			tabs.add(new ClassTabApiDto(heroClass.getOptionType().getDisplayName(), String.format("/classes/%s/options", heroClass.getUrlName()), "tab-option", 3, false));
		}
	}

	public ClassInfoApiDto(Archetype archetype, Collection<String> images) {
		HeroClass heroClass = archetype.getHeroClass();
		name = new NameApi(archetype.getHeroClass().getCapitalazeName() + " " + archetype.getCapitalizeName(), archetype.getHeroClass().getEnglishName()+ " " + archetype.getEnglishName());
		this.images = images;
		tabs.add(new ClassTabApiDto("Навыки", String.format("/classes/%s/architypes/%s", heroClass.getUrlName(), archetype.getUrlName()), "tab-traits", 0, true));
		tabs.add(new ClassTabApiDto("Описание", String.format("/classes/%s/archetype/%s/description", heroClass.getUrlName(), archetype.getUrlName()), "tab-description", 1, true));
		if (heroClass.getSpellcasterType() != SpellcasterType.NONE || archetype.getSpellcasterType() != SpellcasterType.NONE) {
			tabs.add(new ClassTabApiDto("Заклинания", String.format("/classes/%s/%s/spells", heroClass.getUrlName(), archetype.getUrlName()), "tab-spells", 2, false));	
		}
		if (heroClass.getOptionType() != null) {
			tabs.add(new ClassTabApiDto(heroClass.getOptionType().getDisplayName(), String.format("/classes/%s/%s/options", heroClass.getUrlName(), archetype.getUrlName()), "tab-option", 3, false));
		}
		if (archetype.getOptionType() != null) {
			tabs.add(new ClassTabApiDto(archetype.getOptionType().getDisplayName(), String.format("/classes/%s/%s/options", heroClass.getUrlName(), archetype.getUrlName()), "tab-option", 4, false));
		}
	}
}