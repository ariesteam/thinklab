package org.integratedmodelling.mca.evamix;

import java.util.ArrayList;
import java.util.Collection;

import org.apache.log4j.Logger;
import org.integratedmodelling.collections.Pair;
import org.integratedmodelling.exceptions.ThinklabException;
import org.integratedmodelling.exceptions.ThinklabValidationException;

/**
 * Perform EVAMIX concordance analysis of a set of quantitative and/or
 * qualitative criteria with a set of weights.
 * 
 * @author Tim Welch (original Python version), EcoTrust 2007 GPL-2 release
 * @author Ferdinando Villa (port to Java and modifications)
 * 
 */
public class Evamix {

	/*
	 * give it its own logger so we can use it somewhere else as well
	 */
	static Logger log = Logger.getLogger(Evamix.class);

	/**
	 * Evamix results are grouped in an object of this kind.
	 * 
	 * @author Ferdinando Villa
	 * 
	 */
	public static class Results {

		int num_criteria;
		int num_alternatives;
		int num_qual_criteria;
		int num_quant_criteria;
		public double[][] quant_impact_matrix;
		public double[][] qual_impact_matrix;
		public double[][] evamix_matrix;
		public double[]   evamix_scores;

		String[] alternative_names = null;
		String[] criteria_names = null;
		
		/**
		 * if this is not null, the final scores and matrices do not contains
		 * the original input criteria indexed by the column numbers in this
		 * arra
		 */
		public ArrayList<Integer> degenerate_columns = null;
		private double[][] input;
		private double[] criteria_weights;
		private String[] criteria_types;
		private boolean[] criteria_cost_benefit;

		public Results(double[][] input, double[] criteria_weights,
				String[] criteria_types, boolean[] criteria_cost_benefit) {

			this.input = input;
			this.criteria_weights = criteria_weights;
			this.criteria_types = criteria_types;
			this.criteria_cost_benefit = criteria_cost_benefit;	
		}

		public void dump() {

			System.out.println("Criteria weights:\n");
			
			for (int i = 0; i < criteria_names.length; i++) {
				System.out.println(
						"\t" +
						criteria_names[i] + " (" +
						criteria_types[i] + 
						", " +
						(criteria_cost_benefit == null ? 
								"benefit" : 
								(criteria_cost_benefit[i] ? "benefit" : "cost")) +
						"): " +
						criteria_weights[i]);
			}
			
			System.out.println("\nInput data:\n");

			for (int i = 0; i < input.length; i++) {

				System.out.print("\t" + alternative_names[i] + ": ");
				for (int j = 0; j < input[0].length; j++) {
					System.out.print(input[i][j] + " ");
				}
				System.out.println();
			}
			
			System.out.println("\nFinal EVAMIX concordance scores:\n");

			for (int i = 0; i < evamix_scores.length; i++) {
				System.out.println(
						"\t" + 
						alternative_names[i] + ": "
						+ evamix_scores[i]);
			}
		}

		public void notifyDegenerateColumns(ArrayList<Integer> degenerateColumns) {
			degenerate_columns = degenerateColumns;
		}

		public void setCriteriaNames(String[] criteria_names) {
			this.criteria_names = criteria_names;
		}

		public void setAlternativeNames(String[] alternative_names) {
			this.alternative_names = alternative_names;
		}
	}

