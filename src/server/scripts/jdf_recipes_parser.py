# -*- coding: UTF-8 -*-
#!/usr/bin/python

import sys
import requests
import ast
import re
import codecs
import base64
import threading
from time import sleep
import traceback
from copy import deepcopy
from bs4 import BeautifulSoup
from recipe_model import *

GREEN = "\033[32m"
RED = "\033[31m"
RESET = "\033[0m"

domain = "http://cuisine.journaldesfemmes.com"
all_recipes_categories = "/toutes-les-recettes/"
all_from_category = "/preferes"
all_tags = "/s/?f_libelle="

min_ratings = 5

tags_filename = "jdf_tags.txt"
sub_tags_filename = "jdf_subtags.txt"

results_recipes_filename = "results_jdf_recipes.txt"

post_rdi_url = "http://www.monmenu.fr/s/calculer-calories.html"


def get_all_tags():
	structured_response = BeautifulSoup(requests.get(domain + all_tags).content, "lxml")
	tags = {}
	for tag_title in structured_response.find("div",attrs={"id":"blocOption"}).find_all("aside") :
		title = tag_title.find("header",attrs={"class":"searchTitle"}).find("span").string
		tags[title] = find_all_tags_in_title(tag_title)
	with open(tags_filename, "w+") as f:
		f.write(repr(tags))
		

def find_all_tags_in_title(tag_title):
	tags = []
	tag_list = tag_title.find("fieldset",attrs={"id":"fullList1"})
	if tag_list != None:
		for tag in tag_list.find_all("a"):
			if "id" not in tag.attrs :
				tag_name = parse_real_name(tag.text)
				tags.append(tag_name)
		return tags
	tag_list = tag_title.find("fieldset",attrs={"id":"fullList2"})
	if tag_list != None :
		for tag in tag_list.find_all("label"):
			tag_name = parse_real_name(tag.text)
			tags.append(tag_name)
		return tags
	tag_list = tag_title.find("fieldset",attrs={"id":"fullList3"})
	if tag_list != None : 
		for tag in tag_list.find_all("label"):
			tag_name = parse_real_name(tag.text)
			tags.append(tag_name)
		return tags
	tag_list = tag_title.find("fieldset",attrs={"id":"fullList6"})
	if tag_list != None :
		for tag in tag_list.find_all("label"):
			tag_name = parse_real_name(tag.text)
			tags.append(tag_name)
		return tags
	tag_list = tag_title.find("fieldset",attrs={"id":"topList4"})
	if tag_list != None : 
		for tag in tag_list.find_all("a"):
			if "id" not in tag.attrs :
				tag_name = parse_real_name(tag.text)
				tags.append(tag_name)
		return tags
	tag_list = tag_title.find("fieldset",attrs={"id":"topList5"})
	if tag_list != None : 
		for tag in tag_list.find_all("a"):
			if "id" not in tag.attrs :
				tag_name = parse_real_name(tag.text)
				tags.append(tag_name)
		return tags

def parse_real_name(name):
	return " ".join(name.split()).split("(")[0].strip(" ")


