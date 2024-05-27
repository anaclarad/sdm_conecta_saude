package br.unibh.sdm.persistencia;

import java.util.List;
import java.util.UUID;

import org.socialsignin.spring.data.dynamodb.repository.EnableScan;
import org.springframework.data.repository.CrudRepository;

import br.unibh.sdm.entidades.Paciente;

/**
 * Esta classe estende o padrão CrudRepository 
 * @author jhcru
 *
 */
@EnableScan()
public interface pacienteRepository extends CrudRepository<Paciente, UUID> {
	
	List<Paciente> findByNome(String nome);
	
}
