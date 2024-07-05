package com.bdi.agent.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.persistence.ElementCollection;
import javax.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Phase {
    @ElementCollection
    private Map<Belief, Desire> beliefToDesireMapping = new HashMap<>();

    @OneToMany
    private List<Desire> desires;
}
