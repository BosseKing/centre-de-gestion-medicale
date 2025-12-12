using BillingService.Domain.Common;

namespace BillingService.Domain.Entities;

public class FactureItem : BaseEntity
{
    public int FactureId { get; set; }
    public Facture Facture { get; set; } = null!;
    
    public string TypeActe { get; set; } = string.Empty;
    public string ActeId { get; set; } = string.Empty;
    public decimal PrixUnitaire { get; set; }
    public int Quantite { get; set; }
    public decimal MontantTotal { get; set; }
    public string? Description { get; set; }

    public void CalculerMontantTotal()
    {
        MontantTotal = PrixUnitaire * Quantite;
    }
}
