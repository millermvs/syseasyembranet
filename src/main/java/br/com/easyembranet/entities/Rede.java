package br.com.easyembranet.entities;

import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "redes")
public class Rede {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	private Long idRede;

	@Column
	private String rede;

	@OneToMany(mappedBy = "rede", fetch = FetchType.LAZY)
	private Set<Equipamento> equipamentos = new HashSet<Equipamento>();
}
