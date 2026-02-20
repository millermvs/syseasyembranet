package br.com.easyembranet.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import br.com.easyembranet.entities.Equipamento;

@Repository
public interface EquipamentoRepository extends JpaRepository<Equipamento, Long> {

	@Query("""
			SELECT e FROM Equipamento e
			WHERE e.ip = :ip
			""")
	Optional<Equipamento> findByIp(@Param("ip") String ip);

}
