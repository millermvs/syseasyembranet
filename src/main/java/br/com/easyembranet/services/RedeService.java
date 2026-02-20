package br.com.easyembranet.services;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.easyembranet.dtos.request.rede.RedeRequestDto;
import br.com.easyembranet.dtos.response.equipamento.EquipamentoResponseDto;
import br.com.easyembranet.dtos.response.rede.RedeResponseDto;
import br.com.easyembranet.entities.Equipamento;
import br.com.easyembranet.entities.Rede;
import br.com.easyembranet.exceptions.JaCadastradoException;
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

	@Autowired
	private EquipamentoService equipamentoService;

	@Transactional(readOnly = true)
	public Page<RedeResponseDto> listarRedesCadastradas(Integer page, Integer size) {

		var pageable = PageRequest.of(page, size, Sort.by("rede"));
		var paginaRedes = redeRepository.findAllComTotal(pageable);
		return paginaRedes.map(rede -> {
			var dtoResposta = new RedeResponseDto();
			dtoResposta.setIdRede(rede.getIdRede());
			dtoResposta.setRede(rede.getRede());
			dtoResposta.setTotalEquipamentos(rede.getTotalEquipamentos());
			return dtoResposta;
		});
	}

	@Transactional
	public RedeResponseDto cadastrarRede(RedeRequestDto request) {

		var redeFound = redeRepository.findByRede(request.getRede());

		if (redeFound.isPresent())
			throw new JaCadastradoException("Rede já cadastrada no sistema.");

		var novaRede = new Rede();
		novaRede.setRede(request.getRede());
		redeRepository.save(novaRede);

		var response = new RedeResponseDto();
		response.setRede(novaRede.getRede());
		return response;
	}

	public List<EquipamentoResponseDto> mapearRedeEnviadaComId(Long id) {
		Rede rede = redeRepository.findById(id).orElseThrow(() -> new NaoEncontradoException("Rede não encontrada."));

		int tempoEsperaMs = 1000;

		List<EquipamentoResponseDto> ipsAtivos = new ArrayList<EquipamentoResponseDto>();

		String[] partes = rede.getRede().split("\\.");

		String baseRede = partes[0] + "." + partes[1] + "." + partes[2] + ".";

		for (int i = 2; i <= 50; i++) {

			String ip = baseRede + i;
			System.out.println(ip);

			try {
				InetAddress endereco = InetAddress.getByName(ip);

				if (endereco.isReachable(tempoEsperaMs)) {

					try {
						var equipamentoAlcancado = equipamentoService.buscarInformacoes(ip);

						var equipamentoFound = equipamentoRepository.findByIp(ip);
						if (equipamentoFound.isEmpty()) {
							Equipamento novoEquipamento = new Equipamento();
							novoEquipamento.setIp(equipamentoAlcancado.getIp());
							novoEquipamento.setRede(rede);
							novoEquipamento.setMac(equipamentoAlcancado.getMac());
							novoEquipamento.setNomeRadio(equipamentoAlcancado.getNomeRadio());
							novoEquipamento.setSsid(equipamentoAlcancado.getSsid());
							novoEquipamento.setNivelDeSinal(equipamentoAlcancado.getNivelDeSinal());
							novoEquipamento.setCanalRadio(equipamentoAlcancado.getCanalRadio());
							novoEquipamento.setMacDoAp(equipamentoAlcancado.getMacDoAp());
							novoEquipamento.setStatus(equipamentoAlcancado.getStatus());
							equipamentoRepository.save(novoEquipamento);
							ipsAtivos.add(equipamentoAlcancado);
						} else { // cria o equipamento pra comparar com o do BD e salvar se nao existir
							Equipamento novoEquipamento = new Equipamento();
							novoEquipamento.setIp(equipamentoAlcancado.getIp());
							novoEquipamento.setRede(rede);
							novoEquipamento.setMac(equipamentoAlcancado.getMac());
							novoEquipamento.setNomeRadio(equipamentoAlcancado.getNomeRadio());
							novoEquipamento.setSsid(equipamentoAlcancado.getSsid());
							novoEquipamento.setNivelDeSinal(equipamentoAlcancado.getNivelDeSinal());
							novoEquipamento.setCanalRadio(equipamentoAlcancado.getCanalRadio());
							novoEquipamento.setMacDoAp(equipamentoAlcancado.getMacDoAp());
							novoEquipamento.setStatus(equipamentoAlcancado.getStatus());

							// comparar equipamento
							if (equipamentoFound.get().equals(novoEquipamento)) {
								ipsAtivos.add(equipamentoAlcancado);
							} else {
								var equipamento = equipamentoFound.get();
								equipamento.setRede(rede);
								equipamento.setMac(novoEquipamento.getMac());
								equipamento.setNomeRadio(novoEquipamento.getNomeRadio());
								equipamento.setSsid(novoEquipamento.getSsid());
								equipamento.setNivelDeSinal(novoEquipamento.getNivelDeSinal());
								equipamento.setCanalRadio(novoEquipamento.getCanalRadio());
								equipamento.setMacDoAp(novoEquipamento.getMacDoAp());
								equipamentoRepository.save(equipamento);
								ipsAtivos.add(equipamentoAlcancado);
							}
						}

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

	@Transactional
	public RedeResponseDto deletarRede(Long id) {
		var redeFound = redeRepository.findById(id).orElseThrow(() -> new NaoEncontradoException());
		
		if (!redeFound.getEquipamentos().isEmpty()) {
			   throw new RegraDeNegocioException("Não é possível excluir: rede possui equipamentos vinculados.");
			}
			redeRepository.delete(redeFound);

		redeRepository.delete(redeFound);

		var response = new RedeResponseDto();
		response.setIdRede(redeFound.getIdRede());
		response.setRede(redeFound.getRede());
		return response;
	}
}