package club.dnd5.portal.controller.rest;

import java.util.Map;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.datatables.mapping.DataTablesInput;
import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import club.dnd5.portal.dto.SpellDto;
import club.dnd5.portal.repository.datatable.SpellDatatableRepository;

@RestController
public class SpellRestController {
	@Autowired
	private SpellDatatableRepository repo;

	@GetMapping("/data/spells")
	public DataTablesOutput<SpellDto> getData(@Valid DataTablesInput input,
			@RequestParam Map<String, String> queryParameters) {

		DataTablesOutput<SpellDto> output = repo.findAll(input, SpellDto::new);

		return output;
	}
}