package com.example.analyzerneo4j.controller.tutorial;

import com.example.analyzerneo4j.entity.tutorial.MovieEntity;
import com.example.analyzerneo4j.repository.tutorial.MovieRepository;
import com.example.analyzerneo4j.repository.tutorial.PersonRepository;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/movies")
public class MovieController {
    private final MovieRepository movieRepository;
    private final PersonRepository personRepository;

    public MovieController(MovieRepository movieRepository, PersonRepository personRepository) {
        this.movieRepository = movieRepository;
        this.personRepository = personRepository;
    }

    @PutMapping
    Mono<MovieEntity> createOrUpdateMovie(@RequestBody MovieEntity newMovie) {
        return movieRepository.save(newMovie);
    }

    @PutMapping("/acts")
    @Transactional
    void createOrUpdateActs(@RequestBody RelationshipRequest request) {
        movieRepository.saveActedIn(request, personRepository);
    }

    @GetMapping(value = {"", "/"}, produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    Flux<MovieEntity> getMovies() {
        return movieRepository.findAll();
    }

    @GetMapping("/by-title")
    Flux<MovieEntity> byTitle(@RequestParam String title) {
        return movieRepository.findOneByTitle(title);
    }

    @DeleteMapping("/{id}")
    Mono<Void> delete(@PathVariable String id) {
        return movieRepository.deleteById(id);
    }
}
