package br.com.easyembranet.services;

import java.net.InetAddress;

import org.snmp4j.CommunityTarget;
import org.snmp4j.PDU;
import org.snmp4j.Snmp;
import org.snmp4j.TransportMapping;
import org.snmp4j.mp.SnmpConstants;
import org.snmp4j.smi.OID;
import org.snmp4j.smi.OctetString;
import org.snmp4j.smi.UdpAddress;
import org.snmp4j.smi.VariableBinding;
import org.snmp4j.transport.DefaultUdpTransportMapping;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.easyembranet.dtos.request.equipamento.EquipamentoRequestDto;
import br.com.easyembranet.dtos.request.rede.RedeRequestDto;
import br.com.easyembranet.dtos.response.equipamento.EquipamentoResponseDto;
import br.com.easyembranet.entities.Equipamento;
import br.com.easyembranet.entities.Rede;
import br.com.easyembranet.exceptions.RegraDeNegocioException;
import br.com.easyembranet.models.EquipamentoResponseModel;
import br.com.easyembranet.repositories.EquipamentoRepository;

@Service
public class EquipamentoService {

	@Autowired
	private EquipamentoResponseModel equipamentoResponseModel;

	@Autowired
	private EquipamentoRepository equipamentoRepository;

	@Autowired
	private RedeService redeService;

	@Transactional
	public EquipamentoResponseDto cadastrarEquipamento(EquipamentoRequestDto request) {
		var equipamentoFound = equipamentoRepository.findByIp(request.getIp());

		if (equipamentoFound.isPresent()) {
			throw new RegraDeNegocioException("Equipamento já cadastrado no sistema.");
		}

		Rede rede = redeService.consultarRede(request.getIp());

		var novoEquipamento = new Equipamento();
		novoEquipamento.setIp(request.getIp());
		novoEquipamento.setCanalRadio(request.getCanalRadio());
		novoEquipamento.setMac(request.getMac());
		novoEquipamento.setMacDoAp(request.getMacDoAp());
		novoEquipamento.setNivelDeSinal(request.getNivelDeSinal());
		novoEquipamento.setNomeRadio(request.getNomeRadio());
		novoEquipamento.setSsid(request.getSsid());
		novoEquipamento.setRede(rede);

		EquipamentoResponseDto response = equipamentoResponseModel.montarDtoEquipamento(novoEquipamento);

		return response;
	}

	public EquipamentoResponseDto buscarInformacoesEquipamentoUnico(String ip) {
		try {
			var redeFound = redeService.consultarRede(ip);			
			var response = buscarInformacoes(ip, redeFound);
			return response;

		} catch (Exception e) {
			// cadastraRede
			String rede = redeService.converterIpParaRede(ip);
			var redeFound = redeService.consultarRede(rede);
			
			System.out.println("AQUI" + redeFound);

			if (redeFound.equals(null)) {
				RedeRequestDto novaRede = new RedeRequestDto();
				novaRede.setRede(rede);

				redeService.cadastrarRede(novaRede);
				var response = buscarInformacoes(ip, redeFound);
				return response;
			} else {

				throw new RegraDeNegocioException(e.getMessage());
			}
		}
	}

	public EquipamentoResponseDto buscarInformacoes(String ip, Rede rede) {

		try {
			var pingIp = InetAddress.getByName(ip);
			int tempoEsperaMs = 1000;
			if (!pingIp.isReachable(tempoEsperaMs)) {
				throw new RegraDeNegocioException("IP não responde.");
			}

		} catch (Exception e) {
			throw new RegraDeNegocioException(e.getMessage());
		}

		TransportMapping<UdpAddress> canalDeTransporte = null;
		Snmp snmp = null;

		try {

			canalDeTransporte = new DefaultUdpTransportMapping(); // cria o canal de transporte
			canalDeTransporte.listen(); // coloca o canal pra escutar
			snmp = new Snmp(canalDeTransporte); // instancia o snmp que vai usar este canal

			CommunityTarget<UdpAddress> target = new CommunityTarget<UdpAddress>(); // Criei um alvo SNMP vazio

			// preenchendo o alvo
			target.setCommunity(new OctetString("public")); // senha SNMP é public
			target.setAddress(new UdpAddress(ip + "/161")); // ip de destino
			target.setVersion(SnmpConstants.version1); // estou falando versao1
			target.setTimeout(2000); // espero 2s
			target.setRetries(1); // tento mais 1 vez

			PDU pdu = new PDU(); // criei um envelope SNMP vazio
			pdu.setType(PDU.GET); // envelope tipo GET
			pdu.add(new VariableBinding(new OID("1.3.6.1.2.1.2.2.1.6.2"))); // mac
			pdu.add(new VariableBinding(new OID("1.3.6.1.2.1.1.5.0"))); // nome do radio
			pdu.add(new VariableBinding(new OID("1.3.6.1.4.1.41112.1.4.5.1.2.1"))); // ssid
			pdu.add(new VariableBinding(new OID("1.3.6.1.4.1.41112.1.4.5.1.5.1"))); // nivel de sinal
			pdu.add(new VariableBinding(new OID("1.3.6.1.4.1.41112.1.4.1.1.4.1"))); // canal do radio
			pdu.add(new VariableBinding(new OID("1.3.6.1.4.1.41112.1.4.5.1.4.1"))); // mac do ap

			var responsePdu = snmp.get(pdu, target); // envio a pergunta

			PDU respostaPdu = responsePdu.getResponse(); // PDU que o rádio enviou de volta

			if (respostaPdu == null)
				throw new RegraDeNegocioException("Erro ao conectar SNMP do rádio: " + ip);

			var response = equipamentoResponseModel.montarRespostaEquipamento(ip, respostaPdu);

			var equipamentoFound = equipamentoRepository.findByIp(ip);
			if (equipamentoFound.isEmpty()) {
				Equipamento novoEquipamento = new Equipamento();
				novoEquipamento.setIp(response.getIp());
				novoEquipamento.setRede(rede);
				novoEquipamento.setMac(response.getMac());
				novoEquipamento.setNomeRadio(response.getNomeRadio());
				novoEquipamento.setSsid(response.getSsid());
				novoEquipamento.setNivelDeSinal(response.getNivelDeSinal());
				novoEquipamento.setCanalRadio(response.getCanalRadio());
				novoEquipamento.setMacDoAp(response.getMacDoAp());
				novoEquipamento.setStatus(response.getStatus());
				equipamentoRepository.save(novoEquipamento);
				return response;
			} else { // primeiro cria o equipamento pra comparar com o do BD e salvar se nao existir
				Equipamento novoEquipamento = new Equipamento();
				novoEquipamento.setIp(response.getIp());
				novoEquipamento.setRede(rede);
				novoEquipamento.setMac(response.getMac());
				novoEquipamento.setNomeRadio(response.getNomeRadio());
				novoEquipamento.setSsid(response.getSsid());
				novoEquipamento.setNivelDeSinal(response.getNivelDeSinal());
				novoEquipamento.setCanalRadio(response.getCanalRadio());
				novoEquipamento.setMacDoAp(response.getMacDoAp());
				novoEquipamento.setStatus(response.getStatus());

				// comparar equipamento
				if (equipamentoFound.get().equals(novoEquipamento)) {
					return response;
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
					return response;
				}
			}
		} catch (Exception e) {
			throw new RegraDeNegocioException(e.getMessage());
		} finally {
			try {
				if (snmp != null)
					snmp.close();
			} catch (Exception ignore) {
			}
			try {
				if (canalDeTransporte != null)
					canalDeTransporte.close();
			} catch (Exception ignore) {
			}
		}
	}
}
