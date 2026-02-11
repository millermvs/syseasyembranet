package br.com.easyembranet.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
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

	@PostMapping("mapear")
	public ResponseEntity<List<EquipamentoResponseDto>> postMapearRedeEnviada(@RequestBody RedeRequestDto request) {
		var response = redeService.mapearRedeEnviada(request);
		return ResponseEntity.ok(response);

	}
}