class SubCategoryParserWorker(threading.Thread):
	file_lock = threading.Lock()
	progress_lock = threading.Lock()
	progress = 0

	def __init__(self, main_category, sub_categories):
		threading.Thread.__init__(self)
		self.main_category = main_category
		self.sub_categories = sub_categories
		SubCategoryParserWorker.progress_lock.acquire()
		SubCategoryParserWorker.progress += len(sub_categories)
		SubCategoryParserWorker.progress_lock.release()
	
	def run(self):
		self.running = True
		for sub_category in self.sub_categories :
			self.parseSubCategory(sub_category)
			SubCategoryParserWorker.progress_lock.acquire()
			SubCategoryParserWorker.progress -= 1
			print "[" + self.main_category + "] Finished parsing subcategory \"" + sub_category[0] + "\". " + GREEN + str(SubCategoryParserWorker.progress) + RESET + " subcategories left."  
			SubCategoryParserWorker.progress_lock.release()
			return # TO DELETE ------------------------------------


	def stop(self):
		self.running = False

	def parseSubCategory(self, sub_category):
		min_ratings_reached = False
		page = 0
		while not min_ratings_reached :
			page += 1
			url = domain + sub_category[1] + all_from_category + "-page" + str(page)
			structured_recipes_list = BeautifulSoup(requests.get(url).content, "lxml")
			recipes_div = structured_recipes_list.find("section", attrs={"class":"bu_cuisine_best_recipes"})
			if recipes_div != None:
				for recipe_article_tag in recipes_div.find_all("article"):
					note_tag = recipe_article_tag.find("div",attrs={"class":"bu_cuisine_recette_notes"})
					if note_tag != None:
						count_tag = note_tag.find("span",attrs={"class":"count count--fr"})
						if count_tag != None:
							ratings_string = count_tag.string
							ratings = ""
							for char in ratings_string.split("(")[1]:
								if char.isdigit(): 
									ratings += char
								else : 
									break
							if int(ratings) < min_ratings :
								min_ratings_reached = True
								break
					recipe_title_tag = recipe_article_tag.find("span",attrs={"class":"bu_cuisine_title_4"})
					if recipe_title_tag != None:
						url_tag = recipe_title_tag.find("a")
						if "href" in url_tag.attrs :
							#self.parseRecipe(sub_category, url_tag.attrs["href"])
							self.parseRecipe(sub_category, "/recette/242289-moussaka") # TO DELETE
							return # TO DELETE ------------------------------------

	def parseRecipe(self, sub_category, sub_url):
		url = domain + sub_url
		recipe = {}
		recipe[URL_KEY] = url
		recipe[PRIMARY_CATEGORY_KEY] = self.main_category
		recipe[SECONDARY_CATEGORY_KEY] = sub_category[0]
		structured_recipe = BeautifulSoup(requests.get(url).content, "lxml")

		# Find recipe name
		name_tag = structured_recipe.find("h1",attrs={"class":"bu_cuisine_title_1"})
		if name_tag != None:
			name_span_tag = name_tag.find("span")
			if name_span_tag != None :
				recipe[NAME_KEY] = name_span_tag.string

		recipe_main_tag = structured_recipe.find("article",attrs={"class":"grid_line gutter grid--norwd bu_cuisine_main_recipe"})
		if recipe_main_tag != None :
			# Find image url
			image_url_div = recipe_main_tag.find("div",attrs={"class":"bu_cuisine_recette_img"})
			if image_url_div != None:
				figure_tag = image_url_div.find("figure")
				if figure_tag != None :
					image_url_tag = figure_tag.find("a")
					if image_url_tag != None and "href" in image_url_tag.attrs:
						recipe[IMAGE_URL_KEY] = image_url_tag["href"]
			# Find ratings
			rating_span = recipe_main_tag.find("span",attrs={"class":"jAverage"})
			if rating_span != None :
				recipe[RATING_KEY] = " ".join(rating_span.string.split())
			n_ratings_span = recipe_main_tag.find("span",attrs={"class":"jNbNote"})
			if n_ratings_span != None :
				recipe[NBR_RATINGS_KEY] = n_ratings_span.string

			# Find portions
			portions_title_p_tag = recipe_main_tag.find("p", attrs={"class" : "bu_cuisine_title_3"})
			if portions_title_p_tag != None : 
				portions_span = portions_title_p_tag.find("span")
				if portions_span != None :
					recipe[PORTIONS_KEY] = portions_span.string

			# Find difficulty and prep time
			ul_tag = recipe_main_tag.find("ul",attrs={"class":"bu_cuisine_carnet_2"})
			li_tags = ul_tag.find_all("li")
			if len(li_tags) > 1 :
				recipe[DIFFICULTY_KEY] = li_tags[0].string
				span_tag = li_tags[1].find("span",attrs={"class":"value-title"})
				if span_tag != None and "title" in span_tag.attrs:
					recipe[PREPARATION_TIME_KEY] = span_tag.attrs["title"]

			# Find ingredients
			recipe[INGREDIENTS_LIST_KEY] = []
			recipe[INGREDIENTS_NAMES_KEY] = []
			ingredients_ul = recipe_main_tag.find("ul", attrs={"class":"bu_cuisine_ingredients"})
			for li_tag in ingredients_ul.find_all("li"):
				recipe[INGREDIENTS_LIST_KEY].append(li_tag.text)
				ingr_name_tag = li_tag.find("a")
				if ingr_name_tag != None :
					recipe[INGREDIENTS_NAMES_KEY].append(ingr_name_tag.text)

		# Find prep steps
		recipe[PREPARATION_STEPS_KEY] = []
		prepa_exception = structured_recipe.find("div",attrs={"class":"bu_cuisine_recette_prepa bu_cuisine_recette_prepa_exception"}) 
		prepa_normal = structured_recipe.find_all("div",attrs={"bu_cuisine_recette_prepa"})
		if prepa_exception != None: 
			grid_last_div = prepa_exception.find("div",attrs={"class":"grid_last"})
			if grid_last_div != None :
				recipe[PREPARATION_STEPS_KEY].append(" ".join(grid_last_div.text.split()))
		elif prepa_normal != None :
			for prepa_step in prepa_normal :
				recipe[PREPARATION_STEPS_KEY].append(" ".join(prepa_step.text.split()))


		# Find tags
		recipe[TAGS_KEY] = []
		tags_aside = structured_recipe.find("aside",attrs={"class":"grid_line bu_cuisine_themes"})
		if tags_aside != None :
			for a_tag in tags_aside.find_all("a"):
				recipe[TAGS_KEY].append(a_tag.text)

		self.parseRecipeRDIs(recipe)

		print repr(recipe)

	def parseRecipeRDIs(self, recipe):
		FORM_CONTENT_KEY = "content"
		FORM_PORTIONS_KEY = "portions"
		form_data = \
		{
			FORM_CONTENT_KEY : "",\
			FORM_PORTIONS_KEY : 1
		}
		if INGREDIENTS_LIST_KEY in recipe :
			for ingredient in recipe[INGREDIENTS_LIST_KEY]:
				form_data[FORM_CONTENT_KEY] += ingredient + "\n"
			if len(form_data[FORM_CONTENT_KEY]) > 0 :
				form_data[FORM_CONTENT_KEY] = form_data[FORM_CONTENT_KEY][:-1]
		if PORTIONS_KEY in recipe :
			form_data[FORM_PORTIONS_KEY] = recipe[PORTIONS_KEY]
		structured_response = BeautifulSoup(requests.post(post_rdi_url, data = form_data).content, "lxml")
		rdis_div = structured_response.find("div",attrs={"class":"grid2 grey rounded-box bordered mt1 pa1"})
		cal_li = rdis_div.find("li",attrs={"id":"kcal"})
		if cal_li != None :
			cal_div = cal_li.find("div")
			if cal_div != None :
				recipe[CALORIE_PER_PORTION_KEY] = " ".join(cal_div.text.split())
		prot_li = rdis_div.find("li",attrs={"id":"proteine"})
		if prot_li != None :
			prot_div = prot_li.find("div")
			if prot_div != None :
				recipe[PROTEIN_PER_PORTION_KEY] = " ".join(prot_div.text.split())
		fat_li = rdis_div.find("li",attrs={"id":"lipide"})
		if fat_li != None :
			fat_div = fat_li.find("div")
			if fat_div != None :
				recipe[FAT_PER_PORTION_KEY] = " ".join(fat_div.text.split())
		carbo_li = rdis_div.find("li",attrs={"id":"glucide"})
		if carbo_li != None :
			carbo_div = carbo_li.find("div")
			if carbo_div != None :
				recipe[CARBO_PER_PORTION_KEY] = " ".join(carbo_div.text.split())


	@staticmethod
	def saveRecipe(recipe):
		SubCategoryParserWorker.file_lock.acquire()
		try:
			with open(results_recipes_filename, "a") as f:
				f.write(repr(recipe))
				f.write("\n")
		except Exception as e : 
			raise e
		finally:
			SubCategoryParserWorker.file_lock.release()


