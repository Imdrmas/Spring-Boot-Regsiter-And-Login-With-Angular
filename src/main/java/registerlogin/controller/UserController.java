
package registerlogin.controller;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/api/auth")
public class UserController {
	
	@GetMapping("/user")
	//@PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
	@Secured({"ROLE_USER", "ROLE_ADMIN"})
	public String userAccess() {
		return ">>> User Contents!";
	}

	
	@GetMapping("/admin")
	//@PreAuthorize("hasRole('ADMIN')")
	@Secured("ROLE_ADMIN")
	public String adminAccess() {
		return ">>> Admin Contents";
	}
	
}
