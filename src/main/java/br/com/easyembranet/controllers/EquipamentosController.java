package br.com.easyembranet.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.com.easyembranet.dtos.request.equipamento.EquipamentoRequestDto;
import br.com.easyembranet.dtos.response.equipamento.EquipamentoResponseDto;
import br.com.easyembranet.services.EquipamentoService;

@RestController
@RequestMapping("/api/v1/equipamentos")
public class EquipamentosController {

	@Autowired
	private EquipamentoService equipamentoService;

	@PostMapping("cadastrar")
	public ResponseEntity<EquipamentoResponseDto> postCadastrar(@RequestBody EquipamentoRequestDto request) {
		var response = equipamentoService.cadastrarEquipamento(request);
		return ResponseEntity.ok(response);
	}

	@GetMapping("mapear/{ip}")
	public ResponseEntity<EquipamentoResponseDto> postBuscarInformacoes(@PathVariable String ip) {
		var response = equipamentoService.buscarInformacoesEquipamentoUnico(ip);
		return ResponseEntity.ok(response);
	}

	@GetMapping("listar")
	public ResponseEntity<Page<EquipamentoResponseDto>> listarEquipamentos(@RequestParam(defaultValue = "0") Integer page,
			@RequestParam(defaultValue = "20") Integer size) {
		var response = equipamentoService.listarEquipamentos(page, size);
		return ResponseEntity.ok(response);
	}

}
