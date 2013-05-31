/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.math.statistics;

import org.jamesii.core.math.BinomialCoefficient;
import org.jamesii.core.math.Matrix;
import org.jamesii.core.math.statistics.univariate.ArithmeticMean;

/**
 * This class provides tools for the analysis of bivariate data. <br/>
 * <b>WARNING: UNTESTED IMPLEMENTATIONS!</b> <br/>
 * TODO: This class desperately needs some refactoring.
 * 
 * @author Jan Pommerenke
 * 
 */
public final class BivariateDataAnalysis {

  /**
   * Hidden constructor.
   */
  private BivariateDataAnalysis() {
  }

  /**
   * This method compute the count of allSimilarCouples (Nxy).
   * 
   * @param x
   *          input Matrix (must be IntegerValues)
   * @param y
   *          input Matrix (must be IntegerValues)
   * @return output count of allSimilarCouples
   */
  public static int allSimilarCouples(Matrix x, Matrix y) {
    int nxy = 0;
    int xlc = x.getRows(); // length of X-Column
    int xlr = x.getColumns(); // length of X-Row
    int ylc = y.getRows(); // length of Y-Column
    int ylr = y.getColumns(); // length of Y-Row

    for (int u = 0; u < xlc; u++) {
      for (int z = 0; z < xlr; z++) {
        for (int i = 0; i < ylc; i++) {
          for (int j = 0; j < ylr; j++) {
            if ((u == i && z == j) || (u == i && z == j)) {
              nxy =
                  nxy
                      + (int) BinomialCoefficient.binomial(
                          (int) x.getElement(i, j), 2);
            }
          }
        }
      }
    }
    return nxy;
  }

  /**
   * This method compute the context as difference of real vs. assumption
   * distribution.
   * 
   * @param x
   *          incoming matrix
   * 
   * @return chi the context
   */
  public static double chiSquare(Matrix x) {
    double chi = 0;
    double n = 0;
    int lc = x.getRows(); // length of Column
    int lr = x.getColumns(); // length of Row
    for (int i = 0; i < lc; i++) {
      for (int j = 0; j < lr; j++) {
        n =
            n
                + (((x.getElement(i, j) - expectedFilling(x, i, j)) * (x
                    .getElement(i, j) - expectedFilling(x, i, j))) / expectedFilling(
                    x, i, j));
      }
    }
    chi = n;
    return chi;
  }

  /**
   * This method computes the count of concordant couples. (NC)
   * 
   * @param x
   *          input Matrix (must be IntegerValues)
   * @param y
   *          input Matrix (must be IntegerValues)
   * @return NC output count of concordant couples
   */
  public static int concordant(Matrix x, Matrix y) {
    int nc = 0;
    int xlc = x.getRows(); // length of X-Column
    int xlr = x.getColumns(); // length of X-Row
    int ylc = y.getRows(); // length of Y-Column
    int ylr = y.getColumns(); // length of Y-Row

    for (int u = 0; u < xlc; u++) {
      for (int z = 0; z < xlr; z++) {
        for (int i = 0; i < ylc; i++) {
          for (int j = 0; j < ylr; j++) {
            if ((u < i && z < j) || (u > i && z > j)) {
              nc = nc + (int) (x.getElement(u, z) * y.getElement(i, j));
            }
          }
        }
      }
    }
    return nc;
  }

  /**
   * This method compute the degrees of freedom of R X C.
   * 
   * @param x
   *          incoming matrix
   * 
   * @return df the degreesOfFreedom
   */
  public static double degreesOfFreedom(Matrix x) {
    double df = 0;
    int lc = x.getRows(); // length of Column
    int lr = x.getColumns(); // length of Row
    df = (lc - 1) * (lr - 1);
    return df;
  }

  /**
   * This method compute the count of discordant couples (ND).
   * 
   * @param x
   *          input Matrix (must be IntegerValues)
   * @param y
   *          input Matrix (must be IntegerValues)
   * 
   * @return ND output count of discordant couples
   */
  public static int discordant(Matrix x, Matrix y) {
    int nd = 0;
    int xlc = x.getRows(); // length of X-Column
    int xlr = x.getColumns(); // length of X-Row
    int ylc = y.getRows(); // length of Y-Column
    int ylr = y.getColumns(); // length of Y-Row

    for (int u = 0; u < xlc; u++) {
      for (int z = 0; z < xlr; z++) {
        for (int i = 0; i < ylc; i++) {
          for (int j = 0; j < ylr; j++) {
            if ((u > i && z < j) || (u < i && z > j)) {
              nd = nd + (int) (x.getElement(u, z) * y.getElement(i, j));
            }
          }
        }
      }
    }
    return nd;
  }

