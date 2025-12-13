using Microsoft.AspNetCore.Mvc;
using BillingService.Application.DTOs;
using BillingService.Application.Services;

namespace BillingService.API.Controllers;

[ApiController]
[Route("api/[controller]")]
public class FacturesController : ControllerBase
{
    private readonly IFactureService _factureService;

    public FacturesController(IFactureService factureService)
    {
        _factureService = factureService;
    }

    /// <summary>
    /// Créer une nouvelle facture
    /// </summary>
    [HttpPost]
    public async Task<ActionResult<FactureDto>> CreateFacture([FromBody] CreateFactureDto createDto)
    {
        try
        {
            var facture = await _factureService.CreateFactureAsync(createDto);
            return CreatedAtAction(nameof(GetFacture), new { id = facture.Id }, facture);
        }
        catch (Exception ex)
        {
            return BadRequest(new { message = ex.Message });
        }
    }

    /// <summary>
    /// Récupérer une facture par son ID
    /// </summary>
    [HttpGet("{id}")]
    public async Task<ActionResult<FactureDto>> GetFacture(int id)
    {
        var facture = await _factureService.GetFactureByIdAsync(id);
        
        if (facture == null)
        {
            return NotFound(new { message = $"Facture avec l'ID {id} introuvable." });
        }

        return Ok(facture);
    }

    /// <summary>
    /// Lister toutes les factures
    /// </summary>
    [HttpGet]
    public async Task<ActionResult<IEnumerable<FactureDto>>> GetAllFactures()
    {
        var factures = await _factureService.GetAllFacturesAsync();
        return Ok(factures);
    }

    /// <summary>
    /// Payer une facture
    /// </summary>
    [HttpPost("{id}/payer")]
    public async Task<ActionResult<FactureDto>> PayerFacture(int id)
    {
        try
        {
            var facture = await _factureService.PayerFactureAsync(id);
            return Ok(facture);
        }
        catch (KeyNotFoundException ex)
        {
            return NotFound(new { message = ex.Message });
        }
        catch (InvalidOperationException ex)
        {
            return BadRequest(new { message = ex.Message });
        }
        catch (Exception ex)
        {
            return StatusCode(500, new { message = ex.Message });
        }
    }
    /// <summary>
    /// Met à jour une facture existante
    /// </summary>
    [HttpPut("{id}")]
    [ProducesResponseType(typeof(FactureDto), 200)]
    [ProducesResponseType(400)]
    [ProducesResponseType(404)]
    public async Task<ActionResult<FactureDto>> UpdateFacture(int id, [FromBody] UpdateFactureDto updateDto)
    {
        try
        {
            var facture = await _factureService.UpdateFactureAsync(id, updateDto);
            return Ok(facture);
        }
        catch (KeyNotFoundException ex)
        {
            return NotFound(new { message = ex.Message });
        }
        catch (Exception ex)
        {
            return BadRequest(new { message = ex.Message });
        }
    }

    /// <summary>
    /// Annule une facture avant paiement
    /// </summary>
    [HttpPost("{id}/annuler")]
    [ProducesResponseType(204)]
    [ProducesResponseType(400)]
    [ProducesResponseType(404)]
    public async Task<IActionResult> AnnulerFacture(int id)
    {
        try
        {
            await _factureService.AnnulerFactureAsync(id);
            return NoContent();
        }
        catch (KeyNotFoundException ex)
        {
            return NotFound(new { message = ex.Message });
        }
        catch (Exception ex)
        {
            return BadRequest(new { message = ex.Message });
        }
    }

    /// <summary>
    /// Lister toutes les factures d'un patient
    /// </summary>
    [HttpGet("patient/{patientId}")]
    [ProducesResponseType(typeof(IEnumerable<FactureDto>), 200)]
    public async Task<ActionResult<IEnumerable<FactureDto>>> GetFacturesByPatient(string patientId)
    {
        var factures = await _factureService.GetFacturesByPatientAsync(patientId);
        return Ok(factures);
    }

    /// <summary>
    /// Lister toutes les factures impayées
    /// </summary>
    [HttpGet("impayees")]
    [ProducesResponseType(typeof(IEnumerable<FactureDto>), 200)]
    public async Task<ActionResult<IEnumerable<FactureDto>>> GetFacturesNonPayees()
    {
        var factures = await _factureService.GetFacturesNonPayeesAsync();
        return Ok(factures);
    }

    /// <summary>
    /// Lister toutes les factures payées
    /// </summary>
    [HttpGet("payees")]
    [ProducesResponseType(typeof(IEnumerable<FactureDto>), 200)]
    public async Task<ActionResult<IEnumerable<FactureDto>>> GetFacturesPayees()
    {
        var factures = await _factureService.GetFacturesPayeesAsync();
        return Ok(factures);
    }

    /// <summary>
    /// Ajouter un item à une facture
    /// </summary>
    [HttpPost("{factureId}/items")]
    [ProducesResponseType(typeof(FactureDto), 200)]
    [ProducesResponseType(400)]
    [ProducesResponseType(404)]
    public async Task<ActionResult<FactureDto>> AjouterItemFacture(int factureId, [FromBody] FactureItemDto itemDto)
    {
        try
        {
            var facture = await _factureService.AjouterItemFactureAsync(factureId, itemDto);
            return Ok(facture);
        }
        catch (KeyNotFoundException ex)
        {
            return NotFound(new { message = ex.Message });
        }
        catch (Exception ex)
        {
            return BadRequest(new { message = ex.Message });
        }
    }

    /// <summary>
    /// Supprimer un item d'une facture
    /// </summary>
    [HttpDelete("{factureId}/items/{itemId}")]
    [ProducesResponseType(typeof(FactureDto), 200)]
    [ProducesResponseType(400)]
    [ProducesResponseType(404)]
    public async Task<ActionResult<FactureDto>> SupprimerItemFacture(int factureId, int itemId)
    {
        try
        {
            var facture = await _factureService.SupprimerItemFactureAsync(factureId, itemId);
            return Ok(facture);
        }
        catch (KeyNotFoundException ex)
        {
            return NotFound(new { message = ex.Message });
        }
        catch (Exception ex)
        {
            return BadRequest(new { message = ex.Message });
        }
    }

    /// <summary>
    /// Générer le PDF d'une facture
    /// </summary>
    [HttpGet("{id}/pdf")]
    [ProducesResponseType(typeof(FileContentResult), 200)]
    [ProducesResponseType(404)]
    public async Task<IActionResult> GenererFacturePdf(int id)
    {
        try
        {
            var pdfBytes = await _factureService.GenererFacturePdfAsync(id);
            if (pdfBytes == null || pdfBytes.Length == 0)
                return NotFound();
            return File(pdfBytes, "application/pdf", $"facture_{id}.pdf");
        }
        catch (Exception ex)
        {
            return BadRequest(new { message = ex.Message });
        }
    }

    /// <summary>
    /// Générer un QR code pour la facture
    /// </summary>
    [HttpGet("{id}/qrcode")]
    [ProducesResponseType(typeof(FileContentResult), 200)]
    [ProducesResponseType(404)]
    public async Task<IActionResult> GenererFactureQrCode(int id)
    {
        try
        {
            var qrBytes = await _factureService.GenererFactureQrCodeAsync(id);
            if (qrBytes == null || qrBytes.Length == 0)
                return NotFound();
            return File(qrBytes, "image/png", $"facture_{id}_qrcode.png");
        }
        catch (Exception ex)
        {
            return BadRequest(new { message = ex.Message });
        }
    }
}
