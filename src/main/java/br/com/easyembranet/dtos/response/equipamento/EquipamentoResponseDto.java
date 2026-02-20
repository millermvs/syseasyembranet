package br.com.easyembranet.dtos.response.equipamento;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EquipamentoResponseDto {

	private String ip;
	private String rede;
	private String mac;
	private String nomeRadio;
	private String ssid;
	private String nivelDeSinal;
	private String canalRadio;
	private String macDoAp;
	private String Status;
}