  /**
   * This method computes the expected filling for undifferentiated table.
   * 
   * @param x
   *          incoming matrix
   * @param i
   *          computed column
   * @param j
   *          computed row
   * 
   * @return P expectedFilling
   */
  public static double expectedFilling(Matrix x, int i, int j) {
    double p = 0;
    int lc = x.getRows(); // length of Column
    int lr = x.getColumns(); // length of Row
    double nN = 0;
    double k = 0;
    double v = 0;

    for (int u = 0; u < lc; u++) {
      double n = 0;
      for (int z = 0; z < lr; z++) {
        n = n + x.getElement(u, z);
      }
      nN = nN + n; // ausgabe Summe aller Untersuchungseinheiten N
    }
    for (int u = 0; u < lc; u++) {
      k = k + x.getElement(i, u); // Summe (h i punkt)
    }
    for (int z = 0; z < lr; z++) {
      v = v + x.getElement(z, j); // Summe (h punkt j)
    }
    p = (k * v) / nN;
    return p;
  }

  /**
   * This method computes Gamma.
   * 
   * @param x
   *          incoming Matrix
   * 
   * @return c Gamma
   */
  public static double gamma(Matrix x) {
    double g = 0;
    double concordantXX = concordant(x, x);
    double discordantXX = discordant(x, x);
    g = (concordantXX - discordantXX) / (concordantXX + discordantXX);
    return g;
  }

  /**
   * This method computes Kendall's "Tau a".
   * 
   * @param x
   *          incoming Matrix
   * 
   * @return a Kendall's "Tau a"
   */
  public static double kendallsTauA(Matrix x) {
    double a = 0;
    double concordantXX = concordant(x, x);
    double discordantXX = discordant(x, x);
    a =
        (concordantXX - discordantXX)
            / (concordantXX + discordantXX + similarXJoin(x, x)
                + similarYJoin(x, x) + allSimilarCouples(x, x));
    return a;
  }

  /**
   * This method computes Kendall's "Tau b".
   * 
   * @param x
   *          incoming Matrix
   * 
   * @return b Kendalls "Tau b"
   */
  public static double kendallsTauB(Matrix x) {
    double b = 0;
    double concordantXX = concordant(x, x);
    double discordantXX = discordant(x, x);
    b =
        (concordantXX - discordantXX)
            / Math.sqrt((concordantXX + discordantXX + similarXJoin(x, x))
                * (concordantXX + discordantXX + similarYJoin(x, x)));
    return b;
  }

  /**
   * This method computes Kendall's "Tau c".
   * 
   * @param x
   *          incoming Matrix
   * 
   * @return c Kendall's "Tau c"
   */
  public static double kendallsTauC(Matrix x) {
    double c = 0;
    int r = x.getColumns();
    int l = x.getRows();
    int m = Math.min(r, l);
    double concordantXX = concordant(x, x);
    double discordantXX = discordant(x, x);
    double n =
        concordantXX + discordantXX + similarXJoin(x, x) + similarYJoin(x, x)
            + allSimilarCouples(x, x);
    c = 2 * (concordantXX - discordantXX) / n * n * (m - 1 / m);
    return c;
  }

  /**
   * Lambda is a coherence measure with compute of PRE(proportional reduction).
   * Lambda c is asymmetric mass (c for column). Values Range [0,1] . Is
   * Lambda_c, Lambda_r = 0 => non context between the Variables. Is Lambda_c,
   * Lambda_r = 1 => maximum context.
   * 
   * @param x
   *          the x
   * 
   * @return the double
   */
  public static double lamdaC(Matrix x) {
    double c = 0;
    double sum = 0;
    int lc = x.getRows(); // length of Column
    int lr = x.getColumns(); // length of Row
    double maxj = 0;
    double tmpN = 0;

    for (int j = 0; j < lr; j++) {
      double n = 0;
      double max = 0;
      for (int i = 0; i < lc; i++) {
        n = Math.max(x.getElement(j, i), n);
      }
      max = Math.max(max, n);
      maxj = max; // max von (h punkt j)
    }
    for (int j = 0; j < lc; j++) {
      double n = 0;
      for (int i = 0; i < lr; i++) {
        n = n + x.getElement(j, i);
      }
      tmpN = tmpN + n; // sum of all units under examination
    }
    for (int i = 0; i < lr; i++) {
      double n = 0;
      for (int j = 0; j < lc; j++) {
        n = Math.max(x.getElement(i, j), n);
      }
      sum = sum + n; // sum of all max values of row i
    }
    c = (sum - maxj) / (tmpN - maxj);
    return c;
  }

