package br.com.easyembranet.repositories;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import br.com.easyembranet.entities.Rede;
import br.com.easyembranet.projections.RedeResumoProjection;

@Repository
public interface RedeRepository extends JpaRepository<Rede, Long> {

	@Query("""
			SELECT r FROM Rede r
			WHERE r.rede = :rede
			""")
	Optional<Rede> findByRede(@Param("rede") String rede);

	Page<Rede> findAll(Pageable pageable);
	
	@Query("""
		    SELECT r.idRede as idRede,
		           r.rede as rede,
		           COUNT(e) as totalEquipamentos
		    FROM Rede r
		    LEFT JOIN r.equipamentos e
		    GROUP BY r.idRede, r.rede
		""")
		Page<RedeResumoProjection> findAllComTotal(Pageable pageable);

}
