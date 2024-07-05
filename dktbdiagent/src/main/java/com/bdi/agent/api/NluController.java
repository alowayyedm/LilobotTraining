package com.bdi.agent.api;

import com.bdi.agent.service.NluService;
import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin(origins = {"http://${server.web}"})
@RestController
@RequestMapping("nlu")
public class NluController {

    @Value("${rasa.token}")
    private String rasaToken;

    private final NluService nluService;

    @Autowired
    public NluController(NluService nluService) {
        this.nluService = nluService;
    }

    @GetMapping("/")
    public ResponseEntity<String> hello() {
        return ResponseEntity.ok("Hello, NLU!");
    }

    /**
     * Retrieve a list of all intents.
     *
     * @return The list of intents
     */
    @GetMapping("/intents")
    public ResponseEntity<Map<String, List<String>>> getIntents() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            StringBuilder roles = new StringBuilder("User: " + authentication.getName() + " has roles: ");
            for (GrantedAuthority authority : authentication.getAuthorities()) {
                roles.append(authority.getAuthority()).append(" ");
            }
            System.out.println(roles.toString());
        }
        return ResponseEntity.ok(nluService.getIntentsAndExamples());
    }

    @PutMapping("/intents/{intent}")
    public ResponseEntity<String> updateIntents(@PathVariable String intent, @RequestBody String[] examples) {
        nluService.updateIntent(intent, List.of(examples));
        return ResponseEntity.ok("Intent updated successfully");
    }

    @GetMapping("/auth")
    public ResponseEntity<String> getAuth() {
        return ResponseEntity.ok(this.rasaToken);
    }
}
