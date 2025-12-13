using BillingService.Application.DTOs;

namespace BillingService.Application.Services;

public interface IFactureService
{
    Task<FactureDto> CreateFactureAsync(CreateFactureDto createDto);
    Task<FactureDto?> GetFactureByIdAsync(int id);
    Task<IEnumerable<FactureDto>> GetAllFacturesAsync();
    Task<FactureDto> PayerFactureAsync(int id);

    Task<FactureDto> UpdateFactureAsync(int id, UpdateFactureDto updateDto);
    Task AnnulerFactureAsync(int id);
    Task<IEnumerable<FactureDto>> GetFacturesByPatientAsync(string patientId);
    Task<IEnumerable<FactureDto>> GetFacturesNonPayeesAsync();
    Task<IEnumerable<FactureDto>> GetFacturesPayeesAsync();
    Task<FactureDto> AjouterItemFactureAsync(int factureId, FactureItemDto itemDto);
    Task<FactureDto> SupprimerItemFactureAsync(int factureId, int itemId);
    Task<byte[]> GenererFacturePdfAsync(int factureId);
    Task<byte[]> GenererFactureQrCodeAsync(int factureId);
}
