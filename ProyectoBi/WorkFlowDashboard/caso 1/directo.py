import mysql.connector
import pandas as pd
import numpy as np




config={
    'host':'localhost',
    'user':'root',
    'password':'root',
    'port':'3306',
    'database':'maxsoftdirecto'

}
table='registros'
con= mysql.connector.connect(**config)#kargs

cursor=con.cursor()

cursor.execute(f'SELECT * FROM {table}')

nombres=[i[0] for i in cursor.description]
print(nombres)
datos= cursor.fetchall()
df=pd.DataFrame(datos, columns=nombres)

print(df.head())

df.to_excel('Ganancias.xlsx',sheet_name='registros', index=False)