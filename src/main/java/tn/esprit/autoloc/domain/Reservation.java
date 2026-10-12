package tn.esprit.autoloc.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "reservation")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idReservation;

    private LocalDate dateDebut;
    private LocalDate dateFin;

    @Enumerated(EnumType.STRING)
    private StatutReservation statut;

    // Association ManyToOne vers Client (côté propriétaire)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_client")
    private Client client;

    // Association ManyToOne unidirectionnelle vers Vehicule (côté propriétaire)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_vehicule")
    private Vehicule vehicule;

    // Association OneToOne bidirectionnelle avec Contrat (côté inverse avec cascade ALL)
    @OneToOne(mappedBy = "reservation", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Contrat contrat;
}