	/**
	 * Perform an EVAMIX concordance analysis of a passed set of alternatives according to
	 * a set of criteria weights. The result object contains the ranking of the alternative
	 * that reflects the concordance calculated.
	 * 
	 * @param input data matrix; each row is an alternative, all the criteria values are
	 * 		given in each column.
	 * @param criteria_weights user weighting of criteria.
	 * @param criteria_types for each criterion pass "Ordinal", "Binary" or "Ratio". Only
	 * 		ratio is considered quantitative.
	 * @param criteria_cost_benefit array of boolean, one per criterion, pass true if benefit
	 * 	and false if cost. Can be null, in which case all criteria are assumed to be benefit.
	 * @param alternative_names Pass an array of names to identify each alternative. If 
	 * 		null is passed, alternative names are generated.
	 * @param criteria_names Pass an array of names to identify each criterion. If null, 
	 * 		criteria names are generated.
	 * 
	 * @return A result object that contains the results of the analysis. Note that criteria
	 * 		will be dropped if all alternatives have the same value for them, so don't assume
	 * 		that the input matrix in the result object or any other of the input data 
	 * 		contains the same data as the original ones passed. Alternatives will not be
	 * 		dropped, so only the final number of criteria may differ. All other criteria
	 * 		arrays (including the descriptions) are modified accordingly in the output.
	 * 
	 * @throws ThinklabException in case of invalid inputs.
	 */
	public static Results run(double[][] input, double[] criteria_weights,
			String[] criteria_types, boolean[] criteria_cost_benefit,
			String[] alternative_names, String[] criteria_names)
			throws ThinklabException {

		if (input.length < 1)
			throw new ThinklabValidationException(
					"Evamix analysis cannot be performed: "
							+ "no alternatives in input");

		if (alternative_names == null) {
			alternative_names = new String[input.length];
			for (int i = 0; i < input.length; i++) {
				alternative_names[i] = "Alternative #" + i;
			}
		}

		if (criteria_names == null) {
			criteria_names = new String[criteria_weights.length];
			for (int i = 0; i < criteria_weights.length; i++) {
				criteria_names[i] = "Criterion #" + i;
			}
		}

		/**
		 * Removing criteria and values where all observations are the same,
		 * only giving a warning and raising exceptions if no column remains.
		 */
		ArrayList<Integer> degenerateColumns = new ArrayList<Integer>();

		for (int col = 0; col < input[0].length; col++) {

			double first_val = input[0][col];
			boolean same = true;
			for (int i = 1; i < input.length; i++) {
				if (input[i][col] != first_val) {
					same = false;
					break;
				}
			}
			if (same) {

				log.warn(criteria_names[col] + " (#" +  col + ")"
						+ " has the same value in all alternatives "
						+ "and has been dropped from the analysis");

				degenerateColumns.add(col);
			}
		}

		if (degenerateColumns.size() > 0) {

			if ((input.length - degenerateColumns.size()) < 1)
				throw new ThinklabValidationException(
						"Evamix analysis cannot be performed: "
						+ "all alternatives are the same");

			input = removeColumns(input, degenerateColumns);
			criteria_weights = removeElements(criteria_weights,
					degenerateColumns);
			criteria_types = removeElements(criteria_types, degenerateColumns);
			criteria_names = removeElements(criteria_names, degenerateColumns);
			criteria_cost_benefit = removeElements(criteria_cost_benefit,
					degenerateColumns);
		}

		Results ret = new Results(input, criteria_weights, criteria_types,
				criteria_cost_benefit);

		ret.setCriteriaNames(criteria_names);
		ret.setAlternativeNames(alternative_names);
		
		ret.notifyDegenerateColumns(degenerateColumns);

		ret.num_criteria = input[0].length;
		ret.num_alternatives = input.length;
		int num_crit_weights = criteria_weights.length;

		if (ret.num_criteria != num_crit_weights)
			throw new ThinklabValidationException(
					"evamix: number of criteria in input does not match number of criterion weights");

		/*
		 * Get lists describing which columns (criteria) in the input matrix are
		 * quantitative and which are qualitative
		 */
		Pair<Collection<Integer>, Collection<Integer>> qqc = getCriteriaTypeLists(criteria_types);

		ret.num_qual_criteria = qqc.getFirst().size();
		ret.num_quant_criteria = qqc.getSecond().size();

		/*
		 * Standardize weights into crit_weights itself
		 */
		criteria_weights = standardizeWeights(criteria_weights);

		/*
		 * If cost ratio criterion then values need to be 'flipped' so that
		 * lower values will score better than higher values in_matrix =
		 * self.flip_cost_ratio_criteria_values(in_matrix, quant_cols, crit_bc)
		 */
		input = flipCostRatioCriteriaValues(input, qqc.getSecond(),
				criteria_cost_benefit);

		// standardize quantitative values
		input = standardizeQuantitativeValues(input, qqc.getSecond());

		ret.quant_impact_matrix = generateQuantitativeImpactMatrix(input,
				criteria_weights, qqc.getSecond());

		double quant_abs_sum = absoluteSum(ret.quant_impact_matrix);

		double[][] quant_final_matrix = generateQuantitativeFinalMatrix(
				ret.quant_impact_matrix, quant_abs_sum, ret);

		ret.qual_impact_matrix = generateQualitativeImpactMatrix(input,
				criteria_weights, qqc.getFirst());

		double qual_abs_sum = absoluteSum(ret.qual_impact_matrix);

		double[][] qual_final_matrix = generateQualitativeFinalMatrix(
				ret.qual_impact_matrix, qual_abs_sum, ret);

		ret.evamix_matrix = generateFinalMatrix(quant_final_matrix,
				qual_final_matrix, criteria_weights, qqc.getSecond(), qqc
						.getFirst());

		ret.evamix_scores = generateFinalScores(ret.evamix_matrix);

		return ret;
	}

