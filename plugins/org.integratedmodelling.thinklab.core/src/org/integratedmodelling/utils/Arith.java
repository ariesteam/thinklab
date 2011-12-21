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
// author:  Robert Keller
// purpose: Class Arith of polya package
//          Implements "polymorphic arithmetic"

package org.integratedmodelling.utils;

public class Arith {
	static public boolean greaterThan(Object v1, Object v2) {
		if (!(v1 instanceof Number))
			throw new IllegalArgumentException("NotNumber: " + v1);

		if (!(v2 instanceof Number))
			throw new IllegalArgumentException("NotNumber: " + v2);

		if (v1 instanceof Long) {
			if (v2 instanceof Long) {
				return ((Long) v1).longValue() > ((Long) v2).longValue();
			} else {
				return ((Long) v1).longValue() > ((Number) v2).doubleValue();
			}
		} else {
			if (v2 instanceof Long) {
				return ((Number) v1).doubleValue() > ((Long) v2).longValue();
			} else {
				return ((Number) v1).doubleValue() > ((Number) v2)
						.doubleValue();
			}
		}
	}

	static public boolean greaterThanOrEqual(Object v1, Object v2) {
		if (!(v1 instanceof Number))
			throw new IllegalArgumentException("NotNumber: " + v1);

		if (!(v2 instanceof Number))
			throw new IllegalArgumentException("NotNumber: " + v2);

		if (v1 instanceof Long) {
			if (v2 instanceof Long) {
				return ((Long) v1).longValue() >= ((Long) v2).longValue();
			} else {
				return ((Long) v1).longValue() >= ((Number) v2).doubleValue();
			}
		} else {
			if (v2 instanceof Long) {
				return ((Number) v1).doubleValue() >= ((Long) v2).longValue();
			} else {
				return ((Number) v1).doubleValue() >= ((Number) v2)
						.doubleValue();
			}
		}
	}

	static public boolean equal(Object v1, Object v2) {
		if (v1.equals(v2))
			return true;

		if (v1 instanceof Polylist) {
			if (v2 instanceof Polylist)
				return Polylist.equals((Polylist) v1, (Polylist) v2);
			else
				return false;
		}

		if (v2 instanceof Polylist) {
			return false;
		}

		if (v1 instanceof String) {
			if (v2 instanceof String)
				return v1.equals(v2);
			else
				return false;
		}

		if (v2 instanceof String) {
			return false;
		}

		if (v1 instanceof Boolean) {
			if (v2 instanceof Boolean)
				return v1.equals(v2);
			else
				return false;
		}

		if (v2 instanceof Boolean) {
			return false;
		}

		if (v1 instanceof Number) {
			if (v1 instanceof Long) {
				if (v2 instanceof Long) {
					return ((Long) v1).longValue() == ((Long) v2).longValue();
				} else {
					return ((Long) v1).longValue() == ((Number) v2)
							.doubleValue();
				}
			} else {
				if (v2 instanceof Long) {
					return ((Number) v1).doubleValue() == ((Long) v2)
							.longValue();
				} else {
					return ((Number) v1).doubleValue() == ((Number) v2)
							.doubleValue();
				}
			}
		}

		return false;
	}

	static public Number add(Object v1, Object v2) {
		if (!(v1 instanceof Number))
			throw new IllegalArgumentException("NotNumber: " + v1);

		if (!(v2 instanceof Number))
			throw new IllegalArgumentException("NotNumber: " + v2);

		if (v1 instanceof Long) {
			if (v2 instanceof Long) {
				return new Long(((Long) v1).longValue()
						+ ((Long) v2).longValue());
			} else {
				return new Double(((Long) v1).longValue()
						+ ((Double) v2).doubleValue());
			}
		} else {
			if (v2 instanceof Long) {
				return new Double(((Double) v1).doubleValue()
						+ ((Long) v2).longValue());
			} else {
				return new Double(((Double) v1).doubleValue()
						+ ((Double) v2).doubleValue());
			}
		}
	}

	static public Number subtract(Object v1, Object v2) {
		if (!(v1 instanceof Number))
			throw new IllegalArgumentException("NotNumber: " + v1);

		if (!(v2 instanceof Number))
			throw new IllegalArgumentException("NotNumber: " + v2);

		if (v1 instanceof Long) {
			if (v2 instanceof Long) {
				return new Long(((Long) v1).longValue()
						- ((Long) v2).longValue());
			} else {
				return new Double(((Long) v1).longValue()
						- ((Double) v2).doubleValue());
			}
		} else {
			if (v2 instanceof Long) {
				return new Double(((Double) v1).doubleValue()
						- ((Long) v2).longValue());
			} else {
				return new Double(((Double) v1).doubleValue()
						- ((Double) v2).doubleValue());
			}
		}
	}

	static public Number multiply(Object v1, Object v2) {
		if (!(v1 instanceof Number))
			throw new IllegalArgumentException("NotNumber: " + v1);

		if (!(v2 instanceof Number))
			throw new IllegalArgumentException("NotNumber: " + v2);

		if (v1 instanceof Long) {
			if (v2 instanceof Long) {
				return new Long(((Long) v1).longValue()
						* ((Long) v2).longValue());
			} else {
				return new Double(((Long) v1).longValue()
						* ((Double) v2).doubleValue());
			}
		} else {
			if (v2 instanceof Long) {
				return new Double(((Double) v1).doubleValue()
						* ((Long) v2).longValue());
			} else {
				return new Double(((Double) v1).doubleValue()
						* ((Double) v2).doubleValue());
			}
		}
	}

	static public Number divide(Object v1, Object v2) {
		if (!(v1 instanceof Number))
			throw new IllegalArgumentException("NotNumber: " + v1);

		if (!(v2 instanceof Number))
			throw new IllegalArgumentException("NotNumber: " + v2);

		if (v1 instanceof Long) {
			if (v2 instanceof Long) {
				return new Long(((Long) v1).longValue()
						/ ((Long) v2).longValue());
			} else {
				return new Double(((Long) v1).longValue()
						/ ((Double) v2).doubleValue());
			}
		} else {
			if (v2 instanceof Long) {
				return new Double(((Double) v1).doubleValue()
						/ ((Long) v2).longValue());
			} else {
				return new Double(((Double) v1).doubleValue()
						/ ((Double) v2).doubleValue());
			}
		}
	}

	static public Number mod(Object v1, Object v2) {
		if (!(v1 instanceof Long))
			throw new IllegalArgumentException("NotNumber: " + v1);

		if (!(v2 instanceof Long))
			throw new IllegalArgumentException("NotNumber: " + v2);

		return new Long(((Long) v1).longValue() % ((Long) v2).longValue());
	}
}
