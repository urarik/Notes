package com.example.analyzerneo4j.repository.tutorial;


import com.example.analyzerneo4j.controller.tutorial.RelationshipRequest;
import com.example.analyzerneo4j.entity.tutorial.ActedIn;
import com.example.analyzerneo4j.entity.tutorial.MovieEntity;
import com.example.analyzerneo4j.entity.tutorial.PersonEntity;
import org.springframework.data.neo4j.repository.ReactiveNeo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public interface MovieRepository extends ReactiveNeo4jRepository<MovieEntity, String> {
        Flux<MovieEntity> findOneByTitle(String title);

        default void saveActedIn(RelationshipRequest request, PersonRepository personRepository) {
            MovieEntity movie = this.findOneByTitle(request.movie).blockFirst();
            PersonEntity person = personRepository.findById(request.person).block();
            assert movie != null;
            movie.getActors().add(new ActedIn(List.of(request.role), person));
            System.out.println(movie);
            this.save(movie).subscribe();
        }
}
