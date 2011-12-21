/**
 * Copyright 2011 The ARIES Consortium (http://www.ariesonline.org) and
 * www.integratedmodelling.org. 

   This file is part of Thinklab.

   Thinklab is free software: you can redistribute it and/or modify
   it under the terms of the GNU General Public License as published
   by the Free Software Foundation, either version 3 of the License,
   or (at your option) any later version.

   Thinklab is distributed in the hope that it will be useful, but
   WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
   General Public License for more details.

   You should have received a copy of the GNU General Public License
   along with Thinklab.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.integratedmodelling.agriculture.water;

import org.integratedmodelling.thinklab.KnowledgeManager;
import org.integratedmodelling.thinklab.interfaces.knowledge.IConcept;
import org.integratedmodelling.thinklab.interfaces.knowledge.datastructures.IntelligentMap;

/**
 * Implements crop water need determination as per FAO IRRIGATION WATER MANAGEMENT Training manual no. 3
 * see <a href="http://www.fao.org/docrep/S2022E/s2022e00.htm">FAO online document repository</a>
 * 
 * All of these methods get any additional knowledge in the form of concepts, and use the reasoner to 
 * attribute its role to each. Concepts that will be used are of these classes:
 * 
 * habitat:Humidity         {Low, High}
 * habitat:WindExposure     {Low, High}
 * crop:DevelopmentStage    {InitialStage, DevelopmentStage, MidSeasonStage, LateSeasonStage}
 * crop:SowingMethod        {Seed, Transplant}
 * crop:GrowingSeasonLength {VeryLow, Low, Moderate, High, VeryHigh}
 * 
 * The appropriate subclass should be passed to influence the calculations. If not there, average values will be 
 * used (total for DevelopmentStage), and simplified formulas may be triggered.
 * 
 * @author Ferd
 *
 */
public class CropWaterNeed {

	/*
	 * minimum and maximum, to be returned according to growing season length
	 */
	static IntelligentMap<int[]> growingPeriod = new IntelligentMap<int[]>();

	/*
	 * crop factors for each of the 4 development stages
	 */
	static IntelligentMap<double[]> cropFactors = new IntelligentMap<double[]>();

	/*
	 * growth stage durations for each of the development stages at minimum growth season length (first
	 * 4 numbers) and maximum season length (last 4 numbers).
	 */
	static IntelligentMap<double[]> growthStageDurations = new IntelligentMap<double[]>();

	private static boolean _initialized = false;

	public static double getReferenceEvapotranspiration(IConcept cropType,  IConcept ...concepts) {
		return 0.0;
	}

	public static double getCropFactor(IConcept cropType,  IConcept ...concepts) {
		return 0.0;
	}
	
	public static double getWaterNeedPerHarvest(IConcept cropType, IConcept ...concepts) {
		return 0.0;
	}

	/**
	 * Subtract rainfall to get irrigation need in mm
	 * @param cropType
	 * @param startMonth
	 * @param endMonth
	 * @param concepts
	 * @return
	 */
	public static double getMonthlyWaterNeed(IConcept cropType, int startMonth, int endMonth, IConcept ...concepts) {
		return 0.0;
	}

	
	
