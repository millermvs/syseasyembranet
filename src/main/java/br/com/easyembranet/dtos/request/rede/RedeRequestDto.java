package br.com.easyembranet.dtos.request.rede;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RedeRequestDto {

	@NotBlank(message = "Rede não pode estar vazia")
	private String rede;

	@NotBlank(message = "Modo wireless é obrigatório")
	private String modoWireless;

}
