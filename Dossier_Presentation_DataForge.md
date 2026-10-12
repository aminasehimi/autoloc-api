# Dossier de Présentation Orale (10 Minutes)
## Projet Advanced Machine Learning — Data Preparation & Classification
**Dataset :** *Diabetes 130-US Hospitals for Years 1999–2008 (UCI)*  
**Groupe :** **DataForge**  
**Membres :**  
- **Ben Hamouda Firas**  
- **Nour Mzoughi**  
- **Baha Ben Kilani**  
- **Amina Sehimi**  
- **Sabri Bouallegi**  
- **moemen bennannou**  

---

## ⏱️ Répartition du Temps (Total : 10 minutes)

| Slide | Titre de la Diapositive | Durée Cible | Timing Cumulé |
| :--- | :--- | :---: | :---: |
| **Slide 1** | Introduction & Présentation de l'Équipe DataForge | 0 min 45 s | 00:00 - 00:45 |
| **Slide 2** | Contexte Clinique & Problématique Métier | 1 min 15 s | 00:45 - 02:00 |
| **Slide 3** | Exploration des Données & Chiffres Clés | 1 min 15 s | 02:00 - 03:15 |
| **Slide 4** | Audit & Problèmes de Qualité Identifiés | 1 min 15 s | 03:15 - 04:30 |
| **Slide 5** | Stratégies de Nettoyage & Traitement des Manquants | 1 min 15 s | 04:30 - 05:45 |
| **Slide 6** | Feature Engineering : Regroupement ICD-9 & Âge | 1 min 15 s | 05:45 - 07:00 |
| **Slide 7** | Démarche de Classification & Pipeline ML | 1 min 15 s | 07:00 - 08:15 |
| **Slide 8** | Résultats Comparatifs & Analyse des Métriques | 1 min 30 s | 08:15 - 09:45 |
| **Slide 9** | Enseignements Métier, Limites & Perspectives | 0 min 45 s | 09:45 - 10:30 |
| **Slide 10** | Conclusion & Échanges (Q&A) | 0 min 15 s | 10:30 |

---

## 📑 Détail des Diapositives & Script Orateur

### Slide 1 : Titre & Membres du Groupe (00:00 - 00:45)
* **Titre :** Prédiction des Réadmissions Hospitalières chez les Patients Diabétiques
* **Sous-titre :** Projet Advanced Machine Learning — Data Preparation & Classification
* **Groupe :** DataForge
* **Membres :**
  * Ben Hamouda Firas
  * Nour Mzoughi
  * Baha Ben Kilani
  * Amina Sehimi
  * Sabri Bouallegi
  * moemen bennannou

> 🎙️ **Ce que vous dites à l'oral (45s) :**  
> *"Bonjour à toutes et à tous. Nous sommes l'équipe DataForge, composée de Ben Hamouda Firas, Nour Mzoughi, Baha Ben Kilani, Amina Sehimi, Sabri Bouallegi et Moemen Bennannou. Nous vous présentons aujourd'hui notre projet d'Advanced Machine Learning portant sur la prédiction des réadmissions hospitalières pour les patients diabétiques, basé sur l'historique clinique de 130 hôpitaux américains. En 10 minutes, nous allons vous exposer notre démarche complète : de l'audit de qualité des données jusqu'à l'interprétation clinique des modèles de classification."*

---

### Slide 2 : Contexte et Problématique Métier (00:45 - 02:00)
* **L'enjeu hospitalier :**
  * Le diabète est une maladie chronique entraînant de lourdes complications et des séjours fréquents.
  * Les réadmissions précoces à moins de 30 jours traduisent une rechute ou un défaut d'ajustement thérapeutique.
  * Aux États-Unis, le programme fédéral HRRP (Medicare/CMS) impose de fortes pénalités financières aux établissements ayant des taux excessifs de réadmission.
* **La problématique retenue :**
  > *« Peut-on prédire le statut de réadmission d'un patient diabétique à partir de ses caractéristiques cliniques, de ses diagnostics et de son schéma thérapeutique ? »*
* **La variable cible (`readmitted`) :**
  * `NO` (53,91 %) : Aucune réadmission enregistrée.
  * `>30` (34,93 %) : Réadmission après 30 jours.
  * `<30` (11,16 %) : Réadmission précoce (< 30 jours) — **La classe critique à fort risque médical.**

