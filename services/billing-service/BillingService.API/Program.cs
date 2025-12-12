using Microsoft.EntityFrameworkCore;
using BillingService.Application.Services;
using BillingService.Domain.Interfaces;
using BillingService.Infrastructure.Data;
using BillingService.Infrastructure.Repositories;

var builder = WebApplication.CreateBuilder(args);

// Add services to the container
builder.Services.AddControllers();
builder.Services.AddEndpointsApiExplorer();
builder.Services.AddSwaggerGen(c =>
{
    c.SwaggerDoc("v1", new() { 
        Title = "Billing Service API", 
        Version = "v1",
        Description = "API de gestion de facturation hospitalière"
    });
});

// Database Configuration
var connectionString = builder.Configuration.GetConnectionString("DefaultConnection") 
    ?? "Server=localhost;Database=BillingServiceDb;User=root;Password=;";
var serverVersion = new MySqlServerVersion(new Version(8, 0, 21));

builder.Services.AddDbContext<BillingDbContext>(options =>
    options.UseMySql(connectionString, serverVersion)
);

// Dependency Injection
builder.Services.AddScoped<IFactureRepository, FactureRepository>();
builder.Services.AddScoped<IFactureService, FactureService>();

var app = builder.Build();

// Configure the HTTP request pipeline
if (app.Environment.IsDevelopment())
{
    app.UseSwagger();
    app.UseSwaggerUI();
}

// app.UseHttpsRedirection(); // Désactivé pour le développement
app.UseAuthorization();
app.MapControllers();

app.Run();
