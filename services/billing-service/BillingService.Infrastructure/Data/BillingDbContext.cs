using Microsoft.EntityFrameworkCore;
using BillingService.Domain.Entities;

namespace BillingService.Infrastructure.Data;

public class BillingDbContext : DbContext
{
    public BillingDbContext(DbContextOptions<BillingDbContext> options) : base(options)
    {
    }

    public DbSet<Facture> Factures { get; set; }
    public DbSet<FactureItem> FactureItems { get; set; }

    protected override void OnModelCreating(ModelBuilder modelBuilder)
    {
        base.OnModelCreating(modelBuilder);

        // Configuration de Facture
        modelBuilder.Entity<Facture>(entity =>
        {
            entity.HasKey(e => e.Id);
            entity.Property(e => e.NumeroUnique).IsRequired().HasMaxLength(50);
            entity.Property(e => e.PatientId).IsRequired().HasMaxLength(50);
            entity.Property(e => e.DateCreation).IsRequired();
            entity.Property(e => e.Statut).IsRequired();
            entity.Property(e => e.Total).HasColumnType("decimal(18,2)");
            entity.Property(e => e.DatePaiement).IsRequired(false);

            entity.HasMany(e => e.Items)
                .WithOne(i => i.Facture)
                .HasForeignKey(i => i.FactureId)
                .OnDelete(DeleteBehavior.Cascade);

            entity.HasIndex(e => e.NumeroUnique).IsUnique();
        });

        // Configuration de FactureItem
        modelBuilder.Entity<FactureItem>(entity =>
        {
            entity.HasKey(e => e.Id);
            entity.Property(e => e.TypeActe).IsRequired().HasMaxLength(100);
            entity.Property(e => e.ActeId).IsRequired().HasMaxLength(100);
            entity.Property(e => e.PrixUnitaire).HasColumnType("decimal(18,2)");
            entity.Property(e => e.Quantite).IsRequired();
            entity.Property(e => e.MontantTotal).HasColumnType("decimal(18,2)");
            entity.Property(e => e.Description).HasMaxLength(500);
        });
    }
}
