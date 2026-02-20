package br.com.easyembranet.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.com.easyembranet.dtos.request.rede.RedeRequestDto;
import br.com.easyembranet.dtos.response.equipamento.EquipamentoResponseDto;
import br.com.easyembranet.dtos.response.rede.RedeResponseDto;
import br.com.easyembranet.services.RedeService;

@RestController
@RequestMapping("/api/v1/redes")
public class RedeController {

	@Autowired
	private RedeService redeService;

	@GetMapping("listar")
	public ResponseEntity<Page<RedeResponseDto>> getRedes(@RequestParam(defaultValue = "0") Integer page,
			@RequestParam(defaultValue = "20") Integer size) {
		var response = redeService.listarRedesCadastradas(page, size);
		return ResponseEntity.ok(response);
	}

	@PostMapping("cadastrar")
	public ResponseEntity<RedeResponseDto> postCadastrar(@RequestBody RedeRequestDto request) {
		var response = redeService.cadastrarRede(request);
		return ResponseEntity.ok(response);
	}

	@PostMapping("mapear/{id}")
	public ResponseEntity<List<EquipamentoResponseDto>> postMapearRedeEnviadaComId(@PathVariable Long id) {
		var response = redeService.mapearRedeEnviadaComId(id);
		return ResponseEntity.ok(response);
	}

	@DeleteMapping("excluir")
	public ResponseEntity<RedeResponseDto> deletarRede(@RequestParam Long id) {
		var response = redeService.deletarRede(id);
		return ResponseEntity.ok(response);
		
	}

}
