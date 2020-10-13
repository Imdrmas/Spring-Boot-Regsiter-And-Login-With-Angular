
package registerlogin.controller;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.*;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import registerlogin.message.request.LoginForm;
import registerlogin.message.request.SignUpForm;
import registerlogin.message.response.JwtResponse;
import registerlogin.message.response.ResponseMessage;
import registerlogin.model.Role;
import registerlogin.model.RoleName;
import registerlogin.model.User;
import registerlogin.repository.RoleRepository;
import registerlogin.repository.UserRepository;
import registerlogin.security.jwt.JwtProvider;


@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/api/auth")
public class AuthController {

	@Autowired
	AuthenticationManager authenticationManager;

	@Autowired
	UserRepository userRepository;

	@Autowired
	RoleRepository roleRepository;

	@Autowired
	PasswordEncoder encoder;

	@Autowired
	JwtProvider jwtProvider;

	@PostMapping("/login")
	public ResponseEntity<?> login(@Valid @RequestBody LoginForm loginRequest, 
			BindingResult result) {

		Authentication authentication = authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

		SecurityContextHolder.getContext().setAuthentication(authentication);

		String jwt = jwtProvider.generateJwtToken(authentication);
		UserDetails userDetails = (UserDetails) authentication.getPrincipal();
		
		if (result.hasErrors()) {
			return ResponseEntity.ok(new JwtResponse(jwt, userDetails.getUsername(), userDetails.getAuthorities()));
		}

		return ResponseEntity.ok(new JwtResponse(jwt, userDetails.getUsername(), userDetails.getAuthorities()));
	}

	@PostMapping("/register")
	public ResponseEntity<?> register(@Valid @RequestBody SignUpForm signUpRequest) {
		if (userRepository.existsByUsername(signUpRequest.getUsername())) {
			return new ResponseEntity<>(new ResponseMessage("Fail -> Username is already taken!"),
					HttpStatus.BAD_REQUEST);
		}

		if (userRepository.existsByEmail(signUpRequest.getEmail())) {
			return new ResponseEntity<>(new ResponseMessage("Fail -> Email is already in use!"),
					HttpStatus.BAD_REQUEST);
		}

		// Creating user's account
		User user = new User(signUpRequest.getName(), signUpRequest.getUsername(), signUpRequest.getEmail(),
				encoder.encode(signUpRequest.getPassword()), signUpRequest.getGender(), 
				signUpRequest.getBirthDate(), new Date());

		Set<String> strRoles = signUpRequest.getRole();
		Set<Role> roles = new HashSet<>();

		strRoles.forEach(role -> {
			switch (role) {
			case "admin":
				Role adminRole = roleRepository.findByName(RoleName.ROLE_ADMIN)
						.orElseThrow(() -> new RuntimeException("Fail! -> Cause: User Role not find."));
				roles.add(adminRole);
				
				break;
			default:
				Role userRole = roleRepository.findByName(RoleName.ROLE_USER)
						.orElseThrow(() -> new RuntimeException("Fail! -> Cause: User Role not find."));
				roles.add(userRole);
			}
		});

		user.setRoles(roles);
		userRepository.save(user);

		return new ResponseEntity<>(new ResponseMessage("User registered successfully!"), HttpStatus.OK);
	}
	@GetMapping("/findAllUsers")
	public List<User> findAllUsers() {
		return userRepository.findAll();
	}
	   @GetMapping("/getUserByUsername/{username}")
	    public Optional<User> getUserByUsername(@PathVariable("username") String username) {
	        return userRepository.findByUsername(username);
	    }
	    @PutMapping("/updateUser/{id}")
	    public ResponseEntity<User> updateUser(@PathVariable("id") Long userId, @RequestBody User user){
	        Optional<User> users = userRepository.findById(userId);
	        if (users.isPresent()) {
	            User _user = users.get();
	            _user.setName(user.getName());
	            _user.setUsername(user.getUsername());
	            _user.setEmail(user.getEmail());
	            _user.setCreateAt(new Date());
	            _user.setPassword(encoder.encode(user.getPassword()));
	            return new ResponseEntity<User>(userRepository.save(_user), HttpStatus.OK);
	        }
	        return new ResponseEntity<User>(HttpStatus.NOT_FOUND);
	    }
	    @DeleteMapping("/deleteUser/{id}")
	    public ResponseEntity<String> deleteUser(@PathVariable("id") Long userId) {
	        userRepository.deleteById(userId);
	        return new ResponseEntity<String>("User deleted", HttpStatus.OK);
	    }
	    @GetMapping("/getUserById/{id}")
	    public Optional<User> getUserById(@PathVariable("id") Long userId) {
	        return userRepository.findById(userId);
	    }

}
