# Microservice de Facturation Hospitalière

## Description
Microservice de facturation hospitalière développé en .NET Core 9.0 suivant les principes de **Clean Architecture**. Il expose des API REST pour gérer la création, la consultation, la liste et le paiement des factures.

## Architecture

Le projet est organisé selon Clean Architecture avec 4 couches :

### 1. **BillingService.Domain** (Couche Domaine)
- **Entités** :
  - `Facture` : Représente une facture avec numéro unique (FAC-YYYYMMDD-XXX), patient, statut, total
  - `FactureItem` : Ligne de facture avec type d'acte, prix unitaire, quantité, montant total
- **Enums** :
  - `StatutFacture` : Payée / Impayée
  - `TypeActe` : Consultation, Opération, Intervention, Traitement
- **Interfaces** : `IFactureRepository`

### 2. **BillingService.Application** (Couche Application)
- **Services** : `FactureService` - Logique métier de gestion des factures
- **DTOs** : 
  - `CreateFactureDto` - Création de facture
  - `FactureDto` - Représentation complète de facture
  - `FactureItemDto` - Ligne de facture
  - `CreateFactureItemDto` - Création de ligne de facture

### 3. **BillingService.Infrastructure** (Couche Infrastructure)
- **DbContext** : `BillingDbContext` - Configuration Entity Framework Core
- **Repositories** : `FactureRepository` - Accès aux données
- **Base de données** : MySQL avec Pomelo.EntityFrameworkCore.MySql 9.0

### 4. **BillingService.API** (Couche Présentation)
- **Controllers** : `FacturesController` - Endpoints REST
- **Configuration** : Swagger, Dependency Injection, DbContext

## Modèle de Données

### Facture
| Champ | Type | Description |
|-------|------|-------------|
| Id | int | Identifiant unique |
| NumeroUnique | string | Format FAC-YYYYMMDD-XXX |
| PatientId | int | Identifiant du patient |
| DateCreation | DateTime | Date de création |
| Statut | StatutFacture | Payée / Impayée |
| Total | decimal | Montant total |
| DatePaiement | DateTime? | Date du paiement (nullable) |
| Items | Collection | Liste des lignes de facture |

### FactureItem
| Champ | Type | Description |
|-------|------|-------------|
| Id | int | Identifiant unique |
| FactureId | int | Référence à la facture |
| TypeActe | TypeActe | Type d'acte médical |
| ActeId | int | ID de l'acte dans son microservice |
| PrixUnitaire | decimal | Prix unitaire |
| Quantite | int | Quantité |
| MontantTotal | decimal | Montant total (calculé) |
| Description | string? | Description optionnelle |

## API REST

### Endpoints

#### 1. Créer une facture
```
POST /api/factures
Content-Type: application/json

{
  "patientId": 1,
  "items": [
    {
      "typeActe": 0,
      "acteId": 100,
      "prixUnitaire": 50.00,
      "quantite": 1,
      "description": "Consultation générale"
    }
  ]
}
```

#### 2. Récupérer une facture
```
GET /api/factures/{id}
```

#### 3. Lister toutes les factures
```
GET /api/factures
```

#### 4. Payer une facture
```
POST /api/factures/{id}/payer
```

## Configuration

### Base de données
Modifiez la chaîne de connexion dans `appsettings.json` :
```json
{
  "ConnectionStrings": {
    "DefaultConnection": "Server=localhost;Database=facturation_db;User=root;Password=votre_mot_de_passe;"
  }
}
```

## Installation et Démarrage

### Prérequis
- .NET 9.0 SDK
- MySQL Server 8.0 ou supérieur

### Étapes

1. **Restaurer les packages NuGet**
```bash
dotnet restore
```

2. **Créer la base de données**
```bash
dotnet ef database update --project BillingService.Infrastructure --startup-project BillingService.API
```

3. **Lancer l'application**
```bash
cd BillingService.API
dotnet run
```

4. **Accéder à Swagger**
```
https://localhost:5001/swagger
```

## Règles Métier

1. **Génération du numéro unique** : Le format FAC-YYYYMMDD-XXX est généré automatiquement avec un séquencement quotidien
2. **Calcul des totaux** : 
   - Le montant de chaque ligne = PrixUnitaire × Quantite
   - Le total de la facture = Somme de tous les montants des lignes
3. **Paiement** :
   - Une facture ne peut être payée qu'une seule fois
   - Le paiement est global (toute la facture)
   - La date de paiement est enregistrée automatiquement
   - Le statut passe à "Payée"

## Technologies Utilisées

- **.NET 9.0**
- **ASP.NET Core Web API**
- **Entity Framework Core 9.0**
- **MySQL 8.0+** avec **Pomelo.EntityFrameworkCore.MySql**
- **Swashbuckle (Swagger/OpenAPI)**
- **Clean Architecture Pattern**

## Structure du Projet

```
BillingService/
├── BillingService.Domain/
│   ├── Common/
│   │   └── BaseEntity.cs
│   ├── Entities/
│   │   ├── Facture.cs
│   │   └── FactureItem.cs
│   ├── Enums/
│   │   ├── StatutFacture.cs
│   │   └── TypeActe.cs
│   └── Interfaces/
│       └── IFactureRepository.cs
├── BillingService.Application/
│   ├── DTOs/
│   │   ├── CreateFactureDto.cs
│   │   ├── CreateFactureItemDto.cs
│   │   ├── FactureDto.cs
│   │   └── FactureItemDto.cs
│   └── Services/
│       ├── IFactureService.cs
│       └── FactureService.cs
├── BillingService.Infrastructure/
│   ├── Data/
│   │   └── BillingDbContext.cs
│   └── Repositories/
│       └── FactureRepository.cs
└── BillingService.API/
    ├── Controllers/
    │   └── FacturesController.cs
    ├── Program.cs
    └── appsettings.json
```

## Exemples de Requêtes

### Créer une facture avec plusieurs items
```json
POST /api/factures
{
  "patientId": 123,
  "items": [
    {
      "typeActe": 0,
      "acteId": 100,
      "prixUnitaire": 50.00,
      "quantite": 1,
      "description": "Consultation générale"
    },
    {
      "typeActe": 3,
      "acteId": 201,
      "prixUnitaire": 25.00,
      "quantite": 2,
      "description": "Médicaments"
    }
  ]
}
```

### Réponse
```json
{
  "id": 1,
  "numeroUnique": "FAC-20251208-001",
  "patientId": 123,
  "dateCreation": "2025-12-08T10:30:00Z",
  "statut": 0,
  "total": 100.00,
  "datePaiement": null,
  "items": [
    {
      "id": 1,
      "typeActe": 0,
      "acteId": 100,
      "prixUnitaire": 50.00,
      "quantite": 1,
      "montantTotal": 50.00,
      "description": "Consultation générale"
    },
    {
      "id": 2,
      "typeActe": 3,
      "acteId": 201,
      "prixUnitaire": 25.00,
      "quantite": 2,
      "montantTotal": 50.00,
      "description": "Médicaments"
    }
  ]
}
```

## License
Ce projet est développé dans le cadre d'une architecture SOA pour un système hospitalier.
