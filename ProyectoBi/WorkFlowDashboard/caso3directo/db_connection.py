from sqlalchemy import create_engine
import pandas as pd


def dataframe():
    engine = create_engine(f"mysql+pymysql://root:root@localhost/maxsoftdirecto")

    query="""
    SELECT fecha, region, producto, cantidad, precio, cantidad*precio AS total FROM ventas
    WHERE fecha >= DATE_SUB(CURDATE(), INTERVAL 7 DAY)
    """

    df= pd.read_sql(query, engine)

    return df


