;; Bindings to SSJ - for now, only distribution creator functions.
(ns corescience)

;; the following still need to be added. Trivial, but boring.
;		ANDERSON_DARLING,
;		BETA,
;		BETA_SYMMETRIC,
;		CAUCHY,
;		CHI,
;		CHI_SQUARE,
;		CRAMER_VONMISES,
;		ERLANG,
;		EXPONENTIAL,
;		EXTREME_VALUE,
;		FATIGUE_LIFE,
;		FISHER_F,
;		FOLDED_NORMAL,
;		GAMMA,
;		HALF_NORMAL,
;		HYPERBOLIC_SECANT,
;		INVERSE_GAUSSIAN,
;		KOLMOGOROV_SMIRNOV,
;		LAPLACE,
;		LOGISTIC,
;		LOG_LOGISTIC,
;		LOG_NORMAL,
;		NAKAGAMI,
;		NORMAL_INVERSE,
;		PARETO,
;		PASCAL,
;		PEARSON5,
;		PEARSON6,
;		PIECEWISE_LINEAR_EMPIRICAL,
;		POWER,
;		RAYLEIGHT,
;		STUDENT,
;		TRIANGULAR,
;		TRUNCATED,
;		WATSON_G,
;		WATSON_U,
;		WEIBULL,
;		BINOMIAL,
;		EMPIRICAL,
;		GEOMETRIC,
;		HYPERGEOMETRIC,

(defn uniform 
  ([] (umontreal.iro.lecuyer.probdist.NormalDist. )) 
  ([x1 x2] (umontreal.iro.lecuyer.probdist.NormalDist. x1 x2))) 

(defn gaussian 
  [mean std]  
  (umontreal.iro.lecuyer.probdist.NormalDist. mean std)) 

(defn logarithmic 
  [theta]  
  (umontreal.iro.lecuyer.probdist.LogarithmicDist. theta)) 

(defn negative-binomial
  [gamma p]
  (umontreal.iro.lecuyer.probdist.NegativeBinomialDist. gamma p))

(defn poisson
  [lambda]
  (umontreal.iro.lecuyer.probdist.PoissonDist. lambda))
