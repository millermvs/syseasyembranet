package br.com.easyembranet.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "equipamentos")
public class Equipamento {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	private Long idEquipamento;

	@Column
	private String ip;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_rede")
	private Rede rede;
	
	@Column
	private String mac;
	
	@Column
	private String nomeRadio;
	
	@Column
	private String ssid;
	
	@Column
	private String nivelDeSinal;
	
	@Column
	private String canalRadio;
	
	@Column
	private String macDoAp;
	
	@Column
	private String Status;
}
