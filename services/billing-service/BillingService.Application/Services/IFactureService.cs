using BillingService.Application.DTOs;

namespace BillingService.Application.Services;

public interface IFactureService
{
    Task<FactureDto> CreateFactureAsync(CreateFactureDto createDto);
    Task<FactureDto?> GetFactureByIdAsync(int id);
    Task<IEnumerable<FactureDto>> GetAllFacturesAsync();
    Task<FactureDto> PayerFactureAsync(int id);
}
