package br.com.easyembranet.dtos.response.rede;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RedeResponseDto {

	private Long idRede;
	private String rede;
	private String modoWireless;
	private Long totalEquipamentos;

}
