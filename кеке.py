import random
from bs4 import BeautifulSoup
import requests
import sqlalchemy as db
from tabulate import tabulate
import os
import pandas as pd
import matplotlib.pyplot as plt
import logging

db_connection_url = os.getenv('DATABASE_URL', 'sqlite:///weather_database.db')
engine = db.create_engine(db_connection_url)
connection = engine.connect()
metadata = db.MetaData()

City = db.Table('City', metadata,
                db.Column('id', db.Integer, primary_key=True),
                db.Column('name', db.Text),
                db.Column('region', db.Text))

Checks = db.Table('Checks', metadata,
                  db.Column('id', db.Integer, primary_key=True),
                  db.Column('city_id', db.Integer, db.ForeignKey('City.id')),
                  db.Column('date', db.String),
                  db.Column('time', db.String))

Weather= db.Table('Weather', metadata,
                       db.Column('id', db.Integer, primary_key=True),
                       db.Column('check_id', db.Integer, db.ForeignKey('Checks.id')),
                       db.Column('date', db.String),
                       db.Column('temperature', db.Integer),
                       db.Column('wind', db.String))

metadata.create_all(engine)

logging.basicConfig(filename='weather_log.log', filemode='w', format='%(asctime)s - %(name)s - %(levelname)s - %(message)s', level=logging.INFO, encoding='utf-8')

weather_site_url = os.getenv('weather_site', 'https://weather.rambler.ru/world/rossiya/')
base_site_url = os.getenv('base_site', 'https://weather.rambler.ru')

logging.info('Requesting data from the weather site...')
response = requests.get(weather_site_url)
logging.info('Received response from the weather site.')
soup = BeautifulSoup(response.text, 'html.parser')

region_elements = soup.find_all('a', class_='kgSF')
#random_regions = random.sample(region_elements, 7)
for region_element in region_elements[6:13]:
    region_name = region_element.get('data-weather').split('::')[-1]
    logging.info(f'Inserting region "{region_name}" into the database...')
    # Используем таблицу City для добавления регионов
    region_insert = City.insert().values(region=region_name)
    result = connection.execute(region_insert)
    logging.info(f'Region "{region_name}" inserted into the database.')
    region_id = result.inserted_primary_key[0]

    region_url = base_site_url + region_element.get('href')
    response = requests.get(region_url)
    soup = BeautifulSoup(response.text, 'html.parser')
    city_elements = soup.find_all('a', class_="MJZ5")

    for city_element in city_elements[:5]:
        city_name = city_element.get('data-weather').split('::')[-1]
        logging.info(f'Inserting city "{city_name}" into the database...')
        city_record = City.insert().values(name=city_name, region=region_name)
        result = connection.execute(city_record)
        logging.info(f'City "{city_name}" inserted into the database.')
        city_id = result.inserted_primary_key[0]

        city_url = base_site_url + city_element.get('href')
        response = requests.get(city_url)
        soup = BeautifulSoup(response.text, 'html.parser')
        week_dates = soup.find_all('span', class_="PADa")
        temps = soup.find_all('span', class_="AY6t")
        winds = soup.find_all('span', class_="ZX9i")

        for date, temp, wind in zip(week_dates, temps, winds):
            temperature = int(temp.text.replace('°', '').replace('-', '-'))
            weather_check = Checks.insert().values(city_id=city_id, date=date.text, time='06:00')
            result = connection.execute(weather_check)
            check_id = result.inserted_primary_key[0]

            logging.info(f'Temperature "{temperature}" recorded for city "{city_name}" on date "{date.text}" at time "06:00".')
            logging.info(f'Wind "{wind.text}" recorded for city "{city_name}" on date "{date.text}" at time "06:00".')

            weather_data = Weather.insert().values(check_id=check_id, date=date.text, temperature=temperature, wind=wind.text)
            connection.execute(weather_data)

metadata.reflect(bind=engine)

tables = metadata.tables

for table_name, table in tables.items():
    print(f"Table: {table_name}")
    column_names = [column.name for column in table.c]
    data = connection.execute(table.select()).fetchall()
    print(tabulate(data, headers=column_names))
    print()

logging.info('Executing query for weather data...')
query = """
SELECT City.name, Weather.date, Weather.temperature, City.region AS region_name
FROM City
JOIN Checks ON City.id = Checks.city_id
JOIN Weather ON Checks.id = Weather.check_id
ORDER BY City.name, Weather.date
"""
df = pd.read_sql_query(query, connection)
logging.info('Weather data query executed.')

for city in df['name'].unique()[:5]:
    city_data = df[df['name'] == city]
    region_name = city_data['region_name'].iloc[0]  # Get region name for the city
    plt.plot(city_data['date'], city_data['temperature'])
    plt.title(f"{city} ({region_name})")  # Include region name in the plot title
    plt.xlabel('Date')
    plt.ylabel('Temperature')
    plt.show()

connection.close()
logging.info('Connection to the database closed.')
