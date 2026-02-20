package br.com.easyembranet.services;

import java.util.ArrayList;
import java.util.List;

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

import br.com.easyembranet.dtos.response.equipamento.EquipamentoResponseDto;
import br.com.easyembranet.exceptions.RegraDeNegocioException;
import br.com.easyembranet.models.EquipamentoResponseModel;

@Service
public class EquipamentoService {

	@Autowired
	private EquipamentoResponseModel equipamentoResponseModel;
	
	public List<EquipamentoResponseDto> varrerLista(List<String> ips){
		
		List<EquipamentoResponseDto> listaDeResposta = new ArrayList<EquipamentoResponseDto>();
		
		for(var ip : ips) {
			var informacoesDoIp = buscarInformacoes(ip);
			listaDeResposta.add(informacoesDoIp);			
		}
		
		return listaDeResposta;
	}
	
	public EquipamentoResponseDto buscarInformacoes(String ip) {
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
			
			if(respostaPdu == null)
				throw new RegraDeNegocioException("Erro ao conectar SNMP do rádio: " + ip);
			
			var response = equipamentoResponseModel.montarRespostaEquipamento(ip, respostaPdu);	

			return response;

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