	private static boolean[] removeElements(boolean[] input,
			ArrayList<Integer> degenerateColumns) {

		if (input == null)
			return null;

		int finalCriteria = input.length - degenerateColumns.size();
		boolean[] ret = new boolean[finalCriteria];

		int col = 0;
		for (int i = 0; i < input.length; i++) {
			if (!degenerateColumns.contains(i)) {
				ret[col] = input[i];
				col++;
			}

		}
		return ret;
	}

	private static String[] removeElements(String[] input,
			ArrayList<Integer> degenerateColumns) {

		int finalCriteria = input.length - degenerateColumns.size();
		String[] ret = new String[finalCriteria];

		int col = 0;
		for (int i = 0; i < input.length; i++) {
			if (!degenerateColumns.contains(i)) {
				ret[col] = input[i];
				col++;
			}
		}
		return ret;
	}

	private static double[] removeElements(double[] input,
			ArrayList<Integer> degenerateColumns) {

		int finalCriteria = input.length - degenerateColumns.size();
		double[] ret = new double[finalCriteria];

		int col = 0;
		for (int i = 0; i < input.length; i++) {
			if (!degenerateColumns.contains(i)) {
				ret[col] = input[i];
				col++;
			}
		}
		return ret;
	}

	private static double[][] removeColumns(double[][] input,
			ArrayList<Integer> degenerateColumns) {

		int finalCriteria = input[0].length - degenerateColumns.size();

		double[][] ret = new double[input.length][finalCriteria];

		for (int i = 0; i < input.length; i++) {
			int col = 0;
			for (int j = 0; j < input[0].length; j++) {
				if (degenerateColumns.contains(j))
					continue;
				ret[i][col] = input[i][j];
				col++;
			}
		}

		return ret;
	}

	private static double absoluteSum(double[][] matrix) {

		double abs_sum = 0.0;
		for (int i = 0; i < matrix.length; i++) {
			for (int j = 0; j < matrix.length; j++) {
				abs_sum += Math.abs(matrix[i][j]);
			}
		}

		return abs_sum;
	}

	/**
	 * Constructs pairwise quantitative impact matrix
	 */
	static double[][] generateQuantitativeImpactMatrix(double[][] in_matrix,
			double[] crit_weights, Collection<Integer> quant_cols) {

		int dim = in_matrix.length;

		double[][] mat = new double[dim][dim];

		for (int i = 0; i < dim; i++) {
			for (int j = 0; j < dim; j++) {

				if (i != j) {

					// calculate sum(N1, N2, ...) where Nx= weight*(stdA-stdB)
					// for each pair of alternatives A and B for each
					// alternatives
					double sum = 0.0;
					for (int k : quant_cols) {

						double crit_weight = crit_weights[k];
						double std_val_A = in_matrix[i][k];
						double std_val_B = in_matrix[j][k];
						sum += crit_weight * (std_val_A - std_val_B);
					}

					mat[i][j] = sum;

				}
			}
		}

		return mat;
	}

