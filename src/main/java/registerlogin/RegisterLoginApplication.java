package registerlogin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import registerlogin.model.Role;
import registerlogin.model.RoleName;
import registerlogin.repository.RoleRepository;

@SpringBootApplication
public class RegisterLoginApplication {
	
	@Autowired
	RoleRepository roleRepository;

	public static void main(String[] args) {
		SpringApplication.run(RegisterLoginApplication.class, args);
		
		System.out.print("\n");
		System.out.print("App started ...");
	}

	@Bean
	CommandLineRunner runner() {
		return args -> {
          //  roleRepository.deleteAll();
			Role adminRole = new Role(RoleName.ROLE_ADMIN);
			Role useRole = new Role(RoleName.ROLE_USER);
			roleRepository.save(adminRole);
			roleRepository.save(useRole);
		};
	}
}
