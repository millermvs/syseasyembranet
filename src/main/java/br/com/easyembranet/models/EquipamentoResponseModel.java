package br.com.easyembranet.models;

import org.snmp4j.PDU;
import org.snmp4j.smi.VariableBinding;
import org.springframework.stereotype.Component;

import br.com.easyembranet.dtos.response.equipamento.EquipamentoResponseDto;

@Component
public class EquipamentoResponseModel {

	public EquipamentoResponseDto montarRespostaEquipamento(String ip, PDU resposta) {

		VariableBinding mac = resposta.get(0);
		VariableBinding nomeDoRadio = resposta.get(1);
		VariableBinding ssid = resposta.get(2);
		VariableBinding nivelDeSinal = resposta.get(3);
		VariableBinding canalDoRaio = resposta.get(4);
		VariableBinding macDoAP = resposta.get(5);

		var dtoResponse = new EquipamentoResponseDto();
		dtoResponse.setIp(ip);
		dtoResponse.setRede(ip);
		dtoResponse.setMac(mac.getVariable().toString());
		dtoResponse.setNomeRadio(nomeDoRadio.getVariable().toString());
		dtoResponse.setSsid(ssid.getVariable().toString());
		dtoResponse.setNivelDeSinal(nivelDeSinal.getVariable().toString());
		dtoResponse.setCanalRadio(canalDoRaio.getVariable().toString());
		dtoResponse.setMacDoAp(macDoAP.getVariable().toString());
		dtoResponse.setStatus("SUCESSO");
		return dtoResponse;
	}

}