	/**
	 * Construct pair-wise qualitative impact matrix; compares qualitative
	 * criteria for each alternative
	 */
	public static double[][] generateQualitativeImpactMatrix(
			double[][] in_matrix, double[] crit_weights,
			Collection<Integer> qual_cols) {

		int dim = in_matrix.length;

		double[][] impact_matrix = new double[dim][dim];

		for (int i = 0; i < dim; i++) {
			for (int j = 0; j < dim; j++) {

				if (i != j) {

					double sum_greater = 0.0;
					double sum_less = 0.0;

					for (int k : qual_cols) {

						double val_A = in_matrix[i][k];
						double val_B = in_matrix[j][k];
						double crit_weight = crit_weights[k];

						if (val_A > val_B) {
							sum_greater += crit_weight;
						} else if (val_A < val_B) {
							sum_less += crit_weight;
						}
					}

					impact_matrix[i][j] = sum_greater - sum_less;
				}
			}
		}

		return impact_matrix;
	}

	static double[][] generateQuantitativeFinalMatrix(
			double[][] quant_impact_matrix, double quant_abs_sum, Results result) {

		int dim = quant_impact_matrix.length;
		double[][] final_matrix = new double[dim][dim];

		// check for no qualitative criteria
		// TODO: shouldn't even get here if there not qual criteria (so
		// shouldn't pass result)
		if (result.num_quant_criteria > 0) {

			for (int i = 0; i < dim; i++) {
				for (int j = 0; j < dim; j++) {
					final_matrix[i][j] = quant_impact_matrix[i][j]
							/ quant_abs_sum;
				}
			}
		}

		return final_matrix;
	}

	/**
	 * TODO check - this and the previous are at the very least redundant, and
	 * possibly wrong.
	 * 
	 * @param qual_impact_matrix
	 * @param qual_abs_sum
	 * @param result
	 * @return
	 */
	static double[][] generateQualitativeFinalMatrix(
			double[][] qual_impact_matrix, double qual_abs_sum, Results result) {

		int dim = qual_impact_matrix.length;
		double[][] final_matrix = new double[dim][dim];

		// check for no qualitative criteria
		// TODO: shouldn't even get here if there not qual criteria (so
		// shouldn't pass result)
		if (result.num_qual_criteria > 0) {

			for (int i = 0; i < dim; i++) {
				for (int j = 0; j < dim; j++) {
					final_matrix[i][j] = qual_impact_matrix[i][j]
							/ qual_abs_sum;
				}
			}
		}

		return final_matrix;
	}

	private static double[][] deepCopy(double[][] input) {

		double[][] ret = new double[input.length][input[0].length];

		for (int i = 0; i < input.length; i++) {
			for (int j = 0; j < input[0].length; j++) {
				ret[i][j] = input[i][j];
			}
		}

		return ret;
	}

	/**
	 * Calculate final Evamix matrix by combining quantitative and qualitative
	 * final results.
	 * 
	 * @param quan_matrix
	 * @param qual_matrix
	 * @param quant_cols
	 * @param qual_cols
	 * @return
	 * @throws ThinklabException
	 */
	static double[][] generateFinalMatrix(double[][] quant_matrix,
			double[][] qual_matrix, double[] crit_weights,
			Collection<Integer> quant_cols, Collection<Integer> qual_cols)
			throws ThinklabException {

		int quant_h = quant_matrix.length;
		int qual_h = qual_matrix.length;

		if (quant_h > 1) {
			quant_h = quant_matrix[0].length;
		} else {
			throw new ThinklabValidationException(
					"Evamix: quantitative matrix has no dimension");
		}

		if (qual_h > 1) {
			qual_h = qual_matrix[0].length;
		} else {
			throw new ThinklabValidationException(
					"Evamix: qualitative matrix has no dimension");
		}

		if (quant_h != qual_h)
			throw new ThinklabValidationException(
					"Evamix: impact matrices are of different size");

		double sum_quant_weights = 0.0;
		double sum_qual_weights = 0.0;

		for (int k : quant_cols)
			sum_quant_weights += crit_weights[k];

		for (int k : qual_cols)
			sum_qual_weights += crit_weights[k];

		double[][] final_matrix = new double[quant_h][quant_h];

		for (int i = 0; i < quant_h; i++) {
			for (int j = 0; j < quant_h; j++)
				final_matrix[i][j] = 
					(quant_matrix[i][j] * sum_quant_weights) +
					(qual_matrix[i][j] * sum_qual_weights);
		}

		return final_matrix;
	}

