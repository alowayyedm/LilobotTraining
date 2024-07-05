package com.bdi.agent.model;

import com.google.gson.annotations.Expose;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table
@Getter
@Setter
public class Action {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne(cascade =  CascadeType.ALL)
    @JoinColumn(name = "desire_id", referencedColumnName = "id")
    private Desire desire;

    @Expose
    private String type;

    @Expose
    private String name;

    @Expose
    private String subject;

    @Expose
    private String attribute;

    @Expose
    private Boolean completed;

    public Action() {

    }

    /**
     *Constructs a new action class.
     */
    public Action(Desire desire, String type, String name, String subject, String attribute, Boolean isCompleted) {
        this.desire = desire;
        this.type = type;
        this.name = name;
        this.subject = subject;
        this.attribute = attribute;
        this.completed = isCompleted;
    }
}
