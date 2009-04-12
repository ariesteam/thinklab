;; ------------------------------------------------------------------------------------------
;; Basic Thinklab utilities for Clojure
;; 
;; @author Ferdinando Villa
;; @author Gary Johnson
;; @date 1/13/2009
;; ------------------------------------------------------------------------------------------

(ns tl)

(defn alert 
	"Pop up a window with a string in it and block until user clicks OK"
	[string]
	(. javax.swing.JOptionPane (showMessageDialog nil string)))
	
(defn uninterleave [coll]
  (loop [odds  []
         evens []
         rem   coll]
    (if (seq rem)
      (recur (conj odds  (first rem))
             (conj evens (second rem))
             (rrest rem))
      [odds evens])))
      
(defn take-pair-while
  "Returns a lazy seq of successive pairs of items from coll
   while (pred item) returns true for the first item in the pair. pred
   must be free of side-effects."
  [pred coll]
  (when (and (seq coll) (pred (first coll)))
    (lazy-cat (list (first coll) (second coll))
	      (take-pair-while pred (rrest coll)))))

(defn drop-pair-while
  "Returns a lazy seq of the items in coll starting from the first
   item for which (pred item) returns nil."
  [pred coll]
  (if (and (seq coll) (pred (first coll)))
    (recur pred (rrest coll))
    (seq coll)))

(defn split-pair-with
  "Returns a vector of [(take-pair-while pred coll) (drop-pair-while
   pred coll)]"
  [pred coll]
  [(take-pair-while pred coll) (drop-pair-while pred coll)])

(defn group-while
  "Group together all the consecutive pairs of items where (pred
   element) returns true for the first element in the pair."
  [pred coll]
  (when (seq coll)
    (let [[taken dropped] (split-pair-with pred coll)]
      (cond 
      	(empty? dropped) (list taken)
	    	(empty? taken)
	    			(lazy-cat (list (first dropped))
				    	(group-while pred (rest dropped)))
	    	:otherwise 
	    			(lazy-cat (list taken)
							(group-while pred dropped))))))

(defn group-with-following
  "Group pairs of items if their second element matches pred, else
   pair single elements with filler."
  [pred coll filler]
  (cond 
  	(empty? coll)   coll
		(empty? (rest coll)) (list (list (first coll) filler))
		:otherwise 
			(if (pred (second coll))
		     (lazy-cons 
		     		(take 2 coll)
						(group-with-following pred (rrest coll) filler))
		     (lazy-cons 
		     		(list (first coll) filler)
						(group-with-following pred (rest coll) filler)))))

(defn group-with-keywords
  "Take a seq where each element may be followed by a keyword, value
   pair and group each element with a list of its keywords if any, or
   with nil"
  [coll]
  (group-with-following seq? (group-while keyword? coll) nil))
  
(defn count-not-nil
	"Return the number of elements in the collection that are not nil"
	[coll]
	(count (filter #(not (nil? %)) coll)))
	
(defn count-nil
	"Return the number of elements in the collection that are nil"
	[coll]
	(count (filter nil? coll)))
	
(defn map-lists
	"Return the same collection as the input but with all clojure seqs translated to
	polylists."
	[coll]
	(map #(if (seq? %) (tl/listp %) %) coll))

(defn unquote-if-quoted 
	"If the argument is a list starting with quote, return the unquoted argument, otherwise return
	 the unmodified argument"
	 [arg]
	 (if (and (list? arg) (= (str (first arg)) "quote")) (eval arg) arg))