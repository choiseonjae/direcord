package com.direcord.service;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//Imports the Google Cloud client library
import com.google.cloud.speech.v1.RecognitionAudio;
import com.google.cloud.speech.v1.RecognitionConfig;
import com.google.cloud.speech.v1.RecognitionConfig.AudioEncoding;
import com.google.cloud.speech.v1.RecognizeResponse;
import com.google.cloud.speech.v1.SpeakerDiarizationConfig;
import com.google.cloud.speech.v1.SpeechClient;
import com.google.cloud.speech.v1.SpeechRecognitionAlternative;
import com.google.cloud.speech.v1.SpeechRecognitionResult;
import com.google.cloud.speech.v1.WordInfo;
import com.google.protobuf.ByteString;

public class QuickstartSample {

	private static final Logger logger = LoggerFactory.getLogger(QuickstartSample.class);

	/** Demonstrates using the Speech API to transcribe an audio file. */
	public static String callSTTOfWav(String fileName) throws Exception {
		// Instantiates a client
		try (SpeechClient speechClient = SpeechClient.create()) {

			// The path to the audio file to transcribe
			fileName = "/" + fileName;

			fileName = QuickstartSample.class.getResource(fileName).getPath();

			// Reads the audio file into memory
			Path path = Paths.get(fileName);
			byte[] data = Files.readAllBytes(path);
			ByteString audioBytes = ByteString.copyFrom(data);

			// Speech setting
			SpeakerDiarizationConfig speakerDiarizationConfig = SpeakerDiarizationConfig.newBuilder()
					.setEnableSpeakerDiarization(true).setMinSpeakerCount(2).setMaxSpeakerCount(2).build();

			// Builds the sync recognize request
			RecognitionConfig config = RecognitionConfig.newBuilder().setEncoding(AudioEncoding.LINEAR16)
					.setSampleRateHertz(16000).setDiarizationConfig(speakerDiarizationConfig).setLanguageCode("en-US")
					.setAudioChannelCount(2).build();
			RecognitionAudio audio = RecognitionAudio.newBuilder().setContent(audioBytes).build();

			// Performs speech recognition on the audio file
			RecognizeResponse response = speechClient.recognize(config, audio);

			List<SpeechRecognitionResult> results = response.getResultsList();

			StringBuilder transcription = new StringBuilder();

			for (SpeechRecognitionResult result : results) {
				// There can be several alternative transcripts for a given chunk of speech.
				// Just use the
				// first (most likely) one here.
				SpeechRecognitionAlternative alternative = result.getAlternativesList().get(0);
				System.out.printf("Transcription: %s%n", alternative.getTranscript());
				transcription.append(alternative.getTranscript() + "\n");
			}

			return transcription.toString();
		}
	}

	public static String callDistinguishSpeaker(String fileName) throws InterruptedException, ExecutionException {
		try {
			// The path to the audio file to transcribe
			fileName = "/" + fileName;

			fileName = QuickstartSample.class.getResource(fileName).getPath();
			
			Path path = Paths.get(fileName);
			byte[] content = Files.readAllBytes(path);

			try (SpeechClient speechClient = SpeechClient.create()) {
				// Get the contents of the local audio file
				RecognitionAudio recognitionAudio = RecognitionAudio.newBuilder()
						.setContent(ByteString.copyFrom(content)).build();
				
				System.out.println("1");

				SpeakerDiarizationConfig speakerDiarizationConfig = SpeakerDiarizationConfig.newBuilder()
						.setEnableSpeakerDiarization(true).setMinSpeakerCount(2).setMaxSpeakerCount(2).build();
				
				System.out.println("2");

				// Configure request to enable Speaker diarization
				RecognitionConfig config = RecognitionConfig.newBuilder().setEncoding(AudioEncoding.FLAC)
						.setLanguageCode("en-US").setSampleRateHertz(44100).setAudioChannelCount(2)
						.setDiarizationConfig(speakerDiarizationConfig).build();
				
				System.out.println("3");

				// Perform the transcription request
				RecognizeResponse recognizeResponse = speechClient.recognize(config, recognitionAudio);
				
				System.out.println("4");
				System.out.println("[recognizeResponse size] " + recognizeResponse.getResultsCount());
				System.out.println("[recognizeResponse getAlternativesCount size] " + recognizeResponse.getResults(recognizeResponse.getResultsCount() - 1).getAlternativesCount());

				// Speaker Tags are only included in the last result object, which has only one
				// alternative.
				SpeechRecognitionAlternative alternative = recognizeResponse
						.getResults(recognizeResponse.getResultsCount() - 1).getAlternatives(0);
				
				System.out.println("5");
				System.out.println("alternative.getWordsCount()] " + alternative.getWordsCount());

				// The alternative is made up of WordInfo objects that contain the speaker_tag.
				WordInfo wordInfo = alternative.getWords(0);
				
				System.out.println("6");
				
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
						speakerWords.append(
								String.format("\nSpeaker %d: %s", wordInfo.getSpeakerTag(), wordInfo.getWord()));
						currentSpeakerTag = wordInfo.getSpeakerTag();
					}
				}

				System.out.println(speakerWords.toString());
				logger.info("LOGGER INFO 되는지?");
				
				return speakerWords.toString();
			}
		} catch (Exception e) {
			e.printStackTrace();
			return e.getMessage();
			
		}
	}

	/** Demonstrates using the Speech API to transcribe an audio file. */
	public static String callSTTOfFlac(String fileName) throws Exception {
		// Instantiates a client
		try (SpeechClient speechClient = SpeechClient.create()) {

			// The path to the audio file to transcribe
			fileName = "/" + fileName;

			fileName = QuickstartSample.class.getResource(fileName).getPath();

			// Reads the audio file into memory
			Path path = Paths.get(fileName);
			byte[] data = Files.readAllBytes(path);
			ByteString audioBytes = ByteString.copyFrom(data);

			// Builds the sync recognize request
			RecognitionConfig config = RecognitionConfig.newBuilder().setEncoding(AudioEncoding.FLAC)
					.setSampleRateHertz(44100).setLanguageCode("en-US").setAudioChannelCount(2).build();
			RecognitionAudio audio = RecognitionAudio.newBuilder().setContent(audioBytes).build();

			// Performs speech recognition on the audio file
			RecognizeResponse response = speechClient.recognize(config, audio);
			List<SpeechRecognitionResult> results = response.getResultsList();

			StringBuilder transcription = new StringBuilder();

			for (SpeechRecognitionResult result : results) {
				// There can be several alternative transcripts for a given chunk of speech.
				// Just use the
				// first (most likely) one here.
				SpeechRecognitionAlternative alternative = result.getAlternativesList().get(0);
				System.out.printf("Transcription: %s%n", alternative.getTranscript());
				transcription.append(alternative.getTranscript() + "\n");
			}

			return transcription.toString();
		}
		
	}
}