package br.com.easyembranet.services;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.easyembranet.dtos.response.equipamento.EquipamentoResponseDto;
import br.com.easyembranet.entities.Rede;
import br.com.easyembranet.exceptions.NaoEncontradoException;
import br.com.easyembranet.repositories.RedeRepository;

@Service
public class MapearRedeService {

	@Autowired
	private RedeRepository redeRepository;

	@Autowired
	private EquipamentoService equipamentoService;

	public List<EquipamentoResponseDto> mapearRedeEnviadaComId(Long id) {
		Rede rede = redeRepository.findById(id).orElseThrow(() -> new NaoEncontradoException("Rede não encontrada."));

		int tempoEsperaMs = 1000;

		List<EquipamentoResponseDto> ipsAtivos = new ArrayList<EquipamentoResponseDto>();

		String[] partes = rede.getRede().split("\\.");

		String baseRede = partes[0] + "." + partes[1] + "." + partes[2] + ".";

		for (int i = 2; i <= 254; i++) {

			String ip = baseRede + i;
			System.out.println(ip);

			try {
				InetAddress endereco = InetAddress.getByName(ip);

				if (endereco.isReachable(tempoEsperaMs)) {

					try {
						var equipamentoAlcancado = equipamentoService.buscarInformacoes(ip, rede);
						ipsAtivos.add(equipamentoAlcancado);
					} catch (Exception e) {// caso snmp inacessivel
						EquipamentoResponseDto erroEquipamento = new EquipamentoResponseDto();
						erroEquipamento.setIp(ip);
						erroEquipamento.setStatus(e.getMessage());
						ipsAtivos.add(erroEquipamento);
					}
				}

			} catch (Exception e) {
				EquipamentoResponseDto erroEquipamento = new EquipamentoResponseDto();
				erroEquipamento.setIp(ip);
				erroEquipamento.setStatus("Erro ao tentar alcançar o IP " + ip + " " + e.getMessage());
				ipsAtivos.add(erroEquipamento);
			}
		}

		return ipsAtivos;
	}
}
