#!/usr/bin/python
# -*- coding: UTF-8 -*-

from os import mkdir
import os
from os import rmdir


filename = "raw/images_binary_file.txt"
directory = "raw/db_images"
png_filename = "db_image_{0}"
delimiter = "€£"
no_pic_image = "raw/nopic.jpg"

from PIL import Image
from cStringIO import StringIO


def getBinaryImagesFromFile():
	images = []
	print "Getting images from file ..."
	with open(filename, 'rb') as f:
		images = f.read().split(delimiter)
		images = images[:len(images)-1]
	return images

def convertBinaryToImages(binary_images):
	images = []
	for binary_image in binary_images:
		imagefile = StringIO(binary_image)
		images.append(Image.open(imagefile))
	return images

def deleteFilesInDirectory():
	for f in os.listdir(directory):
		path = os.path.join(directory, f)
		try:
			if os.path.isfile(path):
				os.unlink(path)
		except Exception as e:
			print e

def createDirectory():
	try:
		deleteFilesInDirectory()
		rmdir(directory)
	except OSError as e:
		mkdir(directory)

def saveImagesAsPNG(images):
	for i in range(len(images)):
		f = directory + "/" + png_filename.format(i+1)
		img = images[i]
		try:
			img.save(f, 'jpeg')
		except IOError:
			img = Image.open(no_pic_image)
			img.save(f, 'jpeg')



def execute():
	binary_images = getBinaryImagesFromFile()
	images = convertBinaryToImages(binary_images)
	createDirectory()
	saveImagesAsPNG(images)
	

if __name__ == "__main__":
	execute()