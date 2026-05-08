package com.moment.momentbackend.child.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "child_concern")
public class ChildConcern {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "child_id", nullable = false)
    private ChildProfile childProfile;

    @Column(nullable = false)
    private String concern;

    @Builder
    public ChildConcern(ChildProfile childProfile, String concern) {
        this.childProfile = childProfile;
        this.concern = concern;
    }
}