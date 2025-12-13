
using BillingService.Application.DTOs;
using BillingService.Domain.Entities;
using BillingService.Domain.Interfaces;
using QuestPDF.Fluent;
using QuestPDF.Helpers;
using QuestPDF.Infrastructure;
using QRCoder;

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

    public async Task<FactureDto> UpdateFactureAsync(int id, UpdateFactureDto updateDto)
    {
        var facture = await _factureRepository.GetByIdAsync(id);
        if (facture == null)
            throw new KeyNotFoundException($"Facture avec l'ID {id} introuvable.");
        if (facture.Statut == Domain.Enums.StatutFacture.Payee || facture.Statut == Domain.Enums.StatutFacture.Annulee)
            throw new InvalidOperationException("Impossible de modifier une facture payée ou annulée.");

        facture.PatientId = updateDto.PatientId;
        // Synchroniser les items (remplacement complet)
        facture.Items.Clear();
        foreach (var itemDto in updateDto.Items)
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
        if (updateDto.Statut.HasValue)
            facture.Statut = (Domain.Enums.StatutFacture)updateDto.Statut.Value;
        await _factureRepository.UpdateAsync(facture);
        return MapToDto(facture);
    }

    public async Task AnnulerFactureAsync(int id)
    {
        var facture = await _factureRepository.GetByIdAsync(id);
        if (facture == null)
            throw new KeyNotFoundException($"Facture avec l'ID {id} introuvable.");
        if (facture.Statut == Domain.Enums.StatutFacture.Payee)
            throw new InvalidOperationException("Impossible d'annuler une facture déjà payée.");
        facture.Statut = Domain.Enums.StatutFacture.Annulee;
        await _factureRepository.UpdateAsync(facture);
    }

    public async Task<IEnumerable<FactureDto>> GetFacturesByPatientAsync(string patientId)
    {
        var all = await _factureRepository.GetAllAsync();
        return all.Where(f => f.PatientId == patientId).Select(MapToDto);
    }

    public async Task<IEnumerable<FactureDto>> GetFacturesNonPayeesAsync()
    {
        var all = await _factureRepository.GetAllAsync();
        return all.Where(f => f.Statut == Domain.Enums.StatutFacture.Impayee).Select(MapToDto);
    }

    public async Task<IEnumerable<FactureDto>> GetFacturesPayeesAsync()
    {
        var all = await _factureRepository.GetAllAsync();
        return all.Where(f => f.Statut == Domain.Enums.StatutFacture.Payee).Select(MapToDto);
    }

    public async Task<FactureDto> AjouterItemFactureAsync(int factureId, FactureItemDto itemDto)
    {
        var facture = await _factureRepository.GetByIdAsync(factureId);
        if (facture == null)
            throw new KeyNotFoundException($"Facture avec l'ID {factureId} introuvable.");
        if (facture.Statut != Domain.Enums.StatutFacture.Impayee)
            throw new InvalidOperationException("Impossible d'ajouter un item à une facture payée ou annulée.");
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
        facture.CalculerTotal();
        await _factureRepository.UpdateAsync(facture);
        return MapToDto(facture);
    }

    public async Task<FactureDto> SupprimerItemFactureAsync(int factureId, int itemId)
    {
        var facture = await _factureRepository.GetByIdAsync(factureId);
        if (facture == null)
            throw new KeyNotFoundException($"Facture avec l'ID {factureId} introuvable.");
        if (facture.Statut != Domain.Enums.StatutFacture.Impayee)
            throw new InvalidOperationException("Impossible de supprimer un item d'une facture payée ou annulée.");
        var item = facture.Items.FirstOrDefault(i => i.Id == itemId);
        if (item == null)
            throw new KeyNotFoundException($"Item avec l'ID {itemId} introuvable dans la facture.");
        facture.Items.Remove(item);
        facture.CalculerTotal();
        await _factureRepository.UpdateAsync(facture);
        return MapToDto(facture);
    }


    public async Task<byte[]> GenererFacturePdfAsync(int factureId)
    {
        // Configure la licence QuestPDF (obligatoire depuis 2023)
        QuestPDF.Settings.License = QuestPDF.Infrastructure.LicenseType.Community;
        var facture = await _factureRepository.GetByIdAsync(factureId);
        if (facture == null) return Array.Empty<byte>();

        using var stream = new MemoryStream();
        QuestPDF.Infrastructure.IContainer CellStyle(QuestPDF.Infrastructure.IContainer container)
            => container.PaddingVertical(2).BorderBottom(1).BorderColor("#DDD");

        var document = Document.Create(container =>
        {
            container.Page(page =>
            {
                page.Margin(30);
                page.Header().Text($"Facture N° {facture.NumeroUnique}").FontSize(20).Bold();
                page.Content().Column(col =>
                {
                    col.Item().Text($"Patient : {facture.PatientId}");
                    col.Item().Text($"Date : {facture.DateCreation:yyyy-MM-dd HH:mm}");
                    col.Item().Text($"Statut : {facture.Statut}");
                    col.Item().Text($"Total : {facture.Total} €").Bold();
                    col.Item().Text("");
                    col.Item().Table(table =>
                    {
                        table.ColumnsDefinition(columns =>
                        {
                            columns.ConstantColumn(30); // #
                            columns.RelativeColumn(); // TypeActe
                            columns.RelativeColumn(); // ActeId
                            columns.ConstantColumn(60); // Prix
                            columns.ConstantColumn(40); // Qté
                            columns.ConstantColumn(60); // Montant
                        });
                        table.Header(header =>
                        {
                            header.Cell().Element(CellStyle).Text("#");
                            header.Cell().Element(CellStyle).Text("Type");
                            header.Cell().Element(CellStyle).Text("ActeId");
                            header.Cell().Element(CellStyle).Text("Prix");
                            header.Cell().Element(CellStyle).Text("Qté");
                            header.Cell().Element(CellStyle).Text("Montant");
                        });
                        int idx = 1;
                        foreach (var item in facture.Items)
                        {
                            table.Cell().Element(CellStyle).Text(idx.ToString()); idx++;
                            table.Cell().Element(CellStyle).Text(item.TypeActe);
                            table.Cell().Element(CellStyle).Text(item.ActeId);
                            table.Cell().Element(CellStyle).Text($"{item.PrixUnitaire:0.00}");
                            table.Cell().Element(CellStyle).Text(item.Quantite.ToString());
                            table.Cell().Element(CellStyle).Text($"{item.MontantTotal:0.00}");
                        }
                    });
                });
                page.Footer().AlignCenter().Text($"Généré le {DateTime.Now:yyyy-MM-dd HH:mm}").FontSize(10);
            });
        });
        document.GeneratePdf(stream);
        return stream.ToArray();
    }

    public async Task<byte[]> GenererFactureQrCodeAsync(int factureId)
    {
        var facture = await _factureRepository.GetByIdAsync(factureId);
        if (facture == null) return Array.Empty<byte>();
        var qrText = $"Facture: {facture.NumeroUnique}\nPatient: {facture.PatientId}\nTotal: {facture.Total} €\nStatut: {facture.Statut}";
        using var qrGen = new QRCoder.QRCodeGenerator();
        var qrData = qrGen.CreateQrCode(qrText, QRCoder.QRCodeGenerator.ECCLevel.Q);
        using var qrCode = new QRCoder.PngByteQRCode(qrData);
        return qrCode.GetGraphic(10);
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
