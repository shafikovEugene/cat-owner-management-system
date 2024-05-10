package models;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "owners")
public class Owner {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "owner_id")
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "birth_date")
    private LocalDate birthDate;

    @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL, fetch=FetchType.EAGER, orphanRemoval = true)
    private List<Cat> cats;

    public Owner() {}

    public Owner(String name, LocalDate birthDate, List<Cat> cats) {
        this.name = name;
        this.birthDate = birthDate;
        this.cats = cats;
    }
}
