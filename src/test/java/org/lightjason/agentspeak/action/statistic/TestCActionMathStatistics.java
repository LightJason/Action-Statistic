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

package org.lightjason.agentspeak.action.statistic;

import org.apache.commons.math3.distribution.BetaDistribution;
import org.apache.commons.math3.distribution.CauchyDistribution;
import org.apache.commons.math3.distribution.ChiSquaredDistribution;
import org.apache.commons.math3.distribution.ExponentialDistribution;
import org.apache.commons.math3.distribution.FDistribution;
import org.apache.commons.math3.distribution.GammaDistribution;
import org.apache.commons.math3.distribution.GumbelDistribution;
import org.apache.commons.math3.distribution.LaplaceDistribution;
import org.apache.commons.math3.distribution.LevyDistribution;
import org.apache.commons.math3.distribution.LogNormalDistribution;
import org.apache.commons.math3.distribution.LogisticDistribution;
import org.apache.commons.math3.distribution.NakagamiDistribution;
import org.apache.commons.math3.distribution.NormalDistribution;
import org.apache.commons.math3.distribution.ParetoDistribution;
import org.apache.commons.math3.distribution.TDistribution;
import org.apache.commons.math3.distribution.TriangularDistribution;
import org.apache.commons.math3.distribution.UniformRealDistribution;
import org.apache.commons.math3.distribution.WeibullDistribution;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.apache.commons.math3.stat.descriptive.SummaryStatistics;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.lightjason.agentspeak.language.CRawTerm;
import org.lightjason.agentspeak.language.ITerm;
import org.lightjason.agentspeak.language.execution.IContext;
import org.lightjason.agentspeak.testing.IBaseTest;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;


/**
 * test for statistics actions
 */
public final class TestCActionMathStatistics extends IBaseTest
{
    /**
     * testing summary statistic
     */
    private SummaryStatistics m_summarystatistic;

    /**
     * testing descriptive statistic
     */
    private DescriptiveStatistics m_descriptivestatistic;

    /**
     * initialize
     */
    @BeforeEach
    public void initialize()
    {
        m_summarystatistic = new SummaryStatistics();
        m_descriptivestatistic = new DescriptiveStatistics();

        m_summarystatistic.addValue( 2 );
        m_summarystatistic.addValue( 5 );
        m_summarystatistic.addValue( 3 );
        m_descriptivestatistic.addValue( 3 );
        m_descriptivestatistic.addValue( 4 );
        m_descriptivestatistic.addValue( 5 );
    }

    /**
     * test create statistics
     */
    @Test
    public void createstatistics()
    {
        final List<ITerm> l_return = new ArrayList<>();

        new CCreateStatistic().execute(
            false, IContext.EMPTYPLAN,
            Stream.of( "summary", "descriptive" ).map( CRawTerm::of ).collect( Collectors.toList() ),
            l_return
        );
        new CCreateStatistic().execute(
            true, IContext.EMPTYPLAN,
            Stream.of( "summary", "descriptive" ).map( CRawTerm::of ).collect( Collectors.toList() ),
            l_return
        );

        Assertions.assertEquals( 4, l_return.size() );
        Assertions.assertTrue( l_return.get( 0 ).raw() instanceof SummaryStatistics );
        Assertions.assertTrue( l_return.get( 1 ).raw() instanceof DescriptiveStatistics );
        Assertions.assertTrue( l_return.get( 2 ).raw() instanceof SummaryStatistics );
        Assertions.assertTrue( l_return.get( 3 ).raw() instanceof DescriptiveStatistics );
    }

    /**
     * test percentile
     */
    @Test
    public void percentile()
    {
        final List<ITerm> l_return = new ArrayList<>();
        final DescriptiveStatistics l_statistic1 = new DescriptiveStatistics();
        final DescriptiveStatistics l_statistic2 = new DescriptiveStatistics();

        IntStream.range( 0, 100 ).peek( l_statistic1::addValue ).forEach( i -> l_statistic2.addValue( i * 10 ) );


        new CSinglePercentile().execute(
            false, IContext.EMPTYPLAN,
            Stream.of( 50, l_statistic1, l_statistic2 ).map( CRawTerm::of ).collect( Collectors.toList() ),
            l_return
        );

        new CMultiplePercentile().execute(
            false, IContext.EMPTYPLAN,
            Stream.of( l_statistic1, 25, 75 ).map( CRawTerm::of ).collect( Collectors.toList() ),
            l_return
        );


        Assertions.assertEquals( 4, l_return.size() );
        Assertions.assertArrayEquals(
            Stream.of( 49.5, 495, 24.25, 74.75 ).mapToDouble( Number::doubleValue ).boxed().toArray(),
            l_return.stream().map( i -> i.<Number>raw().doubleValue() ).toArray()
        );
    }