> 🎙️ **Ce que vous dites à l'oral (1m15s) :**  
> *"Le diabète représente un fardeau majeur pour les systèmes de santé. Lorsqu'un patient sort de l'hôpital et revient en urgence en moins de 30 jours, c'est non seulement un risque vital pour lui, mais aussi une sanction financière directe pour l'hôpital via le programme américain HRRP. Notre objectif est donc de prédire la variable cible 'readmitted'. Cette cible compte trois modalités : 'NO', '>30 jours' et '<30 jours'. Gardez en tête que la classe critique '<30 jours' ne représente que 11,2 % des cas. C'est elle qui concentre l'enjeu médical et financier, ce qui place d'emblée la gestion du déséquilibre au cœur de notre démarche."*

---

### Slide 3 : Principales Observations lors de l'Exploration (02:00 - 03:15)
* **Dimensions & Volumétrie :**
  * **101 766 séjours** hospitaliers et **50 variables** initiales.
  * **71 518 patients distincts** : 16 773 patients présentent des séjours multiples (jusqu'à 40 admissions pour certains).
* **Parcours de soins :**
  * Durée d'hospitalisation moyenne : 4,4 jours (médiane à 4 jours).
  * Traitement médicamenteux lourd : 16 médicaments prescrits en moyenne par séjour.
* **Suivi biologique & laboratoire :**
  * Test HbA1c (`A1Cresult`) : Absent / non mesuré dans **83,3 %** des séjours.
  * Glucose sérique (`max_glu_serum`) : Absent dans **94,7 %** des cas.
  * L'absence de test reflète la réalité du terrain et constitue une information clinique en soi.

> 🎙️ **Ce que vous dites à l'oral (1m15s) :**  
> *"Lors de l'exploration initiale, nous observons plus de 101 000 séjours pour 50 attributs. Un fait saillant immédiat : nous avons 71 518 patients uniques. Plus de 16 000 patients ont donc été réadmis à répétition au cours des 10 années de recueil. Deuxième constat capital : bien que l'hémoglobine glyquée soit le biomarqueur d'or du diabète, elle n'est mesurée que dans 17 % des admissions. Cela montre que dans les dossiers réels, l'absence d'examen n'est pas un oubli aléatoire mais le reflet d'une pratique clinique que le modèle devra savoir appréhender."*

---

### Slide 4 : Problèmes Identifiés dans les Données (03:15 - 04:30)
1. **Valeurs manquantes dissimulées :**
   * Présence du caractère `'?'` à la place de `NaN`.
   * `weight` manquant à **96,85 %** (inutilisable).
   * `payer_code` (39,55 %) et `medical_specialty` (49,08 %) fortement incomplets.
2. **Codes numériques qui sont en réalité catégoriels :**
   * `admission_type_id`, `discharge_disposition_id`, `admission_source_id` sont stockés en entiers mais désignent des catégories nominales.
3. **Explosion de dimensionnalité (Haute cardinalité) :**
   * Codes de diagnostic CIM-9 (`diag_1`, `diag_2`, `diag_3`) : plus de 700 à 800 codes distincts par colonne.
4. **Piège de l'Accuracy :**
   * Le déséquilibre de classe rend l'accuracy fallacieuse (un modèle naïf prédisant 'NO' obtiendrait 54 % d'accuracy avec 0 % de détection sur la classe critique).

> 🎙️ **Ce que vous dites à l'oral (1m15s) :**  
> *"L'audit de qualité des données a mis en lumière 4 écueils majeurs : Premièrement, les valeurs manquantes étaient masquées sous forme de points d'interrogation, avec un poids absent à 96,8 %. Deuxièmement, des variables administratives clés étaient typées en entiers alors qu'il s'agit d'identifiants nominaux. Troisièmement, les trois diagnostics médicaux comportaient plus de 700 codes CIM-9 chacun : un encodage direct aurait généré plus de 2 000 colonnes creuses et du surapprentissage. Enfin, le fort déséquilibre de la cible nous interdisait formellement de nous reposer sur la seule métrique d'accuracy."*

---

### Slide 5 : Techniques de Nettoyage et de Transformation (04:30 - 05:45)
* **Suppression sélective :**
  * Retrait de `encounter_id` (identifiant pur sans pouvoir prédictif).
  * Suppression de `weight` (> 96 % manquant, imputation impossible).
  * Exclusion de `patient_nbr` de l'apprentissage pour éviter les biais de mémorisation.
