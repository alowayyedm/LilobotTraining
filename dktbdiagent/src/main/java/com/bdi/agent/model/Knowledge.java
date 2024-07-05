package com.bdi.agent.model;

import com.google.gson.annotations.Expose;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Entity
@Table
@NoArgsConstructor
@RequiredArgsConstructor
@Getter
@Setter
public class Knowledge {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NonNull
    @Expose
    private String knowledge;
    @NonNull
    @Expose
    private String type;
    @NonNull
    @Expose
    private String subject;
    @NonNull
    @Expose
    private String attribute;

    @ElementCollection
    @Expose
    //    @CollectionTable(name = "responses", joinColumns = @JoinColumn(name = "id"))
    //    @Column(name = "responses")
    private List<String> values = new ArrayList<>();

    /**
     * Construct new knowledge.
     *
     * @param knowledge The knowledge name
     * @param subject The subject
     * @param attribute The attribute
     */
    public Knowledge(String knowledge, String subject, String attribute) {
        this.knowledge = knowledge;
        this.subject = subject;
        this.attribute = attribute;
    }

    public String getIntentionName() {
        return this.getType() + "_" + this.getSubject() + "_" + this.getAttribute();
    }
}
