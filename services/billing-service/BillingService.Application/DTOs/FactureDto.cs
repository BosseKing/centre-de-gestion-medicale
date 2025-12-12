using BillingService.Domain.Enums;

namespace BillingService.Application.DTOs;

public class FactureDto
{
    public int Id { get; set; }
    public string NumeroUnique { get; set; } = string.Empty;
    public string PatientId { get; set; } = string.Empty;
    public DateTime DateCreation { get; set; }
    public StatutFacture Statut { get; set; }
    public decimal Total { get; set; }
    public DateTime? DatePaiement { get; set; }
    public List<FactureItemDto> Items { get; set; } = new();
}
