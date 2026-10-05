package Silvero.EstudioArquitectura;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.TimeZone;

@SpringBootApplication
public class EstudioArquitecturaApplication {

	public static void main(String[] args) {
		// Windows informa la zona "America/Buenos_Aires", un alias viejo que PostgreSQL rechaza
		// al conectarse. Se reemplaza por el nombre actual de la misma zona.
		if ("America/Buenos_Aires".equals(TimeZone.getDefault().getID())) {
			TimeZone.setDefault(TimeZone.getTimeZone("America/Argentina/Buenos_Aires"));
		}
		SpringApplication.run(EstudioArquitecturaApplication.class, args);
	}

}
