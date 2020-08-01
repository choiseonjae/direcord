package com.direcord.service;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import com.google.cloud.speech.v1.*;
import com.google.cloud.speech.v1.RecognitionConfig.AudioEncoding;
import com.google.protobuf.ByteString;

public class SpeechSpeakerAnalyst implements SpeechAnalyst {
	
	private static SpeechSpeakerAnalyst speakerAnalyst = new SpeechSpeakerAnalyst();
	private SpeechSpeakerAnalyst() {}
	public static SpeechSpeakerAnalyst getInstance() {
		return speakerAnalyst;
	}
	/**
	 * Transcribe the given audio file using speaker diarization.
	 *
	 * @param fileName the path to an audio file.
	 */
	public String analyze(String fileName) throws Exception {
		Path path = Paths.get("./WEB-INF/classes/audio.flac");
		System.out.println("[path] " + path);
		System.out.println("[resources] " + getClass().getResource("/audio.flac").getPath());
		System.out.println("[real path] " + path.toRealPath());
		byte[] content = Files.readAllBytes(path);

		try (SpeechClient speechClient = SpeechClient.create()) {
			// Get the contents of the local audio file
			RecognitionAudio recognitionAudio = RecognitionAudio.newBuilder().setContent(ByteString.copyFrom(content))
					.build();

			SpeakerDiarizationConfig speakerDiarizationConfig = SpeakerDiarizationConfig.newBuilder()
					.setEnableSpeakerDiarization(true).setMinSpeakerCount(2).setMaxSpeakerCount(2).build();

			// Configure request to enable Speaker diarization
			int rateHertz = 8000; // flac - 44100
			RecognitionConfig config = RecognitionConfig.newBuilder().setEncoding(AudioEncoding.LINEAR16).setAudioChannelCount(2)
					.setLanguageCode("en-US").setSampleRateHertz(rateHertz).setDiarizationConfig(speakerDiarizationConfig)
					.build();

			// Perform the transcription request
			RecognizeResponse recognizeResponse = speechClient.recognize(config, recognitionAudio);

			// Speaker Tags are only included in the last result object, which has only one
			// alternative.
			SpeechRecognitionAlternative alternative = recognizeResponse
					.getResults(recognizeResponse.getResultsCount() - 1).getAlternatives(0);

			// The alternative is made up of WordInfo objects that contain the speaker_tag.
			WordInfo wordInfo = alternative.getWords(0);
			int currentSpeakerTag = wordInfo.getSpeakerTag();

			// For each word, get all the words associated with one speaker, once the
			// speaker changes,
			// add a new line with the new speaker and their spoken words.
			StringBuilder speakerWords = new StringBuilder(
					String.format("Speaker %d: %s", wordInfo.getSpeakerTag(), wordInfo.getWord()));

			for (int i = 1; i < alternative.getWordsCount(); i++) {
				wordInfo = alternative.getWords(i);
				if (currentSpeakerTag == wordInfo.getSpeakerTag()) {
					speakerWords.append(" ");
					speakerWords.append(wordInfo.getWord());
				} else {
					speakerWords
							.append(String.format("\nSpeaker %d: %s", wordInfo.getSpeakerTag(), wordInfo.getWord()));
					currentSpeakerTag = wordInfo.getSpeakerTag();
				}
			}

			System.out.println(speakerWords.toString());
			return speakerWords.toString();
		}
	}
}