    /**
     * test clear
     */
    @Test
    public void clear()
    {
        new CClearStatistic().execute(
            false, IContext.EMPTYPLAN,
            Stream.of( m_summarystatistic, m_descriptivestatistic ).map( CRawTerm::of ).collect( Collectors.toList() ),
            Collections.emptyList()
        );

        Assertions.assertEquals( 0, m_summarystatistic.getSum(), 0 );
        Assertions.assertEquals( 0, m_descriptivestatistic.getSum(), 0 );
    }

    /**
     * test create distribution
     */
    @Test
    public void createdistribution()
    {
        final List<ITerm> l_return = new ArrayList<>();

        new CCreateDistribution().execute(
            false, IContext.EMPTYPLAN,
            Stream.of(
                        "normal", "ISAAC", 20, 10,
                        "beta", "SYNCHRONIZEDMERSENNETWISTER", 20, 10,
                        "cauchy", "SYNCHRONIZEDISAAC", 10, 20,
                        "CHISQUARE", "INTERNAL", 10,
                        "EXPONENTIAL", "SYNCHRONIZEDINTERNAL", 5,
                        "F", "WELL512A", 2, 6,
                        "GAMMA", "SYNCHRONIZEDWELL512A", 6, 9,
                        "GUMBLE", "WELL1024A", 2, 7,
                        "LAPLACE", "SYNCHRONIZEDWELL1024A", 20, 18,
                        "LEVY", "WELL19937A", 15, 20,
                        "LOGISTIC", "SYNCHRONIZEDWELL19937A", 10, 17,
                        "LOGNORMAL", "WELL19937C", 12, 14,
                        "NAKAGAMI", "SYNCHRONIZEDWELL19937C", 20, 18,
                        "PARETO", "WELL4449A", 20, 10,
                        "T", "SYNCHRONIZEDWELL4449A", 10,
                        "TRIANGULAR", "WELL44497B", 10, 15, 20,
                        "UNIFORM", "SYNCHRONIZEDWELL44497B", 10, 25,
                        "WEIBULL", 10, 23
                ).map( CRawTerm::of ).collect( Collectors.toList() ),
            l_return
        );

        Assertions.assertEquals( 18, l_return.size() );
        Assertions.assertTrue( l_return.get( 0 ).raw() instanceof NormalDistribution );
        Assertions.assertTrue( l_return.get( 1 ).raw() instanceof BetaDistribution );
        Assertions.assertTrue( l_return.get( 2 ).raw() instanceof CauchyDistribution );
        Assertions.assertTrue( l_return.get( 3 ).raw() instanceof ChiSquaredDistribution );
        Assertions.assertTrue( l_return.get( 4 ).raw() instanceof ExponentialDistribution );
        Assertions.assertTrue( l_return.get( 5 ).raw() instanceof FDistribution );
        Assertions.assertTrue( l_return.get( 6 ).raw() instanceof GammaDistribution );
        Assertions.assertTrue( l_return.get( 7 ).raw() instanceof GumbelDistribution );
        Assertions.assertTrue( l_return.get( 8 ).raw() instanceof LaplaceDistribution );
        Assertions.assertTrue( l_return.get( 9 ).raw() instanceof LevyDistribution );
        Assertions.assertTrue( l_return.get( 10 ).raw() instanceof LogisticDistribution );
        Assertions.assertTrue( l_return.get( 11 ).raw() instanceof LogNormalDistribution );
        Assertions.assertTrue( l_return.get( 12 ).raw() instanceof NakagamiDistribution );
        Assertions.assertTrue( l_return.get( 13 ).raw() instanceof ParetoDistribution );
        Assertions.assertTrue( l_return.get( 14 ).raw() instanceof TDistribution );
        Assertions.assertTrue( l_return.get( 15 ).raw() instanceof TriangularDistribution );
        Assertions.assertTrue( l_return.get( 16 ).raw() instanceof UniformRealDistribution );
        Assertions.assertTrue( l_return.get( 17 ).raw() instanceof WeibullDistribution );
    }

