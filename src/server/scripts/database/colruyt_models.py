#!/usr/bin/python
# -*- coding: UTF-8 -*-

data_dir = "raw/"

branch_results_filename = data_dir + "results_branch_articles.txt"
branch_status_filename = data_dir + "branch_status.txt"
branch_error_messages_filename = data_dir + "branch_errors.txt"

details_results_filename = data_dir + "results_details_articles.txt"
details_status_filename = data_dir + "details_status.txt"
details_error_messages_filename = data_dir + "details_errors.txt"


class BaseArticle:
	BRANCH_INDEX_KEY = "branch_index"
	IMAGE_KEY = "image_url"
	PRICE_UNIT_KEY = "price_unit"
	PRICE_KG_KEY = "price_kg"
	DETAILS_URL_KEY = "details_url"
	CATEGORIES_KEY = "categories"
	WEIRD_NAME_KEY = "weird_name"
	SHORT_DESCRIPTION_KEY = "short_description"
	FULL_DESCRIPTION_KEY = "full_description"

	KEYS = 	[
				BRANCH_INDEX_KEY,\
				IMAGE_KEY,\
				PRICE_UNIT_KEY,\
				PRICE_KG_KEY,\
				DETAILS_URL_KEY,\
				CATEGORIES_KEY,\
				WEIRD_NAME_KEY,\
				SHORT_DESCRIPTION_KEY,\
				FULL_DESCRIPTION_KEY
			]

	def __init__(self, article = None):
		self.infos = {}
		for key in BaseArticle.KEYS:
			self.infos[key] = None
		self.infos[BaseArticle.CATEGORIES_KEY] = []
		if isinstance(article, dict):
			self.fromDict(article)
		elif isinstance(article, BaseArticle):
			self.fromDict(article.infos)

	def __repr__(self):
		return self.infos.__repr__()

	def fromDict(self, infos_dict):
		if isinstance(infos_dict, dict):
			for key in infos_dict:
				if key in BaseArticle.KEYS :
					if isinstance(infos_dict[key],list) :
						self.infos[key] = deepcopy(infos_dict[key])
					else : 
						self.infos[key] = infos_dict[key]


class DetailedArticle(BaseArticle):
	REAL_DETAILS_URL_KEY = "real_details_url"

	PORTION_ENERGY_KJ_KEY = "portion_energy_kj"
	PORTION_ENERGY_KCAL_KEY = "portion_energy_kcal"
	PORTION_TOTAL_FAT_KEY = "portion_total_fat"
	PORTION_SATURATED_FAT_KEY = "portion_saturated_fat"
	PORTION_TOTAL_CARBOHYDRATES_KEY = "portion_total_carbohydrates"
	PORTION_SUGARS_KEY = "portion_sugars"
	PORTION_TOTAL_PROTEINS_KEY = "portion_proteins"
	PORTION_FIBERS_KEY = "portion_fibers"
	PORTION_SALT_KEY = "portion_salt"
	PORTION_INFO_KEY = "portion_info"

	PER_100G_ENERGY_KJ_KEY = "100g_energy_kj"
	PER_100G_ENERGY_KCAL_KEY = "100g_energy_kcal"
	PER_100G_TOTAL_FAT_KEY = "100g_total_fat"
	PER_100G_SATURATED_FAT_KEY = "100g_saturated_fat"
	PER_100G_TOTAL_CARBOHYDRATES_KEY = "100g_total_carbohydrates"
	PER_100G_SUGARS_KEY = "100g_sugars"
	PER_100G_TOTAL_PROTEINS_KEY = "100g_proteins"
	PER_100G_FIBERS_KEY = "100g_fibers"
	PER_100G_SALT_KEY = "100g_salt"

	TOTAL_QUANTITY_KEY = "total_quantity"
	INGREDIENTS_TEXT_KEY = "ingredients_text"
	BAR_CODE_KEY = "bar_code"

	ALLERGENS_CONTAINS_KEY = "allergens_contains"
	ALLERGENS_TRACE_OF_KEY = "allergens_trace"

	KEYS = 	[
				REAL_DETAILS_URL_KEY,\
				PORTION_ENERGY_KJ_KEY,\
				PORTION_ENERGY_KCAL_KEY,\
				PORTION_TOTAL_FAT_KEY,\
				PORTION_SATURATED_FAT_KEY,\
				PORTION_TOTAL_CARBOHYDRATES_KEY,\
				PORTION_SUGARS_KEY,\
				PORTION_TOTAL_PROTEINS_KEY,\
				PORTION_FIBERS_KEY,\
				PORTION_SALT_KEY,\
				PORTION_INFO_KEY,\
				PER_100G_ENERGY_KJ_KEY,\
				PER_100G_ENERGY_KCAL_KEY,\
				PER_100G_TOTAL_FAT_KEY,\
				PER_100G_SATURATED_FAT_KEY,\
				PER_100G_TOTAL_CARBOHYDRATES_KEY,\
				PER_100G_SUGARS_KEY,\
				PER_100G_TOTAL_PROTEINS_KEY,\
				PER_100G_FIBERS_KEY,\
				PER_100G_SALT_KEY,\
				TOTAL_QUANTITY_KEY,\
				INGREDIENTS_TEXT_KEY,\
				BAR_CODE_KEY,\
				ALLERGENS_CONTAINS_KEY,\
				ALLERGENS_TRACE_OF_KEY
			]

	def __init__(self, article):
		BaseArticle.__init__(self, article)
		for key in DetailedArticle.KEYS :
			self.infos[key] = None
		self.infos[DetailedArticle.ALLERGENS_CONTAINS_KEY] = []
		self.infos[DetailedArticle.ALLERGENS_TRACE_OF_KEY] = []
		if isinstance(article, dict):
			self.fromDict(article, False)
		elif isinstance(article, DetailedArticle):
			self.fromDict(article.infos, False)

		def fromDict(self, infos_dict, call_parent = True):
			if call_parent:
				super(DetailedArticle, self).fromDict(infos_dict)
			if isinstance(infos_dict, dict):
				for key in infos_dict:
					if key in DetailedArticle.KEYS :
						if isinstance(infos_dict[key], list):
							self.infos[key] = deepcopy(infos_dict[key])
						else : 
							self.infos[key] = infos_dict[key]
