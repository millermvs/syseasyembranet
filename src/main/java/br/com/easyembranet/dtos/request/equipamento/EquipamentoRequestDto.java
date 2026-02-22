package br.com.easyembranet.dtos.request.equipamento;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EquipamentoRequestDto {

	private String ip;
	private String mac;
	private String nomeRadio;
	private String ssid;
	private String nivelDeSinal;
	private String canalRadio;
	private String macDoAp;

}
