package cz.cvut.fel.pm2.FinanceMicroservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class FinanceMicroserviceApplication {

	public static void main(String[] args) {
		SpringApplication.run(FinanceMicroserviceApplication.class, args);
	}

}
