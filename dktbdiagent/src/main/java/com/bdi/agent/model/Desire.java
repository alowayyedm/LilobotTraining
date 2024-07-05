package com.bdi.agent.model;

import com.bdi.agent.model.enums.Phase;
import com.google.gson.annotations.Expose;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Entity
@Table
@RequiredArgsConstructor
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Desire {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @NonNull
    @Expose
    private String name;
    @NonNull
    @Expose
    private String fullName;
    private boolean active = false;
    @NonNull
    @Expose
    @Enumerated(EnumType.STRING)
    private Phase phase;

    @OneToMany(cascade = CascadeType.ALL)
    @Expose
    private Set<Action> actions;

    /**
     * Construct a new desire.
     *
     * @param id The id of the desire
     * @param phase The phase of the desire
     * @param name The name of the desire
     * @param fullName The full name of the desire
     * @param active If the desire is active right now
     * @param actions The actions of the desire
     */
    public Desire(Long id, Phase phase, @NonNull String name, @NonNull String fullName,
            @NonNull Boolean active, Set<Action> actions) {
        this.id = id;
        this.name = name;
        this.fullName = fullName;
        this.active = active;
        this.actions = actions;
        this.phase = phase;
    }
}
