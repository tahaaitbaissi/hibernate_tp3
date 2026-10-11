package org.example.models;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "equipements") // Spécifier le nom de la table est une bonne pratique
public class Equipement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 150)
    private String nom;

    // Côté "esclave" de la relation ManyToMany (mappé par la propriété "equipements" dans Salle)
    @ManyToMany(mappedBy = "equipements")
    private Set<Salle> salles = new HashSet<>();

    // Constructeur par défaut obligatoire pour JPA/Hibernate
    public Equipement() {
    }

    public Equipement(String nom) {
        this.nom = nom;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public Set<Salle> getSalles() {
        return salles;
    }

    public void setSalles(Set<Salle> salles) {
        this.salles = salles;
    }

    // Remarque : On omet la liste "salles" dans le toString pour éviter les boucles infinies (StackOverflowError)
    @Override
    public String toString() {
        return "Equipement {" +
                "id=" + id +
                ", nom='" + nom + '\'' +
                '}';
    }
}