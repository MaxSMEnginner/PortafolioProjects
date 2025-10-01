import matplotlib.pyplot as plt

from caso3directo.data_analytics import analytics

total_ventas, ventas_region, top_productos = analytics()


plt.style.use('seaborn-v0_8')

plt.figure(figsize=(10,6))
ventas_region.plot(kind='bar', title='Ventas por Region',
                   color='skyblue', edgecolor='navy', alpha=0.7)
plt.xlabel(xlabel='Region')
plt.ylabel(ylabel='Ventas')
plt.tight_layout()
plt.savefig('ventas_region.png', dpi=300, bbox_inches='tight')
plt.close()

plt.figure(figsize=(10,6))
top_productos.plot(kind='barh', title='Productos mas Vendidos',
                   color='lightcoral', edgecolor='darkred', alpha=0.7)
plt.xlabel(xlabel='Producto')
plt.ylabel(ylabel='Ventas')
plt.tight_layout()
plt.savefig('top_productos.png', dpi=300, bbox_inches='tight')
plt.close()