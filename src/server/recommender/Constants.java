package recommender;

public final class Constants {

		private Constants () {
				// to remove the instantiation 
		}

		/*
		Human's daily intake requirements
		Info's from : http://www.mydailyintake.net/daily-intake-levels/
		*/
		public static final float HUMAN_DAILY_FAT = 70; //g
		public static final float HUMAN_DAILY_PROTEINS = 50; //g
		public static final float HUMAN_DAILY_SATURATED_FAT = 20; //g
		public static final float HUMAN_DAILY_CARBOHYDRATES = 310; //g
		public static final float HUMAN_DAILY_SUGARS = 90; //g
		public static final float HUMAN_DAILY_SODIUM = 2.3f; //g

		/* 
		Men & Women's daily energy/calories
		Energetic infos are an average from : http://onmangequoi.lamutuellegenerale.fr/besoins-alimentaires
		*/
		public static final float MEN_DAILY_ENERGY = 9734.31f; //kJoule (2325 kcal)
		public static final float WOMEN_DAILY_ENERGY = 7745.58f; //kJoule (1850 kcal)
		public static final float TEEN_DAILY_ENERGY = 11304.36f; //kJoule (2700 kcal)
		public static final float CHILD_DAILY_ENERGY = 9286.3224f; //kJoule (2218 kcal)
}
