import pandas as pd
import mysql.connector
from mysql.connector import Error

# ========================
# CONFIGURACIÓN DE CONEXIÓN
# ========================
DB_NAME = "maxsoftdirecto"
TABLE_NAME = "registros"

config = {
    "host": "localhost",      # Cambia si tu servidor no es local
    "user": "root",           # Tu usuario MySQL
    "password": "root" # Tu contraseña
}

# ========================
# LEER EL EXCEL
# ========================
excel_file = "MaxSoft Company.xlsx"  # Ruta de tu archivo
df = pd.read_excel(excel_file)

# ========================
# CREAR DB Y TABLA
# ========================
def create_database_and_table():
    try:
        connection = mysql.connector.connect(**config)
        cursor = connection.cursor()

        # Crear base de datos si no existe
        cursor.execute(f"CREATE DATABASE IF NOT EXISTS {DB_NAME}")
        cursor.execute(f"USE {DB_NAME}")

        # Crear tabla si no existe
        cursor.execute(f"""
            CREATE TABLE IF NOT EXISTS {TABLE_NAME} (
                id INT AUTO_INCREMENT PRIMARY KEY,
                Fecha DATE,
                Pais VARCHAR(100),
                Cliente VARCHAR(255),
                TipoNegocio VARCHAR(100),
                Gastos DECIMAL(15,2),
                Ingresos DECIMAL(15,2)
            )
        """)
        connection.commit()
        print("✅ Base de datos y tabla listas.")

    except Error as e:
        print(f"❌ Error creando DB o tabla: {e}")
    finally:
        if connection.is_connected():
            cursor.close()
            connection.close()

# ========================
# INSERTAR DATOS
# ========================
def insert_data():
    try:
        connection = mysql.connector.connect(**config, database=DB_NAME)
        cursor = connection.cursor()

        insert_query = f"""
            INSERT INTO {TABLE_NAME} 
            (Fecha, Pais, Cliente, TipoNegocio, Gastos, Ingresos)
            VALUES (%s, %s, %s, %s, %s, %s)
        """

        for _, row in df.iterrows():
            values = (
                row["Fecha"],
                row["Pais"],
                row["Cliente"],
                row["Tipo de Negocio"],
                float(row["Gastos"]),
                float(row["Ingresos"])
            )
            cursor.execute(insert_query, values)

        connection.commit()
        print(f"✅ Se insertaron {cursor.rowcount} registros.")

    except Error as e:
        print(f"❌ Error insertando datos: {e}")
    finally:
        if connection.is_connected():
            cursor.close()
            connection.close()

# ========================
# EJECUCIÓN
# ========================
if __name__ == "__main__":
    create_database_and_table()
    insert_data()
