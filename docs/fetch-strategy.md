# Stratégie de Fetch et de Cascade — AutoLoc

## 1. Tableau récapitulatif des associations

| Association | Annotations | Côté propriétaire | Fetch | Cascade | Justification métier & technique |
| :--- | :--- | :--- | :--- | :--- | :--- |
| **Contrat ↔ Paiement** | `@OneToMany` / `@ManyToOne` (Bidirectionnelle) | `Paiement` (`contrat_id_contrat`) | **LAZY** | **ALL + orphanRemoval** | Relation de composition forte : un paiement n'a aucun sens ni existence sans son contrat. La suppression du contrat entraîne celle de tous ses paiements (`cascade = ALL`), et le retrait d'un paiement de la collection le supprime de la base (`orphanRemoval = true`). |
| **Agence → Vehicule** | `@OneToMany` / `@ManyToOne` (Bidirectionnelle) | `Vehicule` (`id_agence`) | **LAZY** | **Aucune** | Cycle de vie indépendant : un véhicule survit à la fermeture ou réorganisation de son agence de rattachement (ex. transfert de flotte). Aucune suppression en cascade ne doit être propagée. |
| **Agence → Employe** | `@OneToMany` / `@ManyToOne` (Bidirectionnelle) | `Employe` (`id_agence`) | **LAZY** | **Aucune** | L'employé possède son propre cycle de vie au sein de l'entreprise. La modification ou suppression d'une agence ne doit pas supprimer ses employés. |
| **Vehicule ↔ Equipement** | `@ManyToMany` (Bidirectionnelle) | `Vehicule` (`@JoinTable vehicule_equipement`) | **LAZY** | **Aucune** | Équipements partagés (ex. GPS, siège bébé) pouvant être affectés à plusieurs véhicules successivement ou simultanément. La suppression d'un véhicule ne doit en aucun cas supprimer un équipement du catalogue. Utilisation d'un `Set` pour garantir l'unicité et optimiser les requêtes SQL d'Hibernate. |
| **Client → Reservation** | `@OneToMany` / `@ManyToOne` (Bidirectionnelle) | `Reservation` (`id_client`) | **LAZY** | **PERSIST** | Permet d'enregistrer commodément de nouvelles réservations en même temps qu'un client lors d'un premier parcours de réservation. En revanche, `REMOVE` est proscrit pour préserver l'historique comptable et contractuel des réservations en cas de modification/suppression client. |
| **Reservation → Vehicule** | `@ManyToOne` (Unidirectionnelle) | `Reservation` (`id_vehicule`) | **LAZY** | **Aucune** | La réservation fait référence à un véhicule du parc. Le véhicule a un cycle de vie totalement autonome et ne doit jamais être impacté par la création ou suppression d'une réservation. |
| **Reservation ↔ Contrat** | `@OneToOne` (Bidirectionnelle) | `Contrat` (`id_reservation`) | **LAZY** | **ALL** (côté `Reservation`) | Une réservation confirmée engendre son contrat de location. Si la réservation est persistée ou supprimée (annulation/nettoyage), son contrat associé l'est également (`cascade = CascadeType.ALL` sur `Reservation`). |
| **Vehicule → Maintenance** | `@OneToMany` / `@ManyToOne` (Bidirectionnelle) | `Maintenance` (`id_vehicule`) | **LAZY** | **PERSIST** | Permet d'enregistrer de nouvelles opérations de maintenance directement via l'objet véhicule. La suppression du véhicule ne supprime pas en cascade l'historique de maintenance afin de conserver la traçabilité technique. |

---

## 2. Choix de la stratégie de chargement (`FetchType.LAZY`)

