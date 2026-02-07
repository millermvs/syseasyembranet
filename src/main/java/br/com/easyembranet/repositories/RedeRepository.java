package br.com.easyembranet.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.com.easyembranet.entities.Rede;

@Repository
public interface RedeRepository extends JpaRepository<Rede, Long> {

}
