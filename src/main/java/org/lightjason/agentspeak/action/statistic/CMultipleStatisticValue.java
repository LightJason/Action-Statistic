/*
 * @cond LICENSE
 * ######################################################################################
 * # LGPL License                                                                       #
 * #                                                                                    #
 * # This file is part of the LightJason AgentSpeak(L++)                                #
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

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.apache.commons.math3.stat.descriptive.SummaryStatistics;
import org.lightjason.agentspeak.action.IBaseAction;
import org.lightjason.agentspeak.common.IPath;
import org.lightjason.agentspeak.language.CCommon;
import org.lightjason.agentspeak.language.CRawTerm;
import org.lightjason.agentspeak.language.ITerm;
import org.lightjason.agentspeak.language.execution.IContext;
import org.lightjason.agentspeak.language.fuzzy.IFuzzyValue;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;


/**
 * gets multiple statistic values of a single statistic object.
 * The action returns different statistic values of a
 * single statistic object, the first argument is the statistic
 * object, all other values are string with statistic value names:
 * geometricmean, max, min, count, populationvariance, quadraticmean, secondmoment,
 * standarddeviation, sum, sumlog, sumsquare, variance, mean, kurtiosis
 *
 * {@code [SStd|SVar|SMean]  = .math/statistic/multiplestatisticvalue(Statistic, "standarddeviation", "variance", "mean" );}
 */
public final class CMultipleStatisticValue extends IBaseAction
{
    /**
     * serial id
     */
    private static final long serialVersionUID = -8921158589863826705L;
    /**
     * action name
     */
    private static final IPath NAME = namebyclass( CMultipleStatisticValue.class, "math", "statistic" );

    @Nonnull
    @Override
    public IPath name()
    {
        return NAME;
    }

    @Nonnegative
    @Override
    public int minimalArgumentNumber()
    {
        return 2;
    }

    @Nonnull
    @Override
    public Stream<IFuzzyValue<?>> execute( final boolean p_parallel, @Nonnull final IContext p_context,
                                           @Nonnull final List<ITerm> p_argument, @Nonnull final List<ITerm> p_return
    )
    {
        final List<ITerm> l_arguments = CCommon.flatten( p_argument ).collect( Collectors.toList() );

        if ( CCommon.isssignableto( l_arguments.get( 0 ), SummaryStatistics.class ) )
            l_arguments.stream()
                       .skip( 1 )
                       .map( ITerm::<String>raw )
                       .map( EStatisticValue::of )
                       .mapToDouble( i -> i.value( l_arguments.get( 0 ).<SummaryStatistics>raw() ) )
                       .boxed()
                       .map( CRawTerm::of )
                       .forEach( p_return::add );
        else
            l_arguments.stream()
                       .skip( 1 )
                       .map( ITerm::<String>raw )
                       .map( EStatisticValue::of )
                       .mapToDouble( i -> i.value( l_arguments.get( 0 ).<DescriptiveStatistics>raw() ) )
                       .boxed()
                       .map( CRawTerm::of )
                       .forEach( p_return::add );

        return Stream.of();
    }
}