	static double[] generateFinalScores(double[][] final_matrix) {

		double final_scores[] = new double[final_matrix.length];

		for (int i = 0; i < final_matrix.length; i++) {
			for (int j = 0; j < final_matrix[i].length; j++)
				final_scores[i] += final_matrix[i][j];
		}

		// standardize to sum to 1.0
		double min = min(final_scores);
		double max = max(final_scores);

		double sum = 0.0;
		for (int i = 0; i < final_scores.length; i++) {
			final_scores[i] = (final_scores[i] - max + min);
			sum += final_scores[i];
		}
		for (int i = 0; i < final_scores.length; i++) {
			final_scores[i] /= sum;
		}
		
		return final_scores;
	}

	/*
	 * Return in_matrix with just the quantitative values
	 * normalized
	 */
	private static double[][] standardizeQuantitativeValues(double[][] input,
			Collection<Integer> quant_cols) {

		double[][] new_matrix = deepCopy(input);

		for (int i = 0; i < input.length; i++) {
			for (int j : quant_cols) {

				double[] crit_vals = getCriteriaByColumn(input, j);

				double val = input[i][j];
				double min_val = min(crit_vals);
				double max_val = max(crit_vals);
				val = (val - min_val) / (max_val - min_val);
				new_matrix[i][j] = val;
			}
		}

		return new_matrix;
	}

	public static double min(double[] input) {
		double ret = input[0];
		for (int i = 1; i < input.length; i++)
			if (input[i] < ret)
				ret = input[i];
		return ret;
	}

	public static double max(double[] input) {
		double ret = input[0];
		for (int i = 1; i < input.length; i++)
			if (input[i] > ret)
				ret = input[i];
		return ret;
	}

	private static double flip(double val, double max_val) {
		return (val * -1.0) + max_val;
	}

	private static double[] getCriteriaByColumn(double[][] input, int j) {

		double col_vals[] = new double[input.length];
		for (int i = 0; i < input.length; i++) {
			col_vals[i] = input[i][j];
		}
		return col_vals;
	}

	private static double[][] flipCostRatioCriteriaValues(double[][] input,
			Collection<Integer> quant_cols, boolean[] criteria_cost_benefit) {

		double[][] new_matrix = deepCopy(input);

		for (int col : quant_cols) {

			if (criteria_cost_benefit != null && !criteria_cost_benefit[col]) {

				double[] col_vals = getCriteriaByColumn(input, col);
				double max_val = max(col_vals);

				/*
				 * flip the values
				 */
				for (int i = 0; i < col_vals.length; i++) {
					double new_val = flip(col_vals[i], max_val);
					new_matrix[i][col] = new_val;
				}
			}
		}

		return new_matrix;
	}

	/*
	 * The higher the weight value the less important it is, the lower its
	 * standardized weight score will be. The set of standardized scores sum to
	 * 1.
	 */
	private static double[] standardizeWeights(double[] weights) {

		double max_weight = max(weights);
		double[] new_weights = new double[weights.length];
		double[] std_weights = new double[weights.length];

		for (int i = 0; i < weights.length; i++) {
			double new_weight = (weights[i] * -1.0) + max_weight + 1.0;
			new_weights[i] = new_weight;
		}

		double new_sum = 0;
		for (double d : new_weights) {
			new_sum += d;
		}

		for (int i = 0; i < weights.length; i++) {
			double weight = new_weights[i];
			std_weights[i] = weight / new_sum;
		}

		return std_weights;
	}