	public static void initialize() {

		if (!_initialized) {
			
			growingPeriod.put(KnowledgeManager.getConcept("crop:Alfalfa"), new int[]{100,365});
			growingPeriod.put(KnowledgeManager.getConcept("crop:Banana"), new int[]{300,365});
			growingPeriod.put(KnowledgeManager.getConcept("crop:Barley"), new int[]{120,150});
			growingPeriod.put(KnowledgeManager.getConcept("crop:Oat"), new int[]{120,150});
			growingPeriod.put(KnowledgeManager.getConcept("crop:Wheat"), new int[]{120,150});
			growingPeriod.put(KnowledgeManager.getConcept("crop:GreenBean"), new int[]{75,90});
			growingPeriod.put(KnowledgeManager.getConcept("crop:Bean"), new int[]{95,110});
			growingPeriod.put(KnowledgeManager.getConcept("crop:Cabbage"), new int[]{120,140});
			growingPeriod.put(KnowledgeManager.getConcept("crop:Carrot"), new int[]{100,150});
			growingPeriod.put(KnowledgeManager.getConcept("crop:Citrus"), new int[]{240,365});
			growingPeriod.put(KnowledgeManager.getConcept("crop:Cotton"), new int[]{180,195});
			growingPeriod.put(KnowledgeManager.getConcept("crop:Cucumber"), new int[]{105,130});
			growingPeriod.put(KnowledgeManager.getConcept("crop:Eggplant"), new int[]{130,140});
			growingPeriod.put(KnowledgeManager.getConcept("crop:Flax"), new int[]{150,220});
			growingPeriod.put(KnowledgeManager.getConcept("crop:SmallGrain"), new int[]{150,165});
			growingPeriod.put(KnowledgeManager.getConcept("crop:Lentil"), new int[]{150,170});
			growingPeriod.put(KnowledgeManager.getConcept("crop:Lettuce"), new int[]{75,140});
			growingPeriod.put(KnowledgeManager.getConcept("crop:SweetCorn"), new int[]{80,110});
			growingPeriod.put(KnowledgeManager.getConcept("crop:Maize"), new int[]{125,180});
			growingPeriod.put(KnowledgeManager.getConcept("crop:Melon"), new int[]{120,160});
			growingPeriod.put(KnowledgeManager.getConcept("crop:Millet"), new int[]{105,140});
			growingPeriod.put(KnowledgeManager.getConcept("crop:GreenOnion"), new int[]{70,95});
			growingPeriod.put(KnowledgeManager.getConcept("crop:Onion"), new int[]{150,210});
			growingPeriod.put(KnowledgeManager.getConcept("crop:GroundNut"), new int[]{130,140});
			growingPeriod.put(KnowledgeManager.getConcept("crop:Pea"), new int[]{90,100});
			growingPeriod.put(KnowledgeManager.getConcept("crop:Pepper"), new int[]{120,210});
			growingPeriod.put(KnowledgeManager.getConcept("crop:Potato"), new int[]{105,145});
			growingPeriod.put(KnowledgeManager.getConcept("crop:Radish"), new int[]{35,45});
			growingPeriod.put(KnowledgeManager.getConcept("crop:Rice"), new int[]{90,150});
			growingPeriod.put(KnowledgeManager.getConcept("crop:Sorghum"), new int[]{120,130});
			growingPeriod.put(KnowledgeManager.getConcept("crop:Soybean"), new int[]{135,150});
			growingPeriod.put(KnowledgeManager.getConcept("crop:Spinach"), new int[]{60,100});
			growingPeriod.put(KnowledgeManager.getConcept("crop:Squash"), new int[]{95,120});
			growingPeriod.put(KnowledgeManager.getConcept("crop:SugarBeet"), new int[]{160,230});
			growingPeriod.put(KnowledgeManager.getConcept("crop:SugarCane"), new int[]{270,365});
			growingPeriod.put(KnowledgeManager.getConcept("crop:Sunflower"), new int[]{125,130});
			growingPeriod.put(KnowledgeManager.getConcept("crop:Tobacco"), new int[]{130,160});
			growingPeriod.put(KnowledgeManager.getConcept("crop:Tomato"), new int[]{135,180});

			addCropFactor(KnowledgeManager.getConcept("crop:Wheat"), 0.35, 0.75, 1.15, 0.45);
			addCropFactor(KnowledgeManager.getConcept("crop:Barley"), 0.35, 0.75, 1.15, 0.45);
			addCropFactor(KnowledgeManager.getConcept("crop:Oat"), 0.35, 0.75, 1.15, 0.45);
			addCropFactor(KnowledgeManager.getConcept("crop:GreenBean"), 0.35, 0.70, 1.10, 0.90);
			addCropFactor(KnowledgeManager.getConcept("crop:Bean"), 0.35, 0.70, 1.10, 0.30);
			addCropFactor(KnowledgeManager.getConcept("crop:Cabbage"), 0.45, 0.75, 1.05, 0.90);
			addCropFactor(KnowledgeManager.getConcept("crop:Carrot"), 0.45, 0.75, 1.05, 0.90);
			addCropFactor(KnowledgeManager.getConcept("crop:Cotton"), 0.45, 0.75, 1.15, 0.75);
			addCropFactor(KnowledgeManager.getConcept("crop:Flax"), 0.45, 0.75, 1.15, 0.75);
			addCropFactor(KnowledgeManager.getConcept("crop:Cucumber"), 0.45, 0.70, 0.90, 0.75);
			addCropFactor(KnowledgeManager.getConcept("crop:Squash"), 0.45, 0.70, 0.90, 0.75);
			addCropFactor(KnowledgeManager.getConcept("crop:Eggplant"), 0.45, 0.75, 1.15, 0.80);
			addCropFactor(KnowledgeManager.getConcept("crop:Tomato"), 0.45, 0.75, 1.15, 0.80);
			addCropFactor(KnowledgeManager.getConcept("crop:SmallGrain"), 0.35, 0.75, 1.10, 0.65);
			addCropFactor(KnowledgeManager.getConcept("crop:Lentil"), 0.45, 0.75, 1.10, 0.50);
			addCropFactor(KnowledgeManager.getConcept("crop:Lettuce"), 0.45, 0.60, 1.00, 0.90);
			addCropFactor(KnowledgeManager.getConcept("crop:Spinach"), 0.45, 0.60, 1.00, 0.90);
			addCropFactor(KnowledgeManager.getConcept("crop:SweetCorn"), 0.40, 0.80, 1.15, 1.00);
			addCropFactor(KnowledgeManager.getConcept("crop:Maize"), 0.40, 0.80, 1.15, 0.70);
			addCropFactor(KnowledgeManager.getConcept("crop:Melon"), 0.45, 0.75, 1.00, 0.75);
			addCropFactor(KnowledgeManager.getConcept("crop:Millet"), 0.35, 0.70, 1.10, 0.65);
			addCropFactor(KnowledgeManager.getConcept("crop:GreenOnion"), 0.50, 0.70, 1.00, 1.00);
			addCropFactor(KnowledgeManager.getConcept("crop:Onion"), 0.50, 0.75, 1.05, 0.85);
			addCropFactor(KnowledgeManager.getConcept("crop:GroundNut"), 0.45, 0.75, 1.05, 0.70);
			addCropFactor(KnowledgeManager.getConcept("crop:Pea"), 0.45, 0.80, 1.15, 1.05);
			addCropFactor(KnowledgeManager.getConcept("crop:Pepper"), 0.35, 0.70, 1.05, 0.90);
			addCropFactor(KnowledgeManager.getConcept("crop:Potato"), 0.45, 0.75, 1.15, 0.85);
			addCropFactor(KnowledgeManager.getConcept("crop:Radish"), 0.45, 0.60, 0.90, 0.90);
			addCropFactor(KnowledgeManager.getConcept("crop:Sorghum"), 0.35, 0.75, 1.10, 0.65);
			addCropFactor(KnowledgeManager.getConcept("crop:Soybean"), 0.35, 0.75, 1.10, 0.60);
			addCropFactor(KnowledgeManager.getConcept("crop:SugarBeet"), 0.45, 0.80, 1.15, 0.80);
			addCropFactor(KnowledgeManager.getConcept("crop:Sunflower"), 0.35, 0.75, 1.15, 0.55);
			addCropFactor(KnowledgeManager.getConcept("crop:Tobacco"), 0.35, 0.75, 1.10, 0.90);

			/*
			 * cristo che palle
			 */
			growthStageDurations.put(KnowledgeManager.getConcept("crop:Barley"), 
					new double[]{15, 25, 50, 30, 15, 30, 65, 40});
			growthStageDurations.put(KnowledgeManager.getConcept("crop:Oat"), 
					new double[]{15, 25, 50, 30, 15, 30, 65, 40});
			growthStageDurations.put(KnowledgeManager.getConcept("crop:Wheat"), 
					new double[]{15, 25, 50, 30, 15, 30, 65, 40});
			growthStageDurations.put(KnowledgeManager.getConcept("crop:GreenBean"), 
					new double[]{15, 25, 25, 10, 20, 30, 30, 10});
			growthStageDurations.put(KnowledgeManager.getConcept("crop:Bean"), 
					new double[]{15, 25, 35, 20, 20, 30, 40, 20});
			growthStageDurations.put(KnowledgeManager.getConcept("crop:Cabbage"), 
					new double[]{20, 25, 60, 15, 25, 30, 65, 20 });
			growthStageDurations.put(KnowledgeManager.getConcept("crop:Carrot"), 
					new double[]{20, 30, 30, 20, 25, 35, 70, 20});
			growthStageDurations.put(KnowledgeManager.getConcept("crop:Cotton"), 
					new double[]{30, 50, 55, 45, 30, 50, 65, 50});
			growthStageDurations.put(KnowledgeManager.getConcept("crop:Flax"), 
					new double[]{30, 50, 55, 45, 30, 50, 65, 50});
			growthStageDurations.put(KnowledgeManager.getConcept("crop:Cucumber"), 
					new double[]{20, 30, 40, 15, 25, 35, 50, 20});
			growthStageDurations.put(KnowledgeManager.getConcept("crop:Eggplant"), 
					new double[]{30, 40, 40, 20, 30, 40, 45, 25});
			growthStageDurations.put(KnowledgeManager.getConcept("crop:SmallGrain"), 
					new double[]{20, 30, 60, 40, 25, 35, 65, 40});
			growthStageDurations.put(KnowledgeManager.getConcept("crop:Lentil"), 
					new double[]{20, 30, 60, 40, 25, 35, 70, 40});
			growthStageDurations.put(KnowledgeManager.getConcept("crop:Lettuce"), 
					new double[]{20, 30, 15, 10, 35, 50, 45, 10});
			growthStageDurations.put(KnowledgeManager.getConcept("crop:SweetCorn"), 
					new double[]{20, 25, 25, 10, 20, 30, 50, 10});
			growthStageDurations.put(KnowledgeManager.getConcept("crop:Maize"), 
					new double[]{20, 35, 40, 30, 30, 50, 60, 40});
			growthStageDurations.put(KnowledgeManager.getConcept("crop:Melon"), 
					new double[]{25, 35, 40, 20, 30, 45, 65, 20});
			growthStageDurations.put(KnowledgeManager.getConcept("crop:Millet"), 
					new double[]{15, 25, 40, 25, 20, 30, 55, 35});
			growthStageDurations.put(KnowledgeManager.getConcept("crop:GreenOnion"), 
					new double[]{25, 30, 10, 5, 25, 40, 20, 10});
			growthStageDurations.put(KnowledgeManager.getConcept("crop:Onion"), 
					new double[]{15, 25, 70, 40, 20, 35, 110, 45});
			growthStageDurations.put(KnowledgeManager.getConcept("crop:GroundNut"), 
					new double[]{25, 35, 45, 25, 30, 40, 45, 25});
			growthStageDurations.put(KnowledgeManager.getConcept("crop:Pea"), 
					new double[]{15, 25, 35, 15, 20, 30, 35, 15});
			growthStageDurations.put(KnowledgeManager.getConcept("crop:Pepper"), 
					new double[]{25, 35, 40, 20, 30, 40, 110, 30});
			growthStageDurations.put(KnowledgeManager.getConcept("crop:Potato"), 
					new double[]{25, 30, 30, 20, 30, 35, 50, 30});
			growthStageDurations.put(KnowledgeManager.getConcept("crop:Radish"), 
					new double[]{5, 10, 15, 5, 10, 10, 15, 5});
			growthStageDurations.put(KnowledgeManager.getConcept("crop:Sorghum"), 
					new double[]{20, 30, 40, 30, 20, 35, 45, 30});
			growthStageDurations.put(KnowledgeManager.getConcept("crop:Soybean"), 
					new double[]{20, 30, 60, 25, 20, 30, 70, 30});
			growthStageDurations.put(KnowledgeManager.getConcept("crop:Spinach"), 
					new double[]{20, 20, 15, 5, 20, 30, 40, 10});
			growthStageDurations.put(KnowledgeManager.getConcept("crop:Squash"), 
					new double[]{20, 30, 30, 15, 25, 35, 35, 25});
			growthStageDurations.put(KnowledgeManager.getConcept("crop:SugarBeet"), 
					new double[]{25, 35, 60, 40, 45, 65, 80, 40});
			growthStageDurations.put(KnowledgeManager.getConcept("crop:Sunflower"), 
					new double[]{20, 35, 45, 25, 25, 35, 45, 25});
			growthStageDurations.put(KnowledgeManager.getConcept("crop:Tomato"), 
					new double[]{30, 40, 40, 25, 35, 45, 70, 30});
			
			_initialized = true;
		}
	}
	
	/**
	 * Crop factors for the 4 growth stages
	 * 
	 * @param concept
	 * @param d
	 * @param e
	 * @param f
	 * @param g
	 */
	private static void addCropFactor(IConcept concept, double d, double e,
			double f, double g) {
		cropFactors.put(concept, new double[]{d, e, f, g});
	}

}
