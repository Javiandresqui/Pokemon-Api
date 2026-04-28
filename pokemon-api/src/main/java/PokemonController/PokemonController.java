package com.pokemon.pokemonapi.controller;

import com.pokemon.pokemonapi.service.PokemonService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
public class PokemonController {

    private final PokemonService pokemonService;

    public PokemonController(PokemonService pokemonService) {
        this.pokemonService = pokemonService;
    }

    @GetMapping("/pokemon/encounter")
    public Map<String, Object> encounter() {
        return pokemonService.encounterPokemon();
    }

    @PostMapping("/pokemon/capture")
    public Map<String, Object> capture() {
        return pokemonService.capturePokemon();
    }

    @GetMapping("/pokemon/team")
    public List<Map<String, Object>> getTeam() {
        return pokemonService.getTeam();
    }

    @GetMapping("/pokemon/team/{identifier}")
    public Map<String, Object> getPokemon(@PathVariable String identifier) {
        return pokemonService.getPokemonFromTeam(identifier);
    }

    @DeleteMapping("/pokemon/team/{identifier}")
    public Map<String, Object> deletePokemon(@PathVariable String identifier) {
        return pokemonService.deletePokemon(identifier);
    }
}