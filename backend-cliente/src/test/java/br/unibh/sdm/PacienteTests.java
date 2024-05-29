package br.unibh.sdm;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

import static org.junit.Assert.assertEquals;
import org.junit.FixMethodOrder;
import org.junit.Test;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.socialsignin.spring.data.dynamodb.repository.config.EnableDynamoDBRepositories;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.context.PropertyPlaceholderAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.junit4.SpringRunner;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;

import br.unibh.sdm.entidades.Paciente;
import br.unibh.sdm.persistencia.pacienteRepository;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {PropertyPlaceholderAutoConfiguration.class, PacienteTests.DynamoDBConfig.class})
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class PacienteTests {

    private static Logger LOGGER = LoggerFactory.getLogger(PacienteTests.class);
    private SimpleDateFormat df = new SimpleDateFormat("dd/mm/yyyy");

    @Configuration
	@EnableDynamoDBRepositories(basePackageClasses = { pacienteRepository.class })
	public static class DynamoDBConfig {

		@Value("${amazon.aws.accesskey}")
		private String amazonAWSAccessKey;

		@Value("${amazon.aws.secretkey}")
		private String amazonAWSSecretKey;

		public AWSCredentialsProvider amazonAWSCredentialsProvider() {
			return new AWSStaticCredentialsProvider(amazonAWSCredentials());
		}

		@Bean
		public AWSCredentials amazonAWSCredentials() {
			return new BasicAWSCredentials(amazonAWSAccessKey, amazonAWSSecretKey);
		}

		@Bean
		public AmazonDynamoDB amazonDynamoDB() {
			return AmazonDynamoDBClientBuilder.standard().withCredentials(amazonAWSCredentialsProvider())
					.withRegion(Regions.US_EAST_1).build();
		}
	}
    
	@Autowired
	private pacienteRepository repository;

    @Test
    public void teste1Criacao() throws ParseException {
        LOGGER.info("Criando objetos...");
        Paciente p1 = new Paciente("João Silva", "joao.silva@example.com", "123456789", "senha123", df.format(new java.util.Date()), df.format(new java.util.Date()));
        Paciente p2 = new Paciente("Maria Oliveira", "maria.oliveira@example.com", "987654321", "senha456", df.format(new java.util.Date()), df.format(new java.util.Date()));
        repository.save(p1);
        repository.save(p2);
        Iterable<Paciente> lista = repository.findAll();
      	assertTrue(lista.iterator().hasNext());
        for (Paciente pac : lista) {
            LOGGER.info(pac.toString());
        }
        LOGGER.info("Pesquisando um objeto");
        List<Paciente> result = repository.findByNome("João Silva");
        assertEquals(1, result.size());
        LOGGER.info("Encontrado: {}", result.size());
    }

    @Test
    public void teste2Exclusao() throws ParseException {
        LOGGER.info("Excluindo objetos...");
        List<Paciente> result = repository.findByNome("João Silva");
        for (Paciente pac : result) {
            LOGGER.info("Excluindo paciente id = " + pac.getId());
            repository.delete(pac);
        }
        result = repository.findByNome("João Silva");
        assertEquals(0, result.size());
        LOGGER.info("Exclusão feita com sucesso");
    }
}