    /**
     * test add statistics value
     */
    @Test
    public void addstatisticvalue()
    {
        new CAddStatisticValue().execute(
            false, IContext.EMPTYPLAN,
            Stream.of( m_descriptivestatistic, m_summarystatistic, 1, 2, 3 ).map( CRawTerm::of ).collect( Collectors.toList() ),
            Collections.emptyList()
        );

        Assertions.assertEquals( 6, m_descriptivestatistic.getN(), 0 );
        Assertions.assertEquals( 6, m_summarystatistic.getN() );
    }

    /**
     * test multiple statistics value of summary
     */
    @Test
    public void summarymultiplestatisticvalue()
    {
        final List<ITerm> l_return = new ArrayList<>();

        new CMultipleStatisticValue().execute(
            false, IContext.EMPTYPLAN,
            Stream.of(
                    m_summarystatistic,
                    "variance", "mean",
                    "max", "geometricmean",
                    "populationvariance", "quadraticmean",
                    "secondmoment", "standarddeviation",
                    "sumlog", "sumsquare"
                ).map( CRawTerm::of ).collect( Collectors.toList() ),
            l_return
        );

        Assertions.assertEquals( 10, l_return.size() );
        Assertions.assertArrayEquals(
            Stream.of(
                m_summarystatistic.getVariance(), m_summarystatistic.getMean(),
                m_summarystatistic.getMax(), m_summarystatistic.getGeometricMean(),
                m_summarystatistic.getPopulationVariance(), m_summarystatistic.getQuadraticMean(),
                m_summarystatistic.getSecondMoment(), m_summarystatistic.getStandardDeviation(),
                m_summarystatistic.getSumOfLogs(), m_summarystatistic.getSumsq()
            ).toArray(),

            l_return.stream().map( ITerm::raw ).toArray()
        );
    }

    /**
     * test multiple statistics value of descriptive
     */
    @Test
    public void descriptivemultiplestatisticvalue()
    {
        final List<ITerm> l_return = new ArrayList<>();

        new CMultipleStatisticValue().execute(
            false, IContext.EMPTYPLAN,
            Stream.of(
                    m_descriptivestatistic,
                    "variance", "mean",
                    "max", "geometricmean",
                    "populationvariance", "quadraticmean",
                    "standarddeviation", "sumsquare",
                    "kurtiosis", "count", "sum"
                ).map( CRawTerm::of ).collect( Collectors.toList() ),
            l_return
        );

        Assertions.assertEquals( l_return.size(), 11 );
        Assertions.assertArrayEquals(
                Stream.of(
                        m_descriptivestatistic.getVariance(), m_descriptivestatistic.getMean(),
                        m_descriptivestatistic.getMax(), m_descriptivestatistic.getGeometricMean(),
                        m_descriptivestatistic.getPopulationVariance(), m_descriptivestatistic.getQuadraticMean(),
                        m_descriptivestatistic.getStandardDeviation(), m_descriptivestatistic.getSumsq(),
                        m_descriptivestatistic.getKurtosis(), (double) m_descriptivestatistic.getN(), m_descriptivestatistic.getSum()
                ).toArray(),

                l_return.stream().map( ITerm::raw ).toArray()
        );
    }

    /**
     * test add random sample
     */
    @Test
    public void randomsample()
    {
        final List<ITerm> l_return = new ArrayList<>();

        new CRandomSample().execute(
            false, IContext.EMPTYPLAN,
            Stream.of( new NormalDistribution(), 3 ).map( CRawTerm::of ).collect( Collectors.toList() ),
            l_return
        );

        Assertions.assertEquals( 1, l_return.size() );
        Assertions.assertTrue( l_return.get( 0 ).raw() instanceof List );
        Assertions.assertEquals( 3, l_return.get( 0 ).<List<Number>>raw().size() );
    }

