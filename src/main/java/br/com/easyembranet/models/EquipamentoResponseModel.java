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
		VariableBinding ipDoAP = resposta.get(4);

		var dtoResponse = new EquipamentoResponseDto();
		dtoResponse.setIp(ip);
		dtoResponse.setRede(ip);
		dtoResponse.setMac(mac.getVariable().toString());
		dtoResponse.setNomeRadio(nomeDoRadio.getVariable().toString());
		dtoResponse.setSsid(ssid.getVariable().toString());
		dtoResponse.setNivelDeSinal(nivelDeSinal.getVariable().toString());
		dtoResponse.setIpDoAp(ipDoAP.getVariable().toString());
		dtoResponse.setStatus("Ok");
		return dtoResponse;
	}

}