def get_recipes_sub_categories() : 
	raw_response = requests.get(domain + all_recipes_categories)
	structured_response = BeautifulSoup(raw_response.content, "lxml")
	tag_titles = {}
	for tag_title in structured_response.find_all("a",attrs={"class":"bu_cuisine_title_3"}):
		tag_title_name = tag_title.string
		tag_titles[tag_title_name] = []
		for sub_tag in tag_title.parent.find_all("a",attrs={"class":"bu_cuisine_bloc"}):
			sub_tag_tuple = (sub_tag.string, "")
			href = ""
			if "href" in sub_tag.attrs:
				href = sub_tag.attrs["href"]
			sub_tag_tuple = (sub_tag.string, href)
			tag_titles[tag_title_name].append(sub_tag_tuple)
	with open(sub_tags_filename, "w+") as f:
		f.write(repr(tag_titles))

def start_parsing_sub_categories():
	categories = {}
	with open(sub_tags_filename, "r") as f:
		categories = ast.literal_eval(f.read())
	category = (u'Petit d\xe9jeuner', [(u'Boisson brunch', '/recette-boisson-brunch'), (u'Brioche', '/recette-brioche'), (u'Brunch sal\xe9', '/recette-brunch-sale'), (u'Brunch sucr\xe9', '/recette-brunch-sucre'), (u'Confiture', '/recette-confiture'), (u'Pain boulanger', '/recette-pain-boulanger')])
	worker = SubCategoryParserWorker(category[0], category[1])
	worker.start()
	worker.join()
	# workers = [SubCategoryParserWorker(category, categories[category]) for category in categories]
	# for worker in workers :
	# 	worker.start()
	# for worker in workers:
	# 	worker.join()




if __name__ == "__main__":
	#get_all_tags()
	#get_recipes_sub_categories()
	start_parsing_sub_categories()