  /**
   * Lambda is a coherence measure with compute of PRE(proportional reduction).
   * Lambda r is asymmetric mass (r for Row). Values Range [0,1.]. Is Lambda_c,
   * Lambda_r = 0 => non context between the Variables. Is Lambda_c, Lambda_r =
   * 1 => maximum context.
   * 
   * @param x
   *          the x
   * 
   * @return the double
   */
  public static double lamdaR(Matrix x) {
    double r = 0;
    double sum = 0;
    int lc = x.getRows(); // length of Column
    int lr = x.getColumns(); // length of Row
    double maxi = 0;
    double tmpN = 0;

    for (int i = 0; i < lc; i++) {
      double n = 0;
      double max = 0;
      for (int j = 0; j < lr; j++) {
        n = Math.max(x.getElement(i, j), n);
      }
      max = Math.max(max, n);
      maxi = max; // max von (h i punkt)
    }

    for (int j = 0; j < lc; j++) {
      double n = 0;
      for (int i = 0; i < lr; i++) {
        n = n + x.getElement(j, i);
      }
      tmpN = tmpN + n; // sum of all considered units
    }
    for (int j = 0; j < lc; j++) {
      double n = 0;
      for (int i = 0; i < lr; i++) {
        n = Math.max(x.getElement(j, i), n);
      }
      sum = sum + n; // sum of all max values of column j
    }
    r = (sum - maxi) / (tmpN - maxi);
    return r;
  }

  /**
   * Lambda is a coherence measure with compute of PRE(proportional reduction)
   * Lambda s is symmetric mass (LAMDA).
   * 
   * @param x
   *          the x
   * 
   * @return the double
   */
  public static double lamdaS(Matrix x) {
    double s = 0;
    double sumi = 0;
    double sumj = 0;
    int lc = x.getRows(); // length of Column
    int lr = x.getColumns(); // length of Row
    double maxi = 0;
    double maxj = 0;
    double tmpN = 0;

    for (int i = 0; i < lc; i++) {
      double n = 0;
      double max = 0;
      for (int j = 0; j < lr; j++) {
        n = Math.max(x.getElement(i, j), n);
      }
      max = Math.max(max, n);
      maxi = max; // max (h i point)
    }
    for (int j = 0; j < lr; j++) {
      double n = 0;
      double max = 0;
      for (int i = 0; i < lc; i++) {
        n = Math.max(x.getElement(j, i), n);
      }
      max = Math.max(max, n);
      maxj = max; // max von (h punkt j)
    }
    for (int j = 0; j < lc; j++) {
      double n = 0;
      for (int i = 0; i < lr; i++) {
        n = n + x.getElement(j, i);
      }
      tmpN = tmpN + n; // sum of all considered units N
    }
    for (int i = 0; i < lr; i++) {
      double n = 0;
      for (int j = 0; j < lc; j++) {
        n = Math.max(x.getElement(i, j), n);
      }
      sumi = sumi + n; // ausgabe Summe aller MaxWerte der i Zeile
    }
    for (int j = 0; j < lc; j++) {
      double n = 0;
      for (int i = 0; i < lr; i++) {
        n = Math.max(x.getElement(j, i), n);
      }
      sumj = sumj + n; // ausgabe Summe aller MaxWerte der j Spalte
    }
    s =
        ((sumj - sumi) / (2 * tmpN - maxi - maxj))
            - ((maxi + maxj) / (2 * tmpN - maxi - maxj));
    return s;
  }

