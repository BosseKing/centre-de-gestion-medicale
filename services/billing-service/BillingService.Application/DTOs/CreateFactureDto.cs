namespace BillingService.Application.DTOs;

public class CreateFactureDto
{
    public string PatientId { get; set; } = string.Empty;
    public List<CreateFactureItemDto> Items { get; set; } = new();
}
