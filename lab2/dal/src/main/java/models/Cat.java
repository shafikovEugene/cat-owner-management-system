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
@Table(name = "cats")
public class Cat {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cat_id")
    private long id;

    @Column(name = "name")
    private String name;

    @Column(name = "birth_date")
    private LocalDate birthDate;

    @Column(name = "breed")
    private String breed;

    @Column(name = "color")
    private String color;

    @ManyToOne
    @JoinColumn(name = "owner_id")
    private Owner owner;
    @ManyToMany
    @JoinTable(
            name = "cat_friendships",
            joinColumns = @JoinColumn(name = "cat_id"),
            inverseJoinColumns = @JoinColumn(name = "friend_cat_id")
    )
    private List<Cat> friends;

    public Cat() {}

    public Cat(String name, LocalDate birthDate, String breed, String color, Owner owner, List<Cat> friends) {
        this.name = name;
        this.birthDate = birthDate;
        this.breed = breed;
        this.color = color;
        this.owner = owner;
        this.friends = friends;
    }
}
