package com.bdi.agent.model;

import com.google.gson.annotations.Expose;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;

@Entity
@Table
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Belief {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NonNull
    @Expose
    private String name;
    @NonNull
    @Expose
    private String fullName;
    @NonNull
    @Expose
    private String phase;
    @NonNull
    @Expose
    private Float value;

    /**
     * Construct a new belief.
     *
     * @param name The name of the belief
     * @param fullName The full name of the belief
     * @param phase The phase of the belief
     * @param value The value of the belief
     */
    public Belief(String name, String fullName, String phase, Float value) {
        this.name = name;
        this.fullName = fullName;
        this.phase = phase;
        this.value = value;
    }

    /**
     * Construct a new belief.
     *
     * @param name The name of the belief
     * @param fullName The full name of the belief
     * @param value The value of the belief
     */
    public Belief(String name, String fullName, Float value) {
        this.name = name;
        this.fullName = fullName;
        this.value = value;
    }

    /**
     * If this object equals another.
     *
     * @param o The other object
     * @return If they are equal
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Belief belief = (Belief) o;

        return getName().equals(belief.getName());
    }

    @Override
    public int hashCode() {
        return getName().hashCode();
    }
}

