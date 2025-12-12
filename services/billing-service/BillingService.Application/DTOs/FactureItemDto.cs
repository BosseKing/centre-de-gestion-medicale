namespace BillingService.Application.DTOs;

public class FactureItemDto
{
    public int Id { get; set; }
    public string TypeActe { get; set; } = string.Empty;
    public string ActeId { get; set; } = string.Empty;
    public decimal PrixUnitaire { get; set; }
    public int Quantite { get; set; }
    public decimal MontantTotal { get; set; }
    public string? Description { get; set; }
}