  /**
   * This Method computes the likelihood of P(Xj,Yi) of Matrix_Values.
   * 
   * @param x
   *          incoming matrix
   * @param i
   *          computed column
   * @param j
   *          computed row
   * 
   * @return P likelihood of (i,j) of matrix
   */
  public static double likelihood(Matrix x, int i, int j) {
    double p = 0;
    int lc = x.getRows(); // length of Column
    int lr = x.getColumns(); // length of Row
    double tmpN = 0;
    double k = 0;
    double v = 0;

    for (int u = 0; u < lc; u++) {
      double n = 0;
      for (int z = 0; z < lr; z++) {
        n = n + x.getElement(u, z);
      }
      tmpN = tmpN + n; // ausgabe Summe aller Untersuchungseinheiten N
    }
    for (int u = 0; u < lc; u++) {
      k = k + x.getElement(i, u); // Summe (h i punkt)
    }
    for (int z = 0; z < lr; z++) {
      v = v + x.getElement(z, j); // Summe (h punkt j)
    }
    p = (k * v) / tmpN * tmpN;
    return p;
  }

  /**
   * This method compute OLS-Valuer (b).
   * 
   * @param x
   *          incoming Array of int Values
   * @param y
   *          incoming Array of int Values
   * 
   * @return b OLS-Valuer for the population
   */
  public static double computeOLSValuer(int[] x, int[] y) {
    double b = 0;
    int lx = x.length;
    int n = lx;
    double v = 0;
    for (int i = 0; i < lx; i++) {
      v = x[i] * y[i];
    }
    b = v / n;
    return b;
  }

  /**
   * This Method compute the Product-Moment-CorrealationCoefficient(r). If
   * Variables X,Y are not standardized, RSquare is metric and pos. in interval
   * [0,1] !! If X,Y standardized ,then is "r" = regressionCoefStandard !!
   * 
   * @param x
   *          incoming Array with int Values
   * @param y
   *          incoming Array with int Values
   * 
   * @return R the compute Coefficient of Determination
   */
  public static double computePMCorrealationCoef(int[] x, int[] y) {
    double pm = 0;
    double cov = 0;
    double varx = 0;
    double vary = 0;
    double u = 0;
    double vx = 0;
    double vy = 0;
    int lx = x.length; // length of X / Y
    double a = 0;
    double b = 0; // ArithmeticMean of Y
    b = ArithmeticMean.arithmeticMean(y);
    a = ArithmeticMean.arithmeticMean(x);

    for (int i = 0; i < lx; i++) {
      u += (x[i] - a) * (y[i] - b);
    }
    for (int i = 0; i < lx; i++) {
      vx += (x[i] - a) * (x[i] - a);
    }
    for (int i = 0; i < lx; i++) {
      vy += (y[i] - b) * (y[i] - b);
    }
    cov = u / lx;
    varx = vx / lx;
    vary = vy / lx;

    pm = cov / ((Math.sqrt(varx)) * (Math.sqrt(vary)));
    return pm;
  }

  /**
   * Calculates covariance. Not numerically stable for large differences. Both
   * arrays have to be the same size and must not contain null elements.
   * 
   * @param v1
   *          the first vector
   * @param v2
   *          the second vector
   * 
   * @return the double
   */
  public static Double covariance(Double[] v1, Double[] v2) {
    assert v1.length == v2.length : "Arrays have to have the same length";
    double meanV1 = ArithmeticMean.arithmeticMean(v1);
    double meanV2 = ArithmeticMean.arithmeticMean(v2);

    Double[] product = new Double[v1.length];
    for (int i = 0; i < v1.length; i++) {
      product[i] = v1[i] * v2[i];
    }

    return ArithmeticMean.arithmeticMean(product) - meanV1 * meanV2;
  }

  /**
   * Calculates covariance matrix (or the rows). Rows have to be qually long.
   * Not numerically stable for large differences.
   * 
   * @param values
   *          the rows array
   * 
   * @return the covariance matrix
   */
  public static Double[][] covarianceMatrix(Double[][] values) {

    Double[][] covMatrix = new Double[values.length][values.length];

    for (int i = 0; i < values.length; i++) {
      for (int j = 0; j < values.length; j++) {
        if (j >= i) {
          covMatrix[i][j] = covariance(values[i], values[j]);
        } else {
          covMatrix[i][j] = covMatrix[j][i];
        }
      }
    }

    return covMatrix;
  }

