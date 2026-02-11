package br.com.easyembranet.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.easyembranet.dtos.response.equipamento.EquipamentoResponseDto;
import br.com.easyembranet.services.EquipamentoService;

@RestController
@RequestMapping("/api/v1/equipamentos")
public class EquipamentosController {

	@Autowired
	private EquipamentoService equipamentoService;

	@PostMapping("informacoes")
	public ResponseEntity<List<EquipamentoResponseDto>> postBuscarInformacoes(@RequestBody List<String> ips) {
		var response = equipamentoService.varrerLista(ips);
		return ResponseEntity.ok(response);
	}
}
