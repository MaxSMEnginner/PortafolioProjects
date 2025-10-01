from caso3directo.db_connection import dataframe

def analytics():
    df= dataframe()
    total_ventas= df['total'].sum()

    ventas_region= df.groupby('region')['total'].sum()
    top_productos=df.groupby('producto')['total'].sum().nlargest(5)


    return total_ventas, ventas_region, top_productos

