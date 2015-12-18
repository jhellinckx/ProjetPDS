#!/usr/bin/python
import mysql.connector

cnx = mysql.connector.connect(user='jhellinckx', database='db_appli',password="...")
cursor = cnx.cursor()

query = ("SELECT id_food, url, code, product_name, image_url, energy_100g FROM Food")

cursor.execute(query)
for (id_food, url, code, product_name, image_url, energy_100g) in cursor:
	print product_name
cursor.close()
cnx.close()