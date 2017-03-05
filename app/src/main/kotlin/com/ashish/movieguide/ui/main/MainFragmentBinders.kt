package com.ashish.movieguide.ui.main

import com.ashish.movieguide.di.multibindings.fragment.FragmentComponentBuilder
import com.ashish.movieguide.di.multibindings.fragment.FragmentKey
import com.ashish.movieguide.ui.discover.movie.DiscoverMovieComponent
import com.ashish.movieguide.ui.discover.movie.DiscoverMovieFragment
import com.ashish.movieguide.ui.discover.tvshow.DiscoverTVShowComponent
import com.ashish.movieguide.ui.discover.tvshow.DiscoverTVShowFragment
import com.ashish.movieguide.ui.movie.list.MovieComponent
import com.ashish.movieguide.ui.movie.list.MovieFragment
import com.ashish.movieguide.ui.people.list.PeopleComponent
import com.ashish.movieguide.ui.people.list.PeopleFragment
import com.ashish.movieguide.ui.personal.movie.PersonalMovieComponent
import com.ashish.movieguide.ui.personal.movie.PersonalMovieFragment
import com.ashish.movieguide.ui.personal.tvshow.PersonalTVShowComponent
import com.ashish.movieguide.ui.personal.tvshow.PersonalTVShowFragment
import com.ashish.movieguide.ui.tvshow.list.TVShowComponent
import com.ashish.movieguide.ui.tvshow.list.TVShowFragment
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

/**
 * This will bind all the Fragment's subcomponents that are part of MainActivity
 * into a map using multibindings.
 */
@Module(subcomponents = arrayOf(
        MovieComponent::class,
        TVShowComponent::class,
        PeopleComponent::class,
        DiscoverMovieComponent::class,
        DiscoverTVShowComponent::class,
        PersonalMovieComponent::class,
        PersonalTVShowComponent::class
))
abstract class MainFragmentBinders {

    @Binds
    @IntoMap
    @FragmentKey(MovieFragment::class)
    abstract fun movieComponentBuilder(builder: MovieComponent.Builder): FragmentComponentBuilder<*, *>

    @Binds
    @IntoMap
    @FragmentKey(TVShowFragment::class)
    abstract fun tvShowComponentBuilder(builder: TVShowComponent.Builder): FragmentComponentBuilder<*, *>

    @Binds
    @IntoMap
    @FragmentKey(PeopleFragment::class)
    abstract fun peopleComponentBuilder(builder: PeopleComponent.Builder): FragmentComponentBuilder<*, *>

    @Binds
    @IntoMap
    @FragmentKey(DiscoverMovieFragment::class)
    abstract fun discoverMovieComponentBuilder(builder: DiscoverMovieComponent.Builder): FragmentComponentBuilder<*, *>

    @Binds
    @IntoMap
    @FragmentKey(DiscoverTVShowFragment::class)
    abstract fun discoverTVShowComponentBuilder(
            builder: DiscoverTVShowComponent.Builder): FragmentComponentBuilder<*, *>

    @Binds
    @IntoMap
    @FragmentKey(PersonalMovieFragment::class)
    abstract fun favoriteMovieComponentBuilder(builder: PersonalMovieComponent.Builder): FragmentComponentBuilder<*, *>

    @Binds
    @IntoMap
    @FragmentKey(PersonalTVShowFragment::class)
    abstract fun favoriteTVShowComponentBuilder(
            builder: PersonalTVShowComponent.Builder): FragmentComponentBuilder<*, *>
}