  /**
   * Compute the regression coefficient b1 Variables x,y are not standardized.
   * 
   * @param x
   *          incoming Array with int Values
   * @param y
   *          incoming Array with int Values
   * 
   * @return b1 regressionCoefficient
   */
  public static double regressionCoef(int[] x, int[] y) {
    double b1 = 0;
    int lx = x.length; // length of X is equal length of Y
    double a = 0; // ArithmeticMean of X
    double b = 0; // ArithmeticMean of Y
    double u = 0;
    double v = 0;

    a = ArithmeticMean.arithmeticMean(x);
    b = ArithmeticMean.arithmeticMean(y);

    for (int i = 0; i < lx; i++) {
      u += (x[i] - a) * (y[i] - b);
    }
    for (int i = 0; i < lx; i++) {
      v += (x[i] - a) * (x[i] - a);
    }
    b1 = u / lx / v / lx;
    return b1;
  }

  /**
   * Update on 30.03.2005 this method compute the regression coefficient b1
   * Variables x,y are standardized.
   * 
   * @param x
   *          incoming Array with int Values
   * @param y
   *          incoming Array with int Values
   * 
   * @return b1 regressionCoefficient
   */
  public static double regressionCoefStandard(int[] x, int[] y) {
    double b1 = 0;
    int lx = x.length; // length of X / Y
    double a = 0; // ArithmeticMean of X
    double b = 0; // ArithmeticMean of Y
    double u = 0;
    double v = 0;

    double xval = 0;
    double yval = 0;
    double z = 0;

    a = ArithmeticMean.arithmeticMean(x);
    b = ArithmeticMean.arithmeticMean(y);

    for (int i = 0; i < lx; i++) {
      u += (x[i] - a) * (y[i] - b);
    }
    for (int i = 0; i < lx; i++) {
      v += (x[i] - a) * (x[i] - a);
    }
    for (int i = 0; i < lx; i++) {
      xval = (x[i] - a) / Math.sqrt(u);
      yval = (y[i] - b) / Math.sqrt(v);
      z += xval * yval;
    }

    b1 = z / lx;
    return b1;
  }

  /**
   * This Method compute Coefficient of Determination (R Square) RSquare is
   * metric and pos. in interval [0,1] !! E2 is equal with
   * the"Minimization Criterion S(e)" !!
   * 
   * @param x
   *          incoming Array with int Values
   * @param y
   *          incoming Array with int Values
   * 
   * @return R the compute Coefficient of Determination
   */
  public static double rSquare(int[] x, int[] y) {
    double r = 0;
    double e1 = 0;
    double e2 = 0;
    // double y = 0;
    double b0 = 0;
    double b1 = 0;
    int lx = x.length; // length of X / Y
    double a = 0;
    double b = 0; // ArithmeticMean of Y
    b = ArithmeticMean.arithmeticMean(y);
    a = ArithmeticMean.arithmeticMean(x);
    b1 = BivariateDataAnalysis.regressionCoefStandard(x, y);
    for (int i = 0; i < lx; i++) {
      e1 += (y[i] - b) * (y[i] - b);
    }
    b0 = b - b1 * a;
    for (int i = 0; i < lx; i++) {
      e2 += (y[i] - (b0 + b1 * x[i])) * (y[i] - (b0 + b1 * x[i]));
    }
    r = (e1 - e2) / e1;
    return r;
  }

  /**
   * This method compute the standard error of regression (sigma_e) sigma_e = E2
   * div. (n-k); (E2==S(e)).
   * 
   * @param x
   *          the x
   * @param y
   *          the y
   * 
   * @return s error of regression
   */
  public static double sigmaE(int[] x, int[] y) {
    double s = 0;
    double e2 = 0;
    int lx = x.length;
    int n = lx;
    int k = 0;
    // TODO: wie wird k bestimmt ?? und ist meine Interpretation von n überhaupt
    // richtig
    double a = 0; // ArithmeticMean of X
    double b = 0; // ArithmeticMean of Y
    double b0 = 0;
    double b1 = 0;
    b = ArithmeticMean.arithmeticMean(y);
    a = ArithmeticMean.arithmeticMean(x);
    b1 = BivariateDataAnalysis.regressionCoefStandard(x, y);
    b0 = b - b1 * a;
    for (int i = 0; i < lx; i++) {
      e2 += (y[i] - (b0 + b1 * x[i])) * (y[i] - (b0 + b1 * x[i]));
    }
    s = e2 / (n - k);
    return s;
  }

