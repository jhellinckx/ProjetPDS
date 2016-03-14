# -*- coding: UTF-8 -*-
#!/usr/bin/python

files_dir = "raw/"

domain = "http://cuisine.journaldesfemmes.com"
all_recipes_categories = "/toutes-les-recettes/"
all_from_category = "/preferes"
all_tags = "/s/?f_libelle="

min_ratings = 5

tags_filename = files_dir + "jdf_tags.txt"
sub_categories_filename = files_dir + "jdf_sub_categories.txt"
details_error_messages_filename = files_dir + "jdf_errors.txt"
results_recipes_filename = files_dir + "results_jdf_recipes.txt"


post_rdi_url = "http://www.monmenu.fr/s/calculer-calories.html"


NAME_KEY = "recipe_name"
IMAGE_URL_KEY = "recipe_image_url"
URL_KEY = "recipe_url"
RATING_KEY = "rating"
NBR_RATINGS_KEY = "n_ratings"
INGREDIENTS_LIST_KEY = "ingredients_list"
INGREDIENTS_NAMES_KEY = "ingredients_names"
PORTIONS_KEY = "portions"
DIFFICULTY_KEY = "difficulty"
PREPARATION_TIME_KEY = "prep_time"
TAGS_KEY = "tags"
PREPARATION_STEPS_KEY = "prep_steps"
PRIMARY_CATEGORY_KEY = "primary_category"
SECONDARY_CATEGORY_KEY = "secondary_category"
CALORIE_PER_PORTION_KEY = "portion_calorie"
FAT_PER_PORTION_KEY = "portion_fat"
CARBO_PER_PORTION_KEY = "portion_carbo"
PROTEIN_PER_PORTION_KEY = "portion_protein"



SELECT recipe_id FROM CBUserPredictions INNER JOIN (SELECT rank, numrecipe FROM User_Preferences INNER JOIN RecipeSimilarity ON first_recipe_id=recipe_id AND second_recipe_id=numrecipe ORDER BY similarity LIMIT 3) Z ON Z.numUser=CBUserPredictions.user_id WHERE CBUserPredictions.user_id=1