	private static Pair<Collection<Integer>, Collection<Integer>> getCriteriaTypeLists(
			String[] criteria_types) {

		ArrayList<Integer> qual = new ArrayList<Integer>();
		ArrayList<Integer> quan = new ArrayList<Integer>();

		int i = 0;
		for (String s : criteria_types) {
			if (s.equals("Ratio"))
				quan.add(i);
			else if (s.equals("Ordinal") || s.equals("Binary")) {
				qual.add(i);
			}
			i++;
		}

		return new Pair<Collection<Integer>, Collection<Integer>>(qual, quan);
	}

	public static void main(String args[]) {

		// India 1 input, rows-criteria, rols-alternatives
		double[][] input1 = {
				{4,3,4,2,4,3,3,2,2,2,3,4,2,1,1,3,2,3,37900,0},
				{3,2,3,3,4,2,3,2,2,2,3,4,3,2,1,3,3,3,3000,15000},
				{3,3,3,4,4,4,3,4,2,2,2,4,4,1,1,3,2,3,240,256},
				{4,4,3,4,3,4,3,4,2,2,2,4,4,1,1,3,2,3,12231,5000},
				{2,2,3,4,4,4,3,4,2,2,2,4,4,1,1,3,3,1,25,700},
				{3,3,3,2,2,3,1,2,2,3,3,4,2,1,1,3,3,3,19700,15700},
				{3,3,2,2,2,3,2,2,2,2,2,4,3,1,1,3,2,3,119648,300},
				{3,2,3,2,2,3,1,2,2,3,3,4,3,1,1,3,3,3,14875,15700}};

		double[] crit_weights1 = {1,1,2,1,1,1,4,2,2,1,2,2,1,1,3,3,4,3,3,1};

		double[][] input2 = {
				{ 4, 4, 4, 4, 4, 4, 1, 4, 3, 4, 4, 4, 4, 2, 3, 5, 6, 4, 2, 5 },
				{ 4, 4, 4, 4, 4, 40, 1, 4, 3, 4, 4, 6, 4, 2, 3, 3, 3, 4, 30, 6 },
				{ 4, 5, 4, 4, 4, 4, 2, 4, 1, 4, 4, 4, 4, 7, 3, 3, 3, 4, 4, 7 },
				{ 4, 4, 4, 4, 4, 2, 1, 4, 3, 9, 2, 5, 3, 3, 2, 2, 4, 6, 5, 8 },
				{ 4, 4, 4, 2, 7, 4, 1, 5, 3, 4, 4, 4, 4, 2, 3, 3, 3, 4, 6, 9 },
				{ 4, 4, 4, 4, 4, 4, 6, 4, 2, 4, 2, 5, 4, 2, 4, 3, 2, 3, 7, 8 },
				{ 1, 4, 4, 4, 4, 4, 1, 2, 3, 7, 4, 4, 3, 1, 3, 1, 3, 4, 8, 7 },
				{ 4, 4, 4, 4, 4, 4, 1, 4, 3, 4, 4, 4, 4, 2, 3, 3, 3, 4, 9, 6 }, };

		double[] crit_weights2 = { 
				1, 1, 1, 1, 1, 
				3.2, 1, 1, 1, 1, 
				1, 1, 1.7, 1, 1, 
				1, 1, 1, 5.4, 1 };

		String[] crit_types = { 
				"Ordinal", "Ordinal", "Ordinal", "Ordinal", "Ordinal", 
				"Ordinal", "Ordinal", "Ordinal", "Ordinal", "Ordinal", 
				"Ordinal", "Ordinal", "Ordinal", "Ordinal", "Ordinal", 
				"Ordinal", "Ordinal", "Ordinal", "Ratio", "Ratio" };

		try {
			Results res = 
				run(input1, crit_weights1, crit_types, null, null, null);
			res.dump();
		} catch (ThinklabException e) {
			e.printStackTrace();
		}

	}
}
