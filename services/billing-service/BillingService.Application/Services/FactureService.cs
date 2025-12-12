using BillingService.Application.DTOs;
using BillingService.Domain.Entities;
using BillingService.Domain.Interfaces;

namespace BillingService.Application.Services;

public class FactureService : IFactureService
{
    private readonly IFactureRepository _factureRepository;

    public FactureService(IFactureRepository factureRepository)
    {
        _factureRepository = factureRepository;
    }

    public async Task<FactureDto> CreateFactureAsync(CreateFactureDto createDto)
    {
        var facture = new Facture
        {
            PatientId = createDto.PatientId
        };

        foreach (var itemDto in createDto.Items)
        {
            var item = new FactureItem
            {
                TypeActe = itemDto.TypeActe,
                ActeId = itemDto.ActeId,
                PrixUnitaire = itemDto.PrixUnitaire,
                Quantite = itemDto.Quantite,
                Description = itemDto.Description
            };
            item.CalculerMontantTotal();
            facture.Items.Add(item);
        }

        facture.CalculerTotal();
        
        // Générer le numéro unique
        var sequence = await _factureRepository.GetNextSequenceForTodayAsync();
        facture.GenererNumeroUnique(sequence);

        var createdFacture = await _factureRepository.CreateAsync(facture);
        return MapToDto(createdFacture);
    }

    public async Task<FactureDto?> GetFactureByIdAsync(int id)
    {
        var facture = await _factureRepository.GetByIdAsync(id);
        return facture == null ? null : MapToDto(facture);
    }

    public async Task<IEnumerable<FactureDto>> GetAllFacturesAsync()
    {
        var factures = await _factureRepository.GetAllAsync();
        return factures.Select(MapToDto);
    }

    public async Task<FactureDto> PayerFactureAsync(int id)
    {
        var facture = await _factureRepository.GetByIdAsync(id);
        
        if (facture == null)
        {
            throw new KeyNotFoundException($"Facture avec l'ID {id} introuvable.");
        }

        facture.Payer();
        await _factureRepository.UpdateAsync(facture);
        
        return MapToDto(facture);
    }

    private static FactureDto MapToDto(Facture facture)
    {
        return new FactureDto
        {
            Id = facture.Id,
            NumeroUnique = facture.NumeroUnique,
            PatientId = facture.PatientId,
            DateCreation = facture.DateCreation,
            Statut = facture.Statut,
            Total = facture.Total,
            DatePaiement = facture.DatePaiement,
            Items = facture.Items.Select(item => new FactureItemDto
            {
                Id = item.Id,
                TypeActe = item.TypeActe,
                ActeId = item.ActeId,
                PrixUnitaire = item.PrixUnitaire,
                Quantite = item.Quantite,
                MontantTotal = item.MontantTotal,
                Description = item.Description
            }).ToList()
        };
    }
}
