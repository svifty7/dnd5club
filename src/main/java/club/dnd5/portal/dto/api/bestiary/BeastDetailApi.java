package club.dnd5.portal.dto.api.bestiary;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import club.dnd5.portal.dto.api.NameValueApi;
import club.dnd5.portal.model.AbilityType;
import club.dnd5.portal.model.ArmorType;
import club.dnd5.portal.model.DamageType;
import club.dnd5.portal.model.creature.Action;
import club.dnd5.portal.model.creature.ActionType;
import club.dnd5.portal.model.creature.Condition;
import club.dnd5.portal.model.creature.Creature;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.val;

@JsonInclude(Include.NON_NULL)
@JsonPropertyOrder({"name", "size", "type", "str", "dex", "con", "int", "wiz", "cha"})
@NoArgsConstructor
@Getter
@Setter
public class BeastDetailApi extends BeastApi {
	private Integer experience;
	private String size;
	private String alignment;
	private Byte armorClass;
	private List<String> armors;
	private String armorText;
	private HitPointsApi hits;
	private List<NameValueApi> speed;
	private String str;
	private String dex;
	private String con;
	@JsonProperty("int")
	private String intellect;
	private String wiz;
	private String cha;
	
	private List<NameValueApi> savingThrows;
	private List<NameValueApi> skills;
	
	private List<String> damageResistances;
	private List<String> damageImmunities;
	private List<String> damageVulnerabilities;
	private List<String> conditionImmunities;
	private SenseApi senses;
	
	private List<NameValueApi> feats;
	private List<NameValueApi> actions;
	private List<NameValueApi> reactions;
	private List<NameValueApi> bonusActions;
	private List<NameValueApi> legendary;
	private List<NameValueApi> mysticalActions;
	
	private String description;

	public BeastDetailApi(Creature beast) {
		super(beast);
		size = beast.getSizeName();
		experience = beast.getExp();
		alignment = beast.getAligment();
		armorClass = beast.getAC();
		
		if (!beast.getArmorTypes().isEmpty()) {
			armors = beast.getArmorTypes().stream().map(ArmorType::getCyrillicName).collect(Collectors.toList());
		}
		if (beast.getBonusAC() != null) {
			armorText = beast.getBonusAC();
		}
		hits = new HitPointsApi(beast);
		speed = new ArrayList<>(5);
		speed.add(new NameValueApi(null, beast.getSpeed()));
		if (beast.getFlySpeed() != null) {
			NameValueApi value = new NameValueApi("летая", beast.getFlySpeed());
			if (beast.getHover() != null) {
				value.setHover(Boolean.TRUE);
			}
			speed.add(value);
		}
		if (beast.getSwimmingSpped() != null) {
			speed.add(new NameValueApi("плавая", beast.getSwimmingSpped()));
		}
		if (beast.getDiggingSpeed() != null) {
			speed.add(new NameValueApi("копая ", beast.getDiggingSpeed()));
		}
		if (beast.getClimbingSpeed() != null) {
			speed.add(new NameValueApi("лазая", beast.getClimbingSpeed()));
		}

		str = String.format("%d (%d)", beast.getStrength(), AbilityType.getModifier(beast.getStrength()));
		dex = String.format("%d (%d)", beast.getDexterity(), AbilityType.getModifier(beast.getDexterity()));
		con = String.format("%d (%d)", beast.getConstitution(), AbilityType.getModifier(beast.getConstitution()));
		intellect = String.format("%d (%d)", beast.getIntellect(), AbilityType.getModifier(beast.getIntellect()));
		wiz = String.format("%d (%d)", beast.getWizdom(), AbilityType.getModifier(beast.getWizdom()));
		cha = String.format("%d (%d)", beast.getCharisma(), AbilityType.getModifier(beast.getCharisma()));
		if (!beast.getSavingThrows().isEmpty()) {
			savingThrows = beast.getSavingThrows().stream().map(st -> new NameValueApi(st.getAbility().getCyrilicName(), st.getBonus())).collect(Collectors.toList());
		}
		if (!beast.getSkills().isEmpty()) {
			skills = beast.getSkills().stream().map(skill -> new NameValueApi(skill.getType().getCyrilicName(), skill.getBonus())).collect(Collectors.toList());
		}
		if (!beast.getResistanceDamages().isEmpty()) {
			damageResistances = beast.getResistanceDamages().stream().map(DamageType::getCyrilicName).collect(Collectors.toList());
		}
		if (!beast.getImmunityDamages().isEmpty()) {
			damageImmunities = beast.getImmunityDamages().stream().map(DamageType::getCyrilicName).collect(Collectors.toList());
		}
		if (!beast.getVulnerabilityDamages().isEmpty()) {
			damageImmunities = beast.getVulnerabilityDamages().stream().map(DamageType::getCyrilicName).collect(Collectors.toList());
		}
		if (!beast.getImmunityStates().isEmpty()) {
			conditionImmunities = beast.getImmunityStates().stream().map(Condition::getCyrilicName).collect(Collectors.toList());
		}
		senses = new SenseApi(beast);
		if (!beast.getFeats().isEmpty()) {
			feats = beast.getFeats().stream().map(feat -> new NameValueApi(feat.getName(), feat.getDescription())).collect(Collectors.toList());
		}
		List<Action> actionsBeast = beast.getActions(ActionType.ACTION);
		if (!actionsBeast.isEmpty()) {
			actions = actionsBeast.stream().map(action -> new NameValueApi(action.getName(), action.getDescription())).collect(Collectors.toList());
		}
		actionsBeast = beast.getActions(ActionType.REACTION);
		if (!actionsBeast.isEmpty()) {
			reactions = actionsBeast.stream().map(action -> new NameValueApi(action.getName(), action.getDescription())).collect(Collectors.toList());
		}
		actionsBeast = beast.getActions(ActionType.BONUS);
		if (!actionsBeast.isEmpty()) {
			bonusActions = actionsBeast.stream().map(action -> new NameValueApi(action.getName(), action.getDescription())).collect(Collectors.toList());
		}
		actionsBeast = beast.getActions(ActionType.LEGENDARY);
		if (!actionsBeast.isEmpty()) {
			legendary = actionsBeast.stream().map(action -> new NameValueApi(action.getName(), action.getDescription())).collect(Collectors.toList());
		}
		actionsBeast = beast.getActions(ActionType.MYSTICAL);
		if (!actionsBeast.isEmpty()) {
			mysticalActions = actionsBeast.stream().map(action -> new NameValueApi(action.getName(), action.getDescription())).collect(Collectors.toList());
		}
		description = beast.getDescription();
	}
}