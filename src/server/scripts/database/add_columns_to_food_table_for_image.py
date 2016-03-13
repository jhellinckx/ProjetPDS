#!/usr/bin/python
# -*- coding: UTF-8 -*-

import sys
import ast
import requests
import requests.exceptions
from binary_to_png import *
import time

try:
	import mysql.connector
	from mysql.connector import errorcode

except: # no mysql.connector
	print "Error : import impossible (MySQL python connector). Sur ubuntu, suivre \
	'Installing Connector/Python on Linux Using a Debian Package' sur \
	https://dev.mysql.com/doc/connector-python/en/connector-python-installation-binary.html"
	sys.exit()

MAGENTA = "\033[35m"
GREEN = "\033[32m"
RED = "\033[31m"
RESET = "\033[0m"

db_name = "db_colruyt"
db_properties_filename = "../../src/main/resources/dao.properties"
filename = "raw/images_binary_file.txt"
delimiter = "€£"

def db_params():
	username = None
	password = None
	f = open(db_properties_filename,"r")
	for line in f:
		line = line.strip().split(" ")
		if line[0] == "username":
			username = line[-1]
		elif line[0] == "password":
			password = line[-1]
	f.close()
	return (username, password)

def addOrDeleteColumnToTable(delete): #if boolean delete is True, deletes otherwise add

	addColumnToTableCommand = (
		"ALTER TABLE `Food`"
		"ADD ("
		"`image_blob` BLOB);"
		)

	deleteColumnsFromTableCommand = (
		"ALTER TABLE `Food` "
		"DROP `image_blob`;"
		)

	printName="Adding"
	command=addColumnToTableCommand

	if delete:
		printName="Deleting"
		command=deleteColumnsFromTableCommand

	(username,password) = db_params()
	cnx = mysql.connector.connect(user=username,database=db_name,password=password)
	cursor = cnx.cursor()

	# Add or delete columns to the Food table
	sys.stdout.write(printName + " columns to table Food.. ")
	try:
		cursor.execute(command)
		sys.stdout.write(GREEN + "OK" + RESET + "\n")
	except mysql.connector.Error as err:
		sys.stdout.write(RED + "FAILED : %s"%err + RESET + "\n")
	cnx.commit()
	cursor.close()
	cnx.close()

def selectIdsFromDbColumn():
	selectIdFromFoodCommand = (
		"SELECT `recipe_id`"
		"FROM `Recipe` ORDER BY recipe_id ASC")

	ids = []
	(username,password) = db_params()
	cnx = mysql.connector.connect(user=username,database=db_name,password=password)
	cursor = cnx.cursor()
	sys.stdout.write("Trying to select id from DB ..")
	try:
		cursor.execute(selectIdFromFoodCommand)
		sys.stdout.write(GREEN + " -> OK" + RESET + "\n")
		for idf in cursor:
			ids.append(idf[0])
	except mysql.connector.Error as err:
		sys.stdout.write(RED + "FAILED : %s"%err + RESET + "\n")

	return ids



def selectUrlsFromDbColumn(ids):
	selectUrlsFromFoodCommand = (
		"SELECT `recipe_image_url`"
		"FROM `Recipe`"
		"WHERE `recipe_id` = %s")

	image_dict = {}.fromkeys(ids)

	(username,password) = db_params()
	cnx = mysql.connector.connect(user=username,database=db_name,password=password)
	cursor = cnx.cursor()
	sys.stdout.write("Trying to select urls from DB ..")
	for i in ids:
		try:
			args = (i,)
			cursor.execute(selectUrlsFromFoodCommand, args)
			image_dict[i] = cursor.fetchone()[0]
		except mysql.connector.Error as err:
			sys.stdout.write(RED + "FAILED : %s"%err + RESET + "\n")
	sys.stdout.write(GREEN + " -> OK" + RESET + "\n")

	return image_dict


def getAllImagesFromUrl(image_dict):
	print "Downloading images (takes overs 25 min for db_colruyt).. "
	string = "Images Downloaded: {0}"
	count = 0
	s = requests.Session()				# http-presistent requests.
	for i in image_dict:
		image_downloaded = False
		while(not image_downloaded):
			try:
				response = s.get(image_dict[i])
				image_dict[i] = (response.content)
				count += 1
				image_downloaded = True
				if ((count%200) == 0):
					print(string.format(count))
			except requests.exceptions.ConnectionError:
				time.sleep(5)
				s = requests.Session()

def getAllImagesFromFile():
	images = []
	print "Getting images from file ..."
	with open(filename, 'rb') as f:
		images = f.read().split(delimiter)
		images = images[:len(images)-1]
	return images

def updateDb(images, image_urls):
	updateImageColumnCommand = (
		"UPDATE `Food`"
		"SET `image_blob` = %s"
		"WHERE `image_url` = %s")
	(username,password) = db_params()
	cnx = mysql.connector.connect(user=username,database=db_name,password=password)
	cursor = cnx.cursor()
	sys.stdout.write("Updating image_blob in DB ...")

	for i in range(len(images)):
		try:
			args = (images[i], image_urls[i])
			cursor.execute(updateImageColumnCommand, args) 
		except mysql.connector.Error as err:
			sys.stdout.write(RED + "FAILED : %s"%err + RESET + "\n")

	cnx.commit()
	cursor.close()
	cnx.close()



def execute(with_file):
	food_ids = selectIdsFromDbColumn()
	image_dict = selectUrlsFromDbColumn(food_ids)
	getAllImagesFromUrl(image_dict)
	convertImages(image_dict)


if __name__ == "__main__":
	execute(True)