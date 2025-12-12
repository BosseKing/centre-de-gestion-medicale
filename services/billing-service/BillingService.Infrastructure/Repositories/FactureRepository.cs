using Microsoft.EntityFrameworkCore;
using BillingService.Domain.Entities;
using BillingService.Domain.Interfaces;
using BillingService.Infrastructure.Data;

namespace BillingService.Infrastructure.Repositories;

public class FactureRepository : IFactureRepository
{
    private readonly BillingDbContext _context;

    public FactureRepository(BillingDbContext context)
    {
        _context = context;
    }

    public async Task<Facture?> GetByIdAsync(int id)
    {
        return await _context.Factures
            .Include(f => f.Items)
            .FirstOrDefaultAsync(f => f.Id == id);
    }

    public async Task<IEnumerable<Facture>> GetAllAsync()
    {
        return await _context.Factures
            .Include(f => f.Items)
            .ToListAsync();
    }

    public async Task<Facture> CreateAsync(Facture facture)
    {
        _context.Factures.Add(facture);
        await _context.SaveChangesAsync();
        return facture;
    }

    public async Task UpdateAsync(Facture facture)
    {
        _context.Factures.Update(facture);
        await _context.SaveChangesAsync();
    }

    public async Task<int> GetNextSequenceForTodayAsync()
    {
        var today = DateTime.UtcNow.Date;
        var tomorrow = today.AddDays(1);

        var count = await _context.Factures
            .Where(f => f.DateCreation >= today && f.DateCreation < tomorrow)
            .CountAsync();

        return count + 1;
    }
}
