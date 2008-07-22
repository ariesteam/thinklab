package org.integratedmodelling.mca;

public class Evamix {
	
	/**
	 * Evamix results are grouped in an object of this kind.
	 * 
	 * @author Ferdinando Villa
	 *
	 */
	public class Results {
		
	}
	
//	#===============================================================================
//		# Delphos - a decision-making tool for community-based marine conservation.
//		# 
//		# @copyright	2007 Ecotrust
//		# @author		Tim Welch
//		# @contact		twelch at ecotrust dot org
//		# @license		GNU GPL 2 
//		# 
//		# This program is free software; you can redistribute it and/or 
//		# modify it under the terms of the GNU General Public License as published by
//		# the Free Software Foundation; either version 2 of the License, or
//		# (at your option) any later version.  The full license for this distribution
//		# has been made available in the file LICENSE.txt
//		#
//		# $Id$
//		#
//		# @summary - implementation of the Evamix MCA algorithm
//		#===============================================================================
//
//		from delphos_exceptions import *
//		from util.common_functions import *
//		import csv
//		from copy import deepcopy
//
//
	 Results run(double[][] input, double[] criteria_weights, int[] criteria_types, boolean[] criteria_cost_benefit) {

	
//		    def do_analysis(self, in_matrix, crit_weights, crit_types, crit_bc):
//		        """Performs multicriteria analysis using the Evamix algorithm
//		        
//		        Returns a list containing a list of final scores and a list of
//		        intermediate datasets generated during the analysis.
//		        [final_scores, [quant_impact_matrix, qual_impact_matrix, final_matrix]]
//		        """
//		        if not in_matrix:
//		            raise DelphosError, "No in_matrix matrix"
//		        if type(in_matrix) is not type([]):
//		            raise DelphosError, "Bad in_matrix matrix"
//		        if not crit_weights:
//		            raise DelphosError, "No criteria weights given"
//		        #print "crit weights: "+str(crit_weights)
//		        if type(crit_weights) is not type([]):
//		            raise DelphosError, "Expected list of crit_weights"
//		        if not crit_types:
//		            raise DelphosError, "No criteria types given"
//		        if type(crit_types) is not type([]):
//		            raise DelphosError, "No criteria types given"
//		        if not crit_bc:
//		            raise DelphosError, "No criteria cost/benefits given"
//		        if type(crit_bc) is not type([]):
//		            raise DelphosError, "No criteria cost/benefits given"        
//
//		        if len(in_matrix) < 1:
//		            raise DelphosError, "in_matrix contains no data"
//
//		        if self.debug:
//		            print "in matrix:"
//		            for row in in_matrix:
//		                print row
//
//		            print "crit weights:"
//		            print crit_weights
//
//		            print "crit types:"
//		            print crit_types
//		            
//		            print "cost benefits:"
//		            print crit_bc
//		        
//		        self.num_criteria = len(in_matrix[0])
//		        self.num_alternatives = len(in_matrix)
//		        num_crit_weights = len(crit_weights)
//		        if self.num_criteria is not num_crit_weights:
//		            raise DelphosError, "Number of criteria in in_matrix ("+str(self.num_criteria)+") does not match number of criteria weights given ("+str(num_crit_weights)+")"
//		  
//		        #Get lists describing which columns (criteria) in in_matrix are quantitative and which are qualitative
//		        (quant_cols, qual_cols) = self.gen_crit_type_lists(crit_types)
//		        self.num_qual_criteria = len(qual_cols)
//		        self.num_quant_criteria = len(quant_cols)
//		        if self.debug:
//		            print "\nQuantitative columns:"
//		            print quant_cols
//		            print "\nQualitative columns:"
//		            print qual_cols
//
//		        #Check if for any quant criteria, the values are the same for all alternatives
//		        if self.num_quant_criteria > 0:
//		            for j in quant_cols:
//		                first_val = in_matrix[0][j]
//		                same = True
//		                for i in range(len(in_matrix)):
//		                    if in_matrix[i][j] != first_val:
//		                        same = False
//		                if same:
//		                    raise DelphosError, "The quantitative values in row "+str(j)+ " are all the same.  This is not supported.  At least one of the values must differ from the rest for each row."
//
//		        #Check if for any qualitative criteria, the values are the same for all alternatives
//		        if self.num_qual_criteria > 0:
//		            same = True
//		            for j in qual_cols:
//		                first_val = in_matrix[0][j]
//		                for i in range(len(in_matrix)):
//		                    if in_matrix[i][j] != first_val:
//		                        same = False
//		            if same:
//		                raise DelphosError, "The criteria values are the same for each alternative.  This is not supported.  The values on at least one row must differ in their value."
//		        
//		        #Standardize weights
//		        self.standardize_weights(crit_weights)
//		        if self.debug:
//		            print "\nStandardized weights: "
//		            print crit_weights
//
//		        # If cost ratio criterion then values need to be 'flipped' so that lower 
//		        # values will score better than higher values
//		        in_matrix = self.flip_cost_ratio_criteria_values(in_matrix, quant_cols, crit_bc)
//		            
//		        in_matrix = self.standardize_quantitative_values(in_matrix, quant_cols)
//		        if self.debug:
//		            print "\nStandardized quant values"
//		            for row in in_matrix:
//		                print row
//		    
//		        #Generate quantitative impact matrix
//		        quant_impact_matrix = self.gen_quant_impact_matrix(in_matrix, crit_weights, quant_cols)
//		        if self.debug:            
//		            print "\nQuantitative impact matrix:"
//		            for row in quant_impact_matrix:
//		                print row
//
//		        #Compute absolute value of quantitative impact matrix
//		        quant_abs_sum = self.absolute_sum(quant_impact_matrix)
//		        if self.debug:
//		            print "\nAbsolute Sum of quantitative impact matrix:"
//		            print quant_abs_sum
//		        
//		        #Generate quantitative final matrix
//		        quant_final_matrix = self.gen_quant_final_matrix(quant_impact_matrix, quant_abs_sum)
//		        if self.debug:
//		            print "\nFinal quantitative matrix"
//		            for row in quant_final_matrix:
//		                print row 
//		        
//		        #Generate qualitative impact matrix
//		        qual_impact_matrix = self.gen_qual_impact_matrix(in_matrix, crit_weights, qual_cols)
//		        if self.debug:
//		            print "\nQualitative impact matrix:"
//		            for row in qual_impact_matrix:
//		                print row
//		        
//		        #Compute absolute sum of qualitative impace matrix
//		        qual_abs_sum = self.absolute_sum(qual_impact_matrix)
//		        if self.debug:
//		            print "\nAbsolute Sum of qualitative impact matrix:"
//		            print qual_abs_sum
//		               
//		        #Generate qualitative final matrix
//		        qual_final_matrix = self.gen_qual_final_matrix(qual_impact_matrix, qual_abs_sum)
//		        if self.debug:
//		            print "\nFinal qualitative matrix:"
//		            for row in qual_final_matrix:
//		                print row
//		        
//		        final_matrix = self.gen_final_matrix(quant_final_matrix, qual_final_matrix, crit_weights, quant_cols, qual_cols)
//		        if self.debug:
//		            print "\nFinal matrix:"
//		            for row in final_matrix:
//		                print row
//		        
//		        final_scores = self.gen_final_scores(final_matrix)
//		        if self.debug:
//		            print "\nFinal scores:"
//		            for i in range(len(final_scores)):
//		                print str(i)+": "+str(final_scores[i])
//		                
//		        return [final_scores, [crit_weights, quant_impact_matrix, qual_impact_matrix, final_matrix]]
	 
		 return null;
	 }
	 
//		    def standardize_weights(self, weights):
//		        """Standardizes a set of criteria weights, modifies the list given, returns nothing
//		    
//		        The higher the weight value the less important it is, the lower its 
//		        standardized weight score will be.  The set of standardized scores sum to 1.
//		        """
//		        max_weight = max(weights)
//		        new_weights = []
//		        std_weights = []
//		        
//		        for i in range(len(weights)):
//		            new_weight = (weights[i]*-1)+max_weight+1
//		            new_weights.append(new_weight)
//		        new_sum = sum(new_weights)
//		        
//		        for i in range(len(new_weights)):
//		            weight = new_weights[i]
//		            std_weight = float(weight)/float(new_sum)
//		            weights[i] = std_weight
//
//		    def flip_cost_ratio_criteria_values(self, in_matrix, quant_cols, crit_bc):
//		        flip = lambda val, max_val: (val*-1)+max_val
//		        
//		        #Copy in_matrix, each iteration needs the untouched original.
//		        new_matrix = deepcopy(in_matrix)
//
//		        for col in quant_cols:
//		            if crit_bc[col] == 'C':
//		                col_vals = self.get_criteria_by_col(in_matrix, col)
//		                max_val = max(col_vals)
//		                #Flip the values
//		                for i in range(len(col_vals)):
//		                    new_val = flip(col_vals[i], max_val)
//		                    #Update the original value
//		                    new_matrix[i][col] = new_val
//		        
//		        return new_matrix
//
//		    def standardize_quantitative_values(self, in_matrix, quant_cols):
//		        """Standardizes all quantitative values.
//		        
//		        Return a 2D list which is in_matrix with just the quant values modified"""
//		        #Copy in_matrix, each iteration needs the untouched original.
//		        new_matrix = deepcopy(in_matrix)
//		        #Go right to the quantitative columns for each alternative
//		        for i in range(len(in_matrix)):
//		            for j in quant_cols:
//		                #Get all values for current quant column
//		                crit_vals = self.get_criteria_by_col(in_matrix, j)                    
//		                val = in_matrix[i][j]
//		                min_val = min(crit_vals)
//		                max_val = max(crit_vals)
//		                val = float(val-min_val)/float(max_val-min_val)
//		                new_matrix[i][j] = val
//		        return new_matrix
//
//		    def gen_quant_impact_matrix(self, in_matrix, crit_weights, quant_cols):
//		        """Construct pair-wise quantitative impact matrix
//		        
//		        Compares quantitative criteria for each alternative"""
//		        dim = len(in_matrix)
//		        mat = initialize_float_array(dim, dim)
//		        #Pair-wise calculations
//		        for i in range(dim):
//		            for j in range(dim):
//		                #Don't compare alternative to itself
//		                if i is not j:
//		                    #calculate sum(N1, N2, ...) where Nx= weight*(stdA-stdB) 
//		                    #for each pair of alternatives A and B for each alternatives
//		                    Ni_vals = []
//		                    for k in quant_cols:
//		                        #print "i:"+str(i)+" j:"+str(j)+" k:"+str(k)
//		                        crit_weight = float(crit_weights[k])
//		                        #print "crit weight: "+str(crit_weight)
//		                        std_val_A = float(in_matrix[i][k])
//		                        #print "std_val_A: "+str(std_val_A)
//		                        std_val_B = float(in_matrix[j][k])
//		                        #print "std_val_B: "+str(std_val_B)
//		                        Ni = crit_weight * (std_val_A - std_val_B)
//		                        #print "Ni: "+str(Ni)
//		                        Ni_vals.append(Ni)
//		                    #print "Sum: "+str(sum(Ni_vals))
//		                    mat[i][j] = sum(Ni_vals)
//		        return mat
//
//		    def gen_qual_impact_matrix(self, in_matrix, crit_weights, qual_cols):
//		        """Construct pair-wise quaLitative impact matrix
//		        
//		        Compares qualitative criteria for each alternative"""
//		        dim = len(in_matrix)
//		        impact_matrix = initialize_float_array(dim, dim)
//		        for i in range(dim):
//		            for j in range(dim):
//		                crit_type = None
//		                bc_type = None
//		                #Don't compare alternative to itself
//		                if i is not j:
//		                    sum_greater = 0.0
//		                    sum_less = 0.0
//		                    for k in qual_cols:
//		                        #print "i:"+str(i)+" j:"+str(j)+" k:"+str(k)
//		                        val_A = float(in_matrix[i][k])
//		                        #print "val_A: "+str(val_A)
//		                        val_B = float(in_matrix[j][k])
//		                        #print "val_B: "+str(val_B)
//		                        crit_weight = float(crit_weights[k])
//		                        #print "crit weight: "+str(crit_weight)
//		                        
//		                        if (val_A > val_B):
//		                            sum_greater += crit_weight
//		                            #print "sum_greater: "+str(sum_greater)
//		                        elif (val_A < val_B):
//		                            sum_less += crit_weight
//		                            #print "sum_less: "+str(sum_less)
//		                            
//		                    #print "total sum greater: "+str(sum_greater)
//		                    #print "total sum less: "+str(sum_less)
//		                    impact_matrix[i][j] = sum_greater - sum_less
//		                    
//		                    #print "cell val: "+str(impact_matrix[i][j])         
//		        return impact_matrix
//
//		    def gen_quant_final_matrix(self, quant_impact_matrix, quant_abs_sum):
//		        dim = len(quant_impact_matrix)
//		        final_matrix = initialize_float_array(dim, dim)
//
//		        #Check for no qualitative criteria
//		        #TODO: shouldn't even get here if there not qual criteria
//		        if self.num_quant_criteria > 0:       
//		            for i in range(dim):
//		                for j in range(dim):
//		                    #print quant_impact_matrix[i][j]
//		                    #print quant_abs_sum
//		                    final_matrix[i][j] = quant_impact_matrix[i][j]/quant_abs_sum
//		        return final_matrix
//
//		    def gen_qual_final_matrix(self, qual_impact_matrix, qual_abs_sum):
//		        dim = len(qual_impact_matrix)
//		        final_matrix = initialize_float_array(dim, dim)
//		        #Check for no qualitative criteria
//		        #TODO: shouldn't even get here if there not qual criteria
//		        if self.num_qual_criteria > 0:
//		            for i in range(dim):
//		                for j in range(dim):
//		                    final_matrix[i][j] = qual_impact_matrix[i][j]/qual_abs_sum
//		        return final_matrix
//
//		    def gen_final_matrix(self, quant_matrix, qual_matrix, crit_weights, quant_cols, qual_cols):
//		        """Calculate final Evamix matrix
//		        
//		        The final matrix is a combination of the quantitative and qualitative matrices
//		        """
//		        quant_h = qual_h = quant_h = qual_h = 0
//		        #Validate input matrices
//		        quant_h = len(quant_matrix)
//		        if (quant_h > 1):
//		            quant_h = len(quant_matrix[0])
//		        else:
//		            raise DelphosError, "First matrix has no dimension"
//
//		        qual_h = len(qual_matrix)
//		        if (qual_h > 1):
//		            qual_h = len(qual_matrix[0])
//		        else:
//		            raise DelphosError, "Second matrix has no dimension"
//		        
//		        if quant_h is not qual_h and quant_h is not qual_h:
//		            raise DelphosError, "Matrices are not the same dimensions"
//
//		        #Calculate sum of all quantitative and qualitative criteria weights
//		        sum_quant_weights = 0.0
//		        sum_qual_weights = 0.0
//		        for k in quant_cols:
//		            sum_quant_weights += crit_weights[k]
//		        for k in qual_cols:
//		            sum_qual_weights += crit_weights[k]
//		        #print "sum quantitative weights: "+str(sum_quant_weights)
//		        #print "sum qualitative weights: "+str(sum_qual_weights)
//		         
//		        final_matrix = initialize_float_array(quant_h, quant_h)
//		        for i in range(quant_h):
//		            for j in range(quant_h):
//		                final_matrix[i][j] = quant_matrix[i][j]*sum_quant_weights + qual_matrix[i][j]*sum_qual_weights
//		                #print "cell value: "+str(final_matrix[i][j])
//		        return final_matrix
//
//		    def gen_final_scores(self, final_matrix):
//		        final_scores = [] 
//		        for row in final_matrix:
//		            final_scores.append(sum(row))
//		        return final_scores
//
//		    def gen_crit_type_lists(self, crit_types):
//		        """Given a list of criteria types (eg. "Binary", "Ordinal", "Ratio"), 
//		        returns a tuple containing two lists. 1 of indices of quantitative 
//		        criteria and 1 of indices of qualitative criteria.  These are indices 
//		        into the crit_types (or in_matrix) list allowing for quick retrieval 
//		        of criteria of one type or the other during the analysis process.
//		        """   
//		        quant_list = []
//		        qual_list = []
//		        for i in range(len(crit_types)):
//		            cur_type = crit_types[i]
//		            if cur_type == "Ratio":
//		                quant_list.append(i)
//		            elif cur_type == "Ordinal" or cur_type == "Binary":
//		                qual_list.append(i)
//		        return (quant_list, qual_list)
//
//		    def get_criteria_by_col(self, in_matrix, col):
//		        """Traverses in_matrix horizontally getting a list of values for the given column""" 
//		        col_vals = []
//		        for i in range(len(in_matrix)):
//		            col_vals.append(in_matrix[i][col])
//		        return col_vals
//		    
//		    def absolute_sum(self, matrix):
//		        abs_sum = 0.0
//		        for i in range(len(matrix)):
//		            for j in range(len(matrix)):
//		                abs_sum += abs(matrix[i][j])
//		        return abs_sum
//
//		if __name__ == "__main__":
//		    #India 1 input, rows-criteria, rols-alternatives
//		#    input = [
//		#        [4,3,4,2,4,3,3,2,2,2,3,4,2,1,1,3,2,3,37900,0],
//		#        [3,2,3,3,4,2,3,2,2,2,3,4,3,2,1,3,3,3,3000,15000],
//		#        [3,3,3,4,4,4,3,4,2,2,2,4,4,1,1,3,2,3,240,256],
//		#        [4,4,3,4,3,4,3,4,2,2,2,4,4,1,1,3,2,3,12231,5000],
//		#        [2,2,3,4,4,4,3,4,2,2,2,4,4,1,1,3,3,1,25,700],
//		#        [3,3,3,2,2,3,1,2,2,3,3,4,2,1,1,3,3,3,19700,15700],
//		#        [3,3,2,2,2,3,2,2,2,2,2,4,3,1,1,3,2,3,119648,300],
//		#        [3,2,3,2,2,3,1,2,2,3,3,4,3,1,1,3,3,3,14875,15700]
//		#    ]
//
//		    input = [
//		        [4, 4, 4, 4, 4, 4, 1, 4, 3, 4, 4, 4, 4, 2, 3, 3, 3, 4, 2, 5],
//		        [4, 4, 4, 4, 4, 4, 1, 4, 3, 4, 4, 4, 4, 2, 3, 3, 3, 4, 3, 6],
//		        [4, 4, 4, 4, 4, 4, 1, 4, 3, 4, 4, 4, 4, 2, 3, 3, 3, 4, 4, 7],
//		        [4, 4, 4, 4, 4, 4, 1, 4, 3, 4, 4, 4, 4, 2, 3, 3, 3, 4, 5, 8],
//		        [4, 4, 4, 4, 4, 4, 1, 4, 3, 4, 4, 4, 4, 2, 3, 3, 3, 4, 6, 9],
//		        [4, 4, 4, 4, 4, 4, 1, 4, 3, 4, 4, 4, 4, 2, 3, 3, 3, 4, 7, 8],
//		        [4, 4, 4, 4, 4, 4, 1, 4, 3, 4, 4, 4, 4, 2, 3, 3, 3, 4, 8, 7],
//		        [4, 4, 4, 4, 4, 4, 1, 4, 3, 4, 4, 4, 4, 2, 3, 3, 3, 4, 9, 6],
//		    ]
//
//		    #India 1 data, matches evamix spreadsheet, old delphos uses same data in different order
//		    #crit_weights = [1,1,2,1,1,1,4,2,2,1,2,2,1,1,3,3,4,3,3,1]
//		    crit_weights = [1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1]
//		    crit_types = ["Ordinal","Ordinal","Ordinal","Ordinal","Ordinal","Ordinal","Ordinal","Ordinal","Ordinal","Ordinal","Ordinal","Ordinal","Ordinal","Binary","Ordinal","Ordinal","Ordinal","Ordinal","Ratio","Ratio"]
//		     
//		    print "Input:"
//		    for i in range(len(input)):
//		        print input[i]
//		    print ""
//		    print "Weightings:"
//		    print crit_weights
//
//		    #Do analysis
//		    evamix_tool = Evamix()
//		    try:
//		        evamix_tool.do_analysis(input, crit_weights, crit_types)
//		    except DelphosError, e:
//		        print "Error: "+str(e)
}
