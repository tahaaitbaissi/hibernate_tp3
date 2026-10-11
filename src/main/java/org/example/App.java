package org.example;

import org.example.models.*;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.time.LocalDateTime;

public class App {
    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("hibernate_tp3_pu");
        EntityManager em = emf.createEntityManager();

        try {
            // 1. Insertion des données
            em.getTransaction().begin();

            Utilisateur user = new Utilisateur();
            user.setEmail("taha.aitbaissi@emsi-edu.ma");
            user.setNom("Taha Ait Baissi");

            Salle salle = new Salle();
            salle.setNom("Salle de Réunion");
            salle.setCapacite(25);

            Equipement projecteur = new Equipement();
            projecteur.setNom("Vidéoprojecteur");
            salle.addEquipement(projecteur);

            em.persist(user);
            em.persist(salle);

            Reservation reservation = new Reservation();
            reservation.setDateDebut(LocalDateTime.now().plusDays(1).withHour(10).withMinute(0));
            reservation.setDateFin(LocalDateTime.now().plusDays(1).withHour(12).withMinute(0));

            user.addReservation(reservation);
            salle.addReservation(reservation);

            em.getTransaction().commit();

            // 2. Tests des requêtes JPQL (Exercice 13)
            System.out.println("\n--- Requêtes JPQL ---");
            em.createQuery("SELECT r FROM Reservation r WHERE r.utilisateur.email = :email", Reservation.class)
                    .setParameter("email", "taha.aitbaissi@emsi-edu.ma")
                    .getResultList()
                    .forEach(r -> System.out.println("Réservation trouvée : " + r.getSalle().getNom()));

            LocalDateTime checkStart = LocalDateTime.now().plusDays(1).withHour(9);
            LocalDateTime checkEnd = LocalDateTime.now().plusDays(1).withHour(11);

            Long conflits = em.createQuery(
                            "SELECT COUNT(r) FROM Reservation r WHERE r.salle.id = :salleId " +
                                    "AND (r.dateDebut < :end AND r.dateFin > :start)", Long.class)
                    .setParameter("salleId", salle.getId())
                    .setParameter("start", checkStart)
                    .setParameter("end", checkEnd)
                    .getSingleResult();

            if (conflits == 0) {
                System.out.println("Statut : La salle est disponible.");
            } else {
                System.out.println("Statut : La salle est déjà réservée sur ce créneau (Conflit).");
            }
            System.out.println("---------------------\n");

            // On vide le cache avant la suppression
            em.clear();

            // 3. Test suppression en cascade
            em.getTransaction().begin();

            Utilisateur userToDelete = em.find(Utilisateur.class, user.getId());
            if (userToDelete != null) {
                em.remove(userToDelete);
            }

            em.getTransaction().commit();
            System.out.println("Suppression en cascade validée.");

        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            e.printStackTrace();
        } finally {
            em.close();
            emf.close();
        }
    }
}