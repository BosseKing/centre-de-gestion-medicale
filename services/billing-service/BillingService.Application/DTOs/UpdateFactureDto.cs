namespace BillingService.Application.DTOs;

public class UpdateFactureDto
{
    public string PatientId { get; set; } = string.Empty;
    public List<FactureItemDto> Items { get; set; } = new();
    public int? Statut { get; set; } // 0: Impayée, 1: Payée, 2: Annulée
}