    /**
     * test random simple
     */
    @Test
    public void randomsimple()
    {
        final List<ITerm> l_return = new ArrayList<>();

        new CRandomSimple().execute(
            false, IContext.EMPTYPLAN,
            Stream.of( 5 ).map( CRawTerm::of ).collect( Collectors.toList() ),
            l_return
        );

        Assertions.assertEquals( 1, l_return.size() );
        Assertions.assertTrue( l_return.get( 0 ).raw() instanceof List );
        Assertions.assertEquals( 5, l_return.get( 0 ).<List<Number>>raw().size() );
    }

    /**
     * test single statistics value
     */
    @Test
    public void singlestatisticvalue()
    {
        final List<ITerm> l_return = new ArrayList<>();

        new CSingleStatisticValue().execute(
            false, IContext.EMPTYPLAN,
            Stream.of( "min", m_summarystatistic, m_descriptivestatistic ).map( CRawTerm::of ).collect( Collectors.toList() ),
            l_return
        );

        Assertions.assertEquals( 2, l_return.size() );
        Assertions.assertEquals( m_summarystatistic.getMin(), l_return.get( 0 ).<Double>raw(), 0 );
        Assertions.assertEquals( m_descriptivestatistic.getMin(), l_return.get( 1 ).<Double>raw(), 0 );
    }

    /**
     * test exponential selection with strict parameter
     */
    @Test
    public void exponentialselectionstrict()
    {
        final List<ITerm> l_return = Collections.synchronizedList( new ArrayList<>() );

        IntStream.range( 0, 5000 )
                 .parallel()
                 .forEach( i ->
                        new CExponentialSelection().execute(
                            false, IContext.EMPTYPLAN,
                            Stream.of( Stream.of( "a", "b" ).collect( Collectors.toList() ), Stream.of( 4.5, 3.5 ).collect( Collectors.toList() ), 1 )
                                  .map( CRawTerm::of ).collect( Collectors.toList() ),
                            l_return
                        ) );


        Assertions.assertEquals(
            0.73,
            (double) Collections.frequency( l_return.stream().map( ITerm::raw ).collect( Collectors.toList() ), "a" ) / l_return.size(),
            0.05
        );

        Assertions.assertEquals(
            0.27,
            (double) Collections.frequency( l_return.stream().map( ITerm::raw ).collect( Collectors.toList() ), "b" ) / l_return.size(),
            0.05
        );
    }


    /**
     * test exponential selection with lazy parameter
     */
    @Test
    public void exponentialselectionlazy()
    {
        final List<ITerm> l_return = Collections.synchronizedList( new ArrayList<>() );

        IntStream.range( 0, 6500 )
                 .parallel()
                 .forEach( i ->
                               new CExponentialSelection().execute(
                                   false, IContext.EMPTYPLAN,
                                   Stream.of( Stream.of( "a", "b" ).collect( Collectors.toList() ), Stream.of( 4.5, 3.5 ).collect( Collectors.toList() ), 0.5 )
                                         .map( CRawTerm::of ).collect( Collectors.toList() ),
                                   l_return
                               ) );


        Assertions.assertEquals(
            0.73,
            (double) Collections.frequency( l_return.stream().map( ITerm::raw ).collect( Collectors.toList() ), "a" ) / l_return.size(),
            0.2
        );

        Assertions.assertEquals(
            0.27,
            (double) Collections.frequency( l_return.stream().map( ITerm::raw ).collect( Collectors.toList() ), "b" ) / l_return.size(),
            0.2
        );
    }

    /**
     * test linear selection
     */
    @Test
    public void linearselection()
    {
        final List<ITerm> l_return = Collections.synchronizedList( new ArrayList<>() );

        IntStream.range( 0, 6500 )
                 .parallel()
                 .forEach( i ->
                        new CLinearSelection().execute(
                            false, IContext.EMPTYPLAN,
                            Stream.of( Stream.of( "c", "d" ).collect( Collectors.toList() ), Stream.of( 3, 7 ).collect( Collectors.toList() ) )
                                  .map( CRawTerm::of ).collect( Collectors.toList() ),
                            l_return
        ) );

        Assertions.assertEquals(
            0.3,
            (double) Collections.frequency( l_return.stream().map( ITerm::raw ).collect( Collectors.toList() ), "c" ) / l_return.size(),
            0.05
        );

        Assertions.assertEquals(
            0.7,
            (double) Collections.frequency( l_return.stream().map( ITerm::raw ).collect( Collectors.toList() ), "d" ) / l_return.size(),
            0.05
        );
    }

}
