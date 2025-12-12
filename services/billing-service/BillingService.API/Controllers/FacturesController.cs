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
}
