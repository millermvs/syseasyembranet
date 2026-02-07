package br.com.easyembranet.services;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.easyembranet.dtos.request.rede.RedeRequestDto;
import br.com.easyembranet.dtos.response.equipamento.EquipamentoResponseDto;
import br.com.easyembranet.dtos.response.rede.RedeResponseDto;
import br.com.easyembranet.entities.Equipamento;
import br.com.easyembranet.entities.Rede;
import br.com.easyembranet.exceptions.NaoEncontradoException;
import br.com.easyembranet.exceptions.RegraDeNegocioException;
import br.com.easyembranet.repositories.EquipamentoRepository;
import br.com.easyembranet.repositories.RedeRepository;

@Service
public class RedeService {

	@Autowired
	private RedeRepository redeRepository;

	@Autowired
	private EquipamentoRepository equipamentoRepository;

	public RedeResponseDto cadastrarRede(RedeRequestDto request) {
		var novaRede = new Rede();
		novaRede.setRede(request.getRede());
		redeRepository.save(novaRede);

		var response = new RedeResponseDto();
		response.setRedeEquipamento(novaRede.getRede());
		return response;
	}

	public List<EquipamentoResponseDto> mapearRedeEnviadaComId(Long id) {
		Rede rede = redeRepository.findById(id).orElseThrow(() -> new NaoEncontradoException("Rede não encontrada."));

		int tempoEsperaMs = 1000;

		List<EquipamentoResponseDto> ipsAtivos = new ArrayList<EquipamentoResponseDto>();

		for (int i = 13; i <= 15; i++) {

			String ip = rede.getRede() + i;
			try {
				InetAddress endereco = InetAddress.getByName(ip);

				if (endereco.isReachable(tempoEsperaMs)) {
					Equipamento novoEquipamento = new Equipamento();
					novoEquipamento.setIp(ip);
					novoEquipamento.setRede(rede);
					equipamentoRepository.save(novoEquipamento);

					EquipamentoResponseDto responseEquipamento = new EquipamentoResponseDto();
					responseEquipamento.setIp(ip);
					responseEquipamento.setRede(rede.getRede());
					ipsAtivos.add(responseEquipamento);
				}

			} catch (Exception e) {
				throw new RegraDeNegocioException("Erro ao tentar alcançar o IP " + ip + " " + e.getMessage());
			}
		}

		return ipsAtivos;
	}

	public List<EquipamentoResponseDto> mapearRedeEnviada(RedeRequestDto request) {
		String rede = request.getRede();
		int tempoEsperaMs = 1000;

		List<EquipamentoResponseDto> ipsAtivos = new ArrayList<EquipamentoResponseDto>();

		for (int i = 13; i <= 15; i++) {

			String ip = rede + i;
			try {
				InetAddress endereco = InetAddress.getByName(ip);

				if (endereco.isReachable(tempoEsperaMs)) {
					EquipamentoResponseDto novoEquipamento = new EquipamentoResponseDto();
					novoEquipamento.setIp(ip);
					novoEquipamento.setRede(rede);
					ipsAtivos.add(novoEquipamento);
				}

			} catch (Exception e) {
				throw new RegraDeNegocioException("Erro ao tentar alcançar o IP " + ip + " " + e.getMessage());
			}
		}

		return ipsAtivos;
	}
}
