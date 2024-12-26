package br.com.alura.screenmatch;

import br.com.alura.screenmatch.principal.Principal;
import br.com.alura.screenmatch.repository.SerieRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class  ScreenmatchApplication implements CommandLineRunner {

	// Provisoriamente, essa anotação ficará numa classe que o FrameWork do Spring gerencia, caso contrário ele não consegue ficar responsável pelo salvamento no banco
	@Autowired
	private SerieRepository repositorio;

	// Criando uma instância de classe por meio de reflexão (ScreenmatchApplication.class)
	public static void main(String[] args) {
		SpringApplication.run(ScreenmatchApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		Principal principal = new Principal(repositorio); // Usando um construtor que recebe o repositorio
		principal.exibeMenu();
	}
}