  /**
   * This method compute the critical Value
   * 
   * @param signiveau
   *          incoming significance level
   * @param X
   *          the x
   * @param K
   *          the k
   * 
   * @return cv the ctiticalValue
   */
  /*
   * public static double criticalValue(double df, double signiveau){ double cv
   * = 0; // TODO: wie wird (kritischer Wert cv) berechnet?? return cv; }
   */
  /**
   * This method decide over the hypothesis = 0 and returns the probability.
   * 
   * @param x
   *          incoming Chi-Square-Value
   * @param k
   *          incoming Critical-Value
   * @param signiveau
   *          incoming Value
   * 
   * @return P significance Probability (level)
   */
  public static double significanceProbability(double x, double k,
      double signiveau) {
    // boolean hr = false;
    double p = 0; // probability
    if (x <= k) {
      // hr = true;
      p = 1 - signiveau; // inside of foundation of trust
    } else {
      // hr = false;
      p = signiveau; // outside of foundation of trust
    }
    return p;
  }

  /**
   * This method compute the count of similarXJoin Couples(Nx).
   * 
   * @param x
   *          input Matrix (must be IntegerValues)
   * @param y
   *          input Matrix (must be IntegerValues)
   * 
   * @return Nx output count of similarXJoin
   */
  public static int similarXJoin(Matrix x, Matrix y) {
    int nx = 0;
    int xlc = x.getRows(); // length of X-Column
    int xlr = x.getColumns(); // length of X-Row
    int ylc = y.getRows(); // length of Y-Column
    int ylr = y.getColumns(); // length of Y-Row

    for (int u = 0; u < xlc; u++) {
      for (int z = 0; z < xlr; z++) {
        for (int i = 0; i < ylc; i++) {
          for (int j = 0; j < ylr; j++) {
            if ((u == i && z < j) || (u == i && z > j)) {
              nx = nx + (int) (x.getElement(u, z) * y.getElement(i, j));
            }
          }
        }
      }
    }
    return nx;
  }

  /**
   * This method computes the count of similarXJoin Couples(Ny).
   * 
   * @param x
   *          input Matrix (must be IntegerValues)
   * @param y
   *          input Matrix (must be IntegerValues)
   * 
   * @return Ny output count of similarYJoin
   */
  public static int similarYJoin(Matrix x, Matrix y) {
    int ny = 0;
    int xlc = x.getRows(); // length of X-Column
    int xlr = x.getColumns(); // length of X-Row
    int ylc = y.getRows(); // length of Y-Column
    int ylr = y.getColumns(); // length of Y-Row

    for (int u = 0; u < xlc; u++) {
      for (int z = 0; z < xlr; z++) {
        for (int i = 0; i < ylc; i++) {
          for (int j = 0; j < ylr; j++) {
            if ((u < i && z == j) || (u > i && z == j)) {
              ny = ny + (int) (x.getElement(u, z) * y.getElement(i, j));
            }
          }
        }
      }
    }
    return ny;
  }

  /**
   * This method computes symmetric Somer's d.
   * 
   * @param x
   *          incoming Matrix
   * 
   * @return s Somer's d
   */
  public static double somersdS(Matrix x) {
    double ds = 0;
    double concordantXX = concordant(x, x);
    double discordantXX = discordant(x, x);
    ds =
        (concordantXX - discordantXX)
            / (concordantXX + discordantXX + (similarXJoin(x, x) + similarYJoin(
                x, x)) / 2.0);
    return ds;
  }

  /**
   * This method computes Somer's d with depending x.
   * 
   * @param x
   *          incoming Matrix
   * 
   * @return s Somer's d
   */
  public static double somersdX(Matrix x) {
    double dxy = 0;
    double concordantXX = concordant(x, x);
    double discordantXX = discordant(x, x);
    dxy =
        (concordantXX - discordantXX)
            / (concordantXX + discordantXX + similarXJoin(x, x));
    return dxy;
  }

  /**
   * This method computes Somers�d with depending y.
   * 
   * @param x
   *          incoming Matrix
   * 
   * @return s Somer's d
   */
  public static double somersY(Matrix x) {
    double dyx = 0;
    double concordantXX = concordant(x, x);
    double discordantXX = discordant(x, x);
    dyx =
        (concordantXX - discordantXX)
            / (concordantXX + discordantXX + similarYJoin(x, x));
    return dyx;
  }
}
