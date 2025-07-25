package me.bang9.api.user.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import me.bang9.api.global.entity.BaseEntity;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "agency", schema = "bang9", indexes = {
        @Index(name = "idx_agency_name", columnList = "name"),
})
@Getter
@Setter
@RequiredArgsConstructor
public class AgencyEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "address", nullable = false)
    private String address;

    @Column(name = "contact", nullable = false)
    private String contact;

    @ManyToMany(mappedBy = "memberAgencyList")
    private Set<UserEntity> members = new HashSet<>();

    @OneToOne(mappedBy = "representingAgency")
    private UserEntity representingUser;

}
