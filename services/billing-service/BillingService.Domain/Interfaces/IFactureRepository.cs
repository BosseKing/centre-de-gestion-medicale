using BillingService.Domain.Entities;

namespace BillingService.Domain.Interfaces;

public interface IFactureRepository
{
    Task<Facture?> GetByIdAsync(int id);
    Task<IEnumerable<Facture>> GetAllAsync();
    Task<Facture> CreateAsync(Facture facture);
    Task UpdateAsync(Facture facture);
    Task<int> GetNextSequenceForTodayAsync();
}
