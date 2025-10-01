"""
pip install pandas
pip install mysql-connector-python

"""

import mysql.connector
import pandas as pd


def connection():
    # Conexión a MySQL
    conn = mysql.connector.connect(
        host="localhost",
        user="root",
        password="root",
        database="maxsoftdirecto"
    )

    # Consulta ventas de la última semana
    query = """
    SELECT fecha, region, producto, cantidad, precio,
           cantidad*precio AS total
    FROM ventas
    WHERE fecha >= DATE_SUB(CURDATE(), INTERVAL 7 DAY);
    """
    cursor = conn.cursor()
    cursor.execute(query)
    rows = cursor.fetchall()

    columns = [desc[0] for desc in cursor.description]

    df = pd.DataFrame(rows, columns=columns)
    cursor.close()

    conn.close()
    return df

