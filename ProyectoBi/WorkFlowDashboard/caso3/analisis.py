from caso3.db_connect import connection

df = connection()

def analisis():
    # KPI total de ventas
    total_ventas = df["total"].sum()

    # Ventas por región
    ventas_region = df.groupby("region")["total"].sum()

    # Top 5 productos
    top_productos = df.groupby("producto")["total"].sum().nlargest(5)
    return total_ventas,ventas_region,top_productos

