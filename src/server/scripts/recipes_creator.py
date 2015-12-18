#!/usr/bin/python
import mysql.connector

db_name = "db_appli"
filename = "../dao/dao.properties"
def db_params():
	username = None
	password = None
	f = open(filename,"r")
	for line in f:
		line = line.strip().split(" ")
		if line[0] == "username":
			username = line[-1]
		elif line[0] == "password":
			password = line[-1]
	f.close()
	return (username, password)

(username, password) = db_params()

cnx = mysql.connector.connect(user=username, database=db_name,password=password)
cursor = cnx.cursor()

query = ("SELECT id_food, url, code, product_name, image_url, energy_100g FROM Food WHERE product_name LIKE '%chicken%'")

cursor.execute(query)
for (id_food, url, code, product_name, image_url, energy_100g) in cursor:
	print product_name
cursor.close()
cnx.close()