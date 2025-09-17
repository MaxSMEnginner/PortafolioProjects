import pandas as pd

df= pd.read_csv('data.csv', encoding='utf-8-sig')
print(type(df['Fecha'][0]),df['Fecha'][0])
df['Fecha'] = pd.to_datetime(df['Fecha'])
print(type(df['Fecha'][0]),df['Fecha'][0])
df['Ano']=df['Fecha'].dt.year
df['Beneficio']=((df['Ingresos']) - (df['Gastos']))
df['Margen']=((df['Ingresos']) - (df['Gastos'])) / (df['Ingresos'])

# df=df[df['Fecha'].dt.year == 2025 ]
df4=df[['Ano','Pais','Ingresos','Gastos','Beneficio','Margen']]
df4=df4[(df4['Ano'] == 2025) & (df4['Pais'] == 'Paraguay')]


df2={
    'Total Ingreso': [sum(df4['Ingresos'])],
    'Total Gastos': [sum(df4['Gastos'])],
    'Beneficio': [sum(df4['Ingresos']) - sum(df4['Gastos'])],
    'Margen': [(sum(df4['Ingresos']) - sum(df4['Gastos'])) / sum(df4['Ingresos'])],

}
dfr=pd.DataFrame(df2)
print(dfr)
print(df4)
# df.to_excel('Ganancias3.xlsx',sheet_name='registros', index=False)