using BillingService.Domain.Common;
using BillingService.Domain.Enums;

namespace BillingService.Domain.Entities;

public class Facture : BaseEntity
{
    public string NumeroUnique { get; set; } = string.Empty;
    public string PatientId { get; set; } = string.Empty;
    public DateTime DateCreation { get; set; }
    public StatutFacture Statut { get; set; }
    public decimal Total { get; set; }
    public DateTime? DatePaiement { get; set; }
    
    public ICollection<FactureItem> Items { get; set; } = new List<FactureItem>();

    public Facture()
    {
        DateCreation = DateTime.UtcNow;
        Statut = StatutFacture.Impayee;
    }

    public void CalculerTotal()
    {
        Total = Items.Sum(item => item.MontantTotal);
    }

    public void Payer()
    {
        if (Statut == StatutFacture.Payee)
        {
            throw new InvalidOperationException("La facture est déjà payée.");
        }
        
        Statut = StatutFacture.Payee;
        DatePaiement = DateTime.UtcNow;
    }

    public void GenererNumeroUnique(int sequence)
    {
        var date = DateCreation.ToString("yyyyMMdd");
        NumeroUnique = $"FAC-{date}-{sequence:D3}";
    }
}
