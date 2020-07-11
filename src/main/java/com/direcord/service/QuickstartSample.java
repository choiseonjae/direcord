package com.direcord.service;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.api.gax.longrunning.OperationFuture;
import com.google.cloud.speech.v1.LongRunningRecognizeMetadata;
import com.google.cloud.speech.v1.LongRunningRecognizeResponse;
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

	public static String callDistinguishSpeaker(SpeechClient speechClient, RecognitionConfig config,
			RecognitionAudio audio) throws InterruptedException, ExecutionException {
		try {
			// Use non-blocking call for getting file transcription
			OperationFuture<LongRunningRecognizeResponse, LongRunningRecognizeMetadata> response = speechClient
					.longRunningRecognizeAsync(config, audio);

			while (!response.isDone()) {
				System.out.println("Waiting for response...");
				Thread.sleep(10000);
			}

			// Speaker Tags are only included in the last result object, which has only one
			// alternative.
			LongRunningRecognizeResponse longRunningRecognizeResponse = response.get();
			logger.debug("[longRunningRecognizeResponse size] " + longRunningRecognizeResponse.getResultsCount());
			SpeechRecognitionAlternative alternative = longRunningRecognizeResponse
					.getResults(longRunningRecognizeResponse.getResultsCount() - 1).getAlternatives(0);

			logger.debug("[wordInfo size] " + alternative.getWordsCount());
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

			return speakerWords.toString();
		} catch (Exception e) {
			StackTraceElement[] stacks = new Throwable().getStackTrace();
			String errMsg = "";
			e.printStackTrace();
			for(StackTraceElement stack : stacks) {
				logger.error(stack.getMethodName());
				errMsg += stack.getClassName() + "." + stack.getMethodName() + "\n";
			}
			
			return errMsg;
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

			return callDistinguishSpeaker(speechClient, config, audio);
//			// Performs speech recognition on the audio file
//			RecognizeResponse response = speechClient.recognize(config, audio);
//			List<SpeechRecognitionResult> results = response.getResultsList();
//
//			StringBuilder transcription = new StringBuilder();
//
//			for (SpeechRecognitionResult result : results) {
//				// There can be several alternative transcripts for a given chunk of speech.
//				// Just use the
//				// first (most likely) one here.
//				SpeechRecognitionAlternative alternative = result.getAlternativesList().get(0);
//				System.out.printf("Transcription: %s%n", alternative.getTranscript());
//				transcription.append(alternative.getTranscript() + "\n");
//			}
//
//			return transcription.toString();
		}
	}
}