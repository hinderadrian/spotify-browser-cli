package com.hinder.spotifybrowsercli;

import com.beust.jcommander.JCommander;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.hinder.spotifybrowsercli.api.APISpotify;
import com.hinder.spotifybrowsercli.cli.CLIArguments;
import com.hinder.spotifybrowsercli.cli.CLIFunctions;
import com.hinder.spotifybrowsercli.domain.Tracks;

import java.util.*;
import java.util.stream.Stream;

import static com.hinder.spotifybrowsercli.CommanderFunctions.buildCommanderWithName;
import static com.hinder.spotifybrowsercli.CommanderFunctions.parseArguments;
import static com.hinder.spotifybrowsercli.api.APIFunctions.buildAPI;

public class SpotifyBrowser {
	public static void main(String[] args) {
		System.out.println("Starting the search..\n");

		Gson gson = new GsonBuilder().setPrettyPrinting().create();

		JCommander jCommander = buildCommanderWithName("spotify-browser", CLIArguments::newInstance);

		Stream<CLIArguments> streamOfCLI = parseArguments(jCommander, args, JCommander::usage)
				                                   .orElse(Collections.emptyList())
				                                   .stream()
				                                   .map(obj -> (CLIArguments) obj);

		Optional<CLIArguments> cliArgumentsOptional =
				streamOfCLI
						.filter(cli -> !cli.isHelp())
						.filter(cli -> cli.getQuery() != null)
						.findFirst();

		cliArgumentsOptional.map(CLIFunctions::toMap)
				.map(SpotifyBrowser::executeRequest)
				.orElse(Stream.empty())
				.map(gson::toJson)
				.forEach(System.out::println);
	}

	private static Stream<Tracks> executeRequest(Map<String, Object> params) {
		APISpotify api = buildAPI(APISpotify.class, "https://api.spotify.com/v1");

		Map<String, Object> headersMap = new HashMap<>();
		headersMap.put("Authorization", String.format("Bearer %s", "BQDrB9FFOYPZ5fKI_baafw7v1EMtmWi3njYyzXc-I97Tysg8Z9ebLveW9KNcLMOLVK7FRjo1vP-OkkIReNnKR0iBo3UZ52dzbuhA_aqDvtn6HHMzyfE"));
		headersMap.put("Content-Type", "application/json");


		return Stream.of(params)
				       .map(paramsValue -> api.searchResult(headersMap, paramsValue))
				       .map(data -> data.getTracks());
	}
}