* **Imputation sémantique explicite :**
  * `race` et `medical_specialty` : Remplacement des `'?'` par `'Unknown'`.
  * `A1Cresult` et `max_glu_serum` : Imputation explicite par `'Not_measured'`.
* **Standardisation & Encodage (Pipeline scikit-learn) :**
  * `OneHotEncoder(handle_unknown='ignore')` pour les variables nominales.
  * `StandardScaler` pour les variables numériques d'activité de soins (durée, médicaments, analyses).
  * `ColumnTransformer` pour garantir l'absence de fuite de données (*Data Leakage*).

> 🎙️ **Ce que vous dites à l'oral (1m15s) :**  
> *"Pour nettoyer ces données sans altérer leur sens, nous avons rejeté les solutions simplistes. Nous avons éliminé l'identifiant de visite et la colonne poids. Pour la spécialité médicale et la race, plutôt que de supprimer des lignes, nous avons attribué la modalité 'Unknown'. Pour les tests de biologie, nous avons créé la modalité 'Not_measured' car le fait qu'un médecin décide ou non de tester le glucose est un indicateur de son diagnostic clinique. Enfin, nous avons encapsulé le centrage-réduction des variables numériques et le One-Hot Encoding dans un ColumnTransformer scikit-learn garantissant l'étanchéité absolue entre entraînement et test."*

---

### Slide 6 : Feature Engineering Spécifique (05:45 - 07:00)
* **Regroupement Clinique des Diagnostics (CIM-9 / ICD-9) :**
  * Regroupement des >700 codes en **9 macro-catégories médicales** :
    1. *Circulatoire* (390–459, 785) — pathologie dominante.
    2. *Diabète* (250.xx).
    3. *Respiratoire* (460–519, 786).
    4. *Digestif* (520–579, 787).
    5. *Génito-urinaire* (580–629, 788).
    6. *Néoplasmes / Cancers* (140–239).
    7. *Lésions & Traumatismes* (800–999).
    8. *Musculosquelettique* (710–739).
    9. *Autres diagnostics*.
* **Transformation de l'Âge :**
  * Intervalles initiaux textuels `[0-10)`, `[10-20)`... convertis en valeurs numériques médianes (5, 15, ..., 75, 95 ans) pour modéliser une relation d'ordre continue.
* **Stratification Train / Test :**
  * Split 80 % / 20 % stratifié sur `readmitted`.

> 🎙️ **Ce que vous dites à l'oral (1m15s) :**  
> *"Notre étape clé de Feature Engineering a porté sur la simplification des diagnostics. En nous appuyant sur la nomenclature médicale internationale CIM-9, nous avons regroupé les centaines de codes en 9 grandes catégories cliniques : maladies circulatoires, diabète, appareil respiratoire, appareil digestif, etc. Cela permet au modèle de capturer des profils de comorbidité robustes sans explosion dimensionnelle. De plus, la variable âge a été convertie de tranches textuelles vers des âges médians continus. L'échantillon a ensuite été découpé à 80/20 avec une stratification rigoureuse préservant la répartition des 3 classes."*

---

### Slide 7 : Démarche de Classification (07:00 - 08:15)
* **Cadre :** Classification supervisée multiclasse à 3 modalités (`NO`, `>30`, `<30`).
* **Expérimentation comparative en 3 temps :**
  1. **Logistic Regression (Standard) :** Modèle linéaire de référence (Baseline), poids uniformes.
  2. **Logistic Regression (Balanced) :** Intégration de `class_weight='balanced'` pour contrebalancer la rareté de la classe `<30` en surpondérant son coût d'erreur.
  3. **Random Forest Classifier (Balanced) :** Modèle d'ensemble non-linéaire capable de capturer des interactions complexes entre ordonnances et pathologies.
* **Critères d'évaluation :**
  * **Recall de `<30`** : Capacité à repérer les patients à haut risque (objectif prioritaire).
  * **F1-score de `<30`** et **Macro F1** : Qualité du compromis précision/rappel.
  * **Accuracy** : Suivie à titre indicatif mais non décisionnelle.

> 🎙️ **Ce que vous dites à l'oral (1m15s) :**  
> *"Notre démarche de classification s'articule autour de trois modèles intégrés dans un pipeline automatisé : Premièrement, une Régression Logistique standard qui sert de baseline. Deuxièmement, cette même Régression Logistique à laquelle nous appliquons l'option 'class_weight=balanced' : le modèle inflige une pénalité accrue lorsqu'il rate la classe rare '<30'. Troisièmement, un Random Forest pour évaluer si des arbres de décision non-linéaires apportent un gain. Notre boussole d'évaluation n'est pas l'accuracy globale mais le Recall et le F1-score sur la classe '<30 jours'."*