### Pourquoi généraliser `FetchType.LAZY` sur l'ensemble du projet ?
1. **Éviter le chargement en chaîne (effet domino)** :
   - Par défaut en JPA, les associations to-one (`@ManyToOne` et `@OneToOne`) utilisent un `FetchType.EAGER`.
   - Si `EAGER` était conservé, charger une `Reservation` déclencherait immédiatement le chargement de son `Client`, de son `Vehicule` (qui chargerait son `Agence`, ses `Equipement`, ses `Maintenance`), et de son `Contrat` (qui chargerait ses `Paiement`).
   - Cela générerait des requêtes SQL gigantesques avec de multiples jointures inutiles (`LEFT OUTER JOIN`) et surchargerait la mémoire applicative.
2. **Contrôle des performances et prévention du problème N+1** :
   - En forçant `LAZY` sur tous les `@ManyToOne` et `@OneToOne`, et en conservant le `LAZY` par défaut sur `@OneToMany` et `@ManyToMany`, chaque entité est chargée de manière ciblée et légère.
   - Si les données associées sont requises dans une transaction ou un service spécifique, elles peuvent être chargées à la demande via des requêtes JPQL dédiées (`JOIN FETCH`) ou des `EntityGraph`.

---

## 3. Propagation du cycle de vie (`CascadeType`) et `orphanRemoval`

### Distinction fondamentale entre `cascade = REMOVE` et `orphanRemoval = true`
- **`cascade = CascadeType.REMOVE`** :
  - S'applique uniquement lors de la suppression explicite de l'entité parente (`contratRepository.delete(contrat)`).
  - Hibernate supprime alors automatiquement tous les paiements rattachés au contrat.
- **`orphanRemoval = true`** :
  - S'applique lorsque l'entité parente reste vivante en base, mais qu'un élément enfant est retiré de sa collection Java :
    ```java
    contrat.getPaiements().remove(paiement);
    ```
  - Hibernate détecte que le paiement est devenu orphelin et exécute automatiquement une requête SQL `DELETE FROM paiement WHERE id_paiement = ...`.
- **Combinaison `ALL + orphanRemoval`** :
  - Parfaite adéquation pour le modèle Contrat ↔ Paiement (relation de composition stricte).

### Pourquoi limiter `CascadeType.PERSIST` sur les autres associations ?
- Pour `Client → Reservation` et `Vehicule → Maintenance`, seul `PERSIST` est appliqué :
  - Il apporte du confort de développement lors de la création conjointe d'un parent et de ses enfants.
  - Il interdit toute cascade de suppression (`REMOVE`), protégeant ainsi l'intégrité référentielle et l'historique d'audit.

---

## 4. Choix de modélisation pour `Vehicule ↔ Equipement` (`Set` vs `List`)

- La spécification demande explicitement d'utiliser un `Set<Equipement>` et `Set<Vehicule>`.
- **Raisons techniques avec Hibernate** :
  1. **Gestion optimale de la table de jointure** : Avec un `java.util.List`, l'ajout ou la suppression d'un élément dans une collection `@ManyToMany` sans clé primaire surrogate force souvent Hibernate à supprimer **toutes** les lignes de la table de jointure pour cette entité (`DELETE FROM vehicule_equipement WHERE vehicule_id = ?`) puis à réinsérer la totalité de la liste. Avec un `Set`, Hibernate cible précisément la ligne modifiée (`INSERT` ou `DELETE` unitaire).
  2. **Unicité métier** : Un véhicule ne peut pas se voir attribuer deux fois le même équipement physique ou logique.

---

## 5. Bonnes pratiques Clean Code & Lombok

- **Éviter impérativement `@Data` sur les entités JPA** :
  - L'annotation `@Data` de Lombok génère automatiquement `equals()`, `hashCode()` et `toString()`.
  - Dans le cadre d'associations bidirectionnelles (`Contrat` ↔ `Paiement`, `Agence` ↔ `Vehicule`, etc.), l'appel à `toString()` ou `hashCode()` sur un objet appelle celui de l'objet lié, créant une boucle infinie et provoquant un `StackOverflowError`.
  - **Solution adoptée** : Utilisation exclusive des annotations ciblées `@Getter`, `@Setter`, `@NoArgsConstructor` et `@AllArgsConstructor`.
