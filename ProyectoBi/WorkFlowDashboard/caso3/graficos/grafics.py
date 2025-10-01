import matplotlib.pyplot as plt

from caso3.analisis import analisis


total_ventas, ventas_region, top_productos = analisis()


plt.style.use('seaborn-v0_8')

# Ventas por región - Enhanced version
plt.figure(figsize=(10, 6))
ventas_region.plot(kind="bar", title="Ventas por Región",
                   color='skyblue', edgecolor='navy', alpha=0.7)
plt.xlabel("Región")
plt.ylabel("Ventas")
plt.grid(axis='y', alpha=0.3)
plt.tight_layout()
plt.savefig("ventas_region.png", dpi=300, bbox_inches='tight')
plt.close()

# Top productos - Enhanced version
plt.figure(figsize=(10, 6))
top_productos.plot(kind="barh", title="Top 5 Productos",
                   color='lightcoral', edgecolor='darkred', alpha=0.7)
plt.xlabel("Ventas")
plt.ylabel("Productos")
plt.grid(axis='x', alpha=0.3)
plt.tight_layout()
plt.savefig("top_productos.png", dpi=300, bbox_inches='tight')
plt.close()