---

### Slide 8 : Principaux Résultats & Interprétation (08:15 - 09:45)
* **Tableau Comparatif des Performances :**

| Modèle | Accuracy Globale | Recall (<30) | F1-Score (<30) | Macro F1 |
| :--- | :---: | :---: | :---: | :---: |
| **Logistic Regression (Standard)** | **58,3 %** | 2,6 % ⚠️ | 0,049 | 0,387 |
| **Logistic Regression (Balanced)** | 50,1 % | **41,8 %** ⭐ | **0,271** ⭐ | **0,443** ⭐ |
| **Random Forest (Balanced)** | 57,3 % | 8,8 % | 0,141 | 0,438 |

* **Interprétation Clinique :**
  * **Le piège de la baseline :** La régression standard semble la meilleure en accuracy (58,3 %), mais elle est **aveugle sur la classe critique** (Recall de 2,6 %, 97 % des réadmissions ratées !).
  * **L'efficacité du rééquilibrage :** La régression logistique équilibrée multiplie le rappel par **16** (passant à 41,8 %) et maximise le Macro F1 (0,443).
  * **La limite du Random Forest :** Bien que puissant sur la classe `>30`, il peine à isoler la réadmission précoce (Recall à 8,8 %).

> 🎙️ **Ce que vous dites à l'oral (1m30s) :**  
> *"Regardez attentivement ces résultats. Si l'on ne regardait que l'accuracy, on choisirait la Régression Logistique standard à 58,3 %. Mais ce serait une erreur médicale majeure : avec un recall de 2,6 %, ce modèle est totalement aveugle sur les patients réadmis en urgence. Il prédit systématiquement les classes majoritaires.\nÀ l'inverse, regardez la deuxième ligne : avec l'équilibrage 'balanced', le rappel de la classe '<30' bondit à 41,8 %, soit une multiplication par 16 ! Le Macro F1 atteint son niveau maximal à 0,443. La baisse d'accuracy à 50 % est le prix rationnel à payer pour détecter plus de 4 patients à risque sur 10. Dans un hôpital, c'est ce modèle qui permettrait de déclencher des visites à domicile et d'éviter des réhospitalisations coûteuses."*

---

### Slide 9 : Enseignements, Limites & Perspectives (09:45 - 10:30)
* **Enseignements majeurs :**
  * La sémantique des données (`Not_measured`, regroupements ICD-9) est plus déterminante que la seule sophistication algorithmique.
  * L'alignement entre métrique ML et contrainte métier (sanction HRRP) est indispensable.
* **Limites identifiées :**
  * Données rétrospectives (1999–2008) sans prise en compte des nouvelles molécules antidiabétiques.
  * Absence de prise en compte de la trajectoire longitudinale des patients réadmis plusieurs fois.
* **Perspectives futures :**
  * Modélisation par Boosting (XGBoost, LightGBM) avec ajustement fin du seuil de décision (*Threshold Tuning*).
  * Analyse de survie (*Survival Analysis*) pour estimer la probabilité temporelle exacte de réadmission.

> 🎙️ **Ce que vous dites à l'oral (45s) :**  
> *"En conclusion, ce projet prouve que la préparation des données et le choix des métriques dictent la réussite du Machine Learning appliqué à la santé. Nos regroupements cliniques et la prise en compte du déséquilibre ont permis de transformer un dataset complexe en un système d'alerte exploitable. Pour aller plus loin, nous pourrions explorer des modèles de Gradient Boosting comme LightGBM avec réglage fin des seuils de décision, ou modéliser l'historique longitudinal des patients à admissions répétées."*

---

### Slide 10 : Clôture & Remerciements (10:30)
* **Titre :** Merci pour votre attention !
* **Sous-titre :** Questions & Réponses
* **Équipe DataForge :**
  * Ben Hamouda Firas • Nour Mzoughi • Baha Ben Kilani • Amina Sehimi • Sabri Bouallegi • moemen bennannou

> 🎙️ **Ce que vous dites à l'oral (15s) :**  
> *"Toute l'équipe DataForge vous remercie pour votre attention. Nous sommes à présent à votre disposition pour répondre à toutes vos questions."*
