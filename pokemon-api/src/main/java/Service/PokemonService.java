package com.pokemon.pokemonapi.service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
public class PokemonService {

    private final String BASE_URL = "https://pokeapi.co/api/v2/pokemon/";

    private Map<String, Object> lastEncounter = null;
    private final List<Map<String, Object>> team = new ArrayList<>();

    private final RestTemplate restTemplate = new RestTemplate();

    // 🔥 Obtener máximo dinámico
    private int getMaxPokemon() {
        String url = "https://pokeapi.co/api/v2/pokemon-species?limit=0";
        Map<String, Object> response = restTemplate.getForObject(url, Map.class);
        return (int) response.get("count");
    }

    public Map<String, Object> encounterPokemon() {
        Random random = new Random();

        int max = getMaxPokemon();
        int id = random.nextInt(max) + 1;

        String url = BASE_URL + id;
        Map<String, Object> pokemon = restTemplate.getForObject(url, Map.class);

        lastEncounter = pokemon;
        return simplifyPokemon(pokemon);
    }

    public Map<String, Object> capturePokemon() {

        if (lastEncounter == null) {
            throw new RuntimeException("No hay ningún Pokémon disponible para capturar. Usa /encounter primero.");
        }

        if (team.size() >= 6) {
            throw new RuntimeException("Tu equipo está completo. Libera un Pokémon antes de capturar otro.");
        }

        int newId = (int) lastEncounter.get("id");

        boolean exists = team.stream()
                .anyMatch(p -> (int) p.get("id") == newId);

        if (exists) {
            throw new RuntimeException("Este Pokémon ya forma parte de tu equipo.");
        }

        Map<String, Object> simplified = simplifyPokemon(lastEncounter);
        team.add(simplified);

        Map<String, Object> response = new HashMap<>();
        response.put("captured", simplified);
        response.put("team", team);

        lastEncounter = null;

        return response;
    }

    public List<Map<String, Object>> getTeam() {
        return team;
    }

    // 🔍 Buscar por ID o nombre
    public Map<String, Object> getPokemonFromTeam(String identifier) {

        if (team.isEmpty()) {
            throw new RuntimeException("Tu equipo está vacío.");
        }

        return team.stream()
                .filter(p ->
                        String.valueOf(p.get("id")).equals(identifier) ||
                                ((String) p.get("name")).equalsIgnoreCase(identifier)
                )
                .findFirst()
                .orElseThrow(() ->
                        new RuntimeException("El Pokémon '" + identifier + "' no se encuentra en tu equipo.")
                );
    }

    // ❌ Eliminar Pokémon
    public Map<String, Object> deletePokemon(String identifier) {

        if (team.isEmpty()) {
            throw new RuntimeException("Tu equipo está vacío, no hay Pokémon para liberar.");
        }

        Iterator<Map<String, Object>> iterator = team.iterator();

        while (iterator.hasNext()) {
            Map<String, Object> p = iterator.next();

            if (String.valueOf(p.get("id")).equals(identifier) ||
                    ((String) p.get("name")).equalsIgnoreCase(identifier)) {

                iterator.remove();

                Map<String, Object> response = new HashMap<>();
                response.put("released", p);
                response.put("team", team);

                return response;
            }
        }

        throw new RuntimeException("El Pokémon '" + identifier + "' no se encuentra en tu equipo.");
    }

    // 🔥 Simplificar datos
    private Map<String, Object> simplifyPokemon(Map<String, Object> pokemon) {
        Map<String, Object> simple = new HashMap<>();

        simple.put("id", pokemon.get("id"));
        simple.put("name", pokemon.get("name"));

        List<Map<String, Object>> types = (List<Map<String, Object>>) pokemon.get("types");
        List<String> typeNames = new ArrayList<>();

        for (Map<String, Object> t : types) {
            Map<String, Object> type = (Map<String, Object>) t.get("type");
            typeNames.add((String) type.get("name"));
        }

        simple.put("types", typeNames);

        return simple;
    }
}