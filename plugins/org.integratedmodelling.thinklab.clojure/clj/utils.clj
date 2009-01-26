;; ------------------------------------------------------------------------------------------
;; Basic Thinklab utilities for Clojure
;; 
;; @author Ferdinando Villa
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