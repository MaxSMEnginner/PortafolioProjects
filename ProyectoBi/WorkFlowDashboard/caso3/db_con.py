
"""
pip install sqlalchemy
pip install pymysql

"""
from sqlalchemy import create_engine
import pandas as pd
def connection():
    tu_usuario="root"
    tu_password="root"
    host="localhost"
    database="maxsoftdirecto"
    # Conexión con SQLAlchemy
    engine = create_engine(f"mysql+pymysql://{tu_usuario}:{tu_password}@{host}/{database}")

    query = """
    SELECT fecha, region, producto, cantidad, precio,
           cantidad*precio AS total
    FROM ventas
    WHERE fecha >= DATE_SUB(CURDATE(), INTERVAL 7 DAY);
    """

    df = pd.read_sql(query, engine)
    return df

