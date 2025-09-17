import pandas as pd
import mysql.connector

DB_NAME = "maxsoftdirecto"
TABLE_NAME = "registros"

config = {
    "host": "localhost",
    "user": "root",
    "password": "root"
}

excel_file = "MaxSoft Company.xlsx"
df = pd.read_excel(excel_file)


connection = mysql.connector.connect(**config, database=DB_NAME)
cursor = connection.cursor()

cursor.execute(f"SELECT * FROM {TABLE_NAME}")

nombres = [i[0] for i in cursor.description]

datos=cursor.fetchall()

df=pd.DataFrame(datos, columns=nombres)

