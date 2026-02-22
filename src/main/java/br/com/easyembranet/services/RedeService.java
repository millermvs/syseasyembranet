package br.com.easyembranet.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.easyembranet.dtos.request.rede.RedeRequestDto;
import br.com.easyembranet.dtos.response.rede.RedeResponseDto;
import br.com.easyembranet.entities.Rede;
import br.com.easyembranet.exceptions.JaCadastradoException;
import br.com.easyembranet.exceptions.NaoEncontradoException;
import br.com.easyembranet.exceptions.RegraDeNegocioException;
import br.com.easyembranet.repositories.RedeRepository;

@Service
public class RedeService {

	@Autowired
	private RedeRepository redeRepository;

	public Rede consultarRede(String ip) {

		String rede = converterIpParaRede(ip);
		Rede redeFound = redeRepository.findByRede(rede)
				.orElseThrow(() -> new NaoEncontradoException("Rede não encontrada."));

		return redeFound;
	}
	
	public Boolean redeExiste(String ip) {
		String rede = converterIpParaRede(ip);
		var redeFound = redeRepository.findByRede(rede);
		return redeFound.isPresent();
	}

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

	public String converterIpParaRede(String ip) {
		String[] partes = ip.split("\\.");

		String rede = partes[0] + "." + partes[1] + "." + partes[2] + ".0";

		return rede;
	}

}