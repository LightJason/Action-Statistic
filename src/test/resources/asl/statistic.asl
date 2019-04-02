/*
 * @cond LICENSE
 * ######################################################################################
 * # LGPL License                                                                       #
 * #                                                                                    #
 * # This file is part of the LightJason                                                #
 * # Copyright (c) 2015-19, LightJason (info@lightjason.org)                            #
 * # This program is free software: you can redistribute it and/or modify               #
 * # it under the terms of the GNU Lesser General Public License as                     #
 * # published by the Free Software Foundation, either version 3 of the                 #
 * # License, or (at your option) any later version.                                    #
 * #                                                                                    #
 * # This program is distributed in the hope that it will be useful,                    #
 * # but WITHOUT ANY WARRANTY; without even the implied warranty of                     #
 * # MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the                      #
 * # GNU Lesser General Public License for more details.                                #
 * #                                                                                    #
 * # You should have received a copy of the GNU Lesser General Public License           #
 * # along with this program. If not, see http://www.gnu.org/licenses/                  #
 * ######################################################################################
 * @endcond
 */

// -----
// agent for testing statistic
// @iteration 2
// @testcount 2
// -----

// initial-goal
!test.

/**
 * base test
 */
+!test <-
    !testbase;
    !teststatistic
.


/**
 * test max/min index
 */
+!testbase <-
    Distribution = .math/statistic/createdistribution( "normal", 20, 100 );
    RV = .math/statistic/randomsample( Distribution, 8 );

    .test/print("random", RV);
    .test/result( success )
.


/**
 * test statistic
 */
+!teststatistic <-
        Statistic = .math/statistic/createstatistic;
        Distribution = .math/statistic/createdistribution( "normal", 20, 100 );

        RV = .math/statistic/randomsample( Distribution, 8 );
        L = .test/list/range(1, 20);
        .math/statistic/addstatisticvalue(Statistic, RV, L);

        [SMax|SMin|SCount|SPopVariance|SQuadraticMean|SSecondMom|SStd|SSum|SSumSq|SVar|SMean] = .math/statistic/multiplestatisticvalue(Statistic, "max", "min", "count", "populationvariance", "quadraticmean", "secondmoment", "standarddeviation", "sum", "sumsquare", "variance", "mean" );
        SX = .math/statistic/singlestatisticvalue("mean", Statistic);

        .test/print("statistic", SMax, SMin, SCount, SPopVariance, SQuadraticMean, SSecondMom, SStd, SSum, SSumSq, SVar, SMean );
        .test/result( success )
.