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

import org.lightjason.agentspeak.action.IBaseAction;
import org.lightjason.agentspeak.error.context.CExecutionIllegealArgumentException;
import org.lightjason.agentspeak.language.CCommon;
import org.lightjason.agentspeak.language.CRawTerm;
import org.lightjason.agentspeak.language.ITerm;
import org.lightjason.agentspeak.language.execution.IContext;
import org.lightjason.agentspeak.language.fuzzy.IFuzzyValue;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import java.util.stream.Stream;


/**
 * abstract class for creating a selection one element
 * of a list based on a fitness weight
 */
public abstract class IBaseSelection extends IBaseAction
{
    /**
     * serial id
     */
    private static final long serialVersionUID = -365949510289020495L;

    @Nonnegative
    @Override
    public final int minimalArgumentNumber()
    {
        return 2 + this.additionalArgumentNumber();
    }

    @Nonnull
    @Override
    @SuppressWarnings( "unchecked" )
    public final Stream<IFuzzyValue<?>> execute( final boolean p_parallel, @Nonnull final IContext p_context,
                                                 @Nonnull final List<ITerm> p_argument, @Nonnull final List<ITerm> p_return )
    {
        // first and second parameter are lists with values or variables, other values are possible passing arguments
        final List<Object> l_items = p_argument.get( 0 )
                                               .<List<Object>>raw()
                                               .stream()
                                               .map( i -> IBaseSelection.type( p_context, i ) )
                                               .collect( Collectors.toList() );

        final List<Double> l_weight = this.weight(
            l_items,

            p_argument.get( 1 )
                      .<List<Object>>raw()
                      .stream()
                      .map( i -> IBaseSelection.<Number>type( p_context, i ) )
                      .map( Number::doubleValue )
                      .map( Math::abs ),

            p_argument.subList( 2, p_argument.size() )
        );

        if ( l_items.isEmpty() || l_items.size() != l_weight.size() )
            throw new CExecutionIllegealArgumentException(
                p_context, org.lightjason.agentspeak.common.CCommon.languagestring( IBaseSelection.class, "novaluepresent" )
            );

        // select a random value and scale with the sum and caluclate result item
        double l_random = ThreadLocalRandom.current().nextDouble() * l_weight.stream().mapToDouble( i -> i ).sum();
        for ( int i = 0; i < l_weight.size(); i++ )
        {
            l_random -= l_weight.get( i );
            if ( l_random <= 0 )
            {
                p_return.add( CRawTerm.of( l_items.get( i ) ) );
                return Stream.empty();
            }
        }

        // on rounding error return last element
        p_return.add( CRawTerm.of( l_items.get( l_items.size() - 1 ) ) );
        return Stream.empty();
    }

    /**
     * type converting
     *
     * @param p_context context
     * @param p_data data input
     * @tparam T return type
     * @return return value
     */
    @SuppressWarnings( "unchecked" )
    private static <T> T type( @Nonnull final IContext p_context, @Nonnull final Object p_data )
    {
        return p_data instanceof ITerm
               ?  CCommon.replacebycontext( p_context, (ITerm) p_data ).raw()
               : (T) p_data;
    }

    /**
     * modifies the weights
     *
     * @param p_items item list
     * @param p_values stream of weights
     * @param p_argument additional arguments
     * @return list with weights
     */
    @Nonnull
    protected abstract List<Double> weight( @Nonnull final List<?> p_items, @Nonnull final Stream<Double> p_values, @Nonnull final List<ITerm> p_argument );

    /**
     * number of additional parameter
     *
     * @return additional number
     */
    protected int additionalArgumentNumber()
    {
        return 0;
    }
}
