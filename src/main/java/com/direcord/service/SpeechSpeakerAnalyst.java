package com.direcord.service;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import com.direcord.model.Speaking;
import com.google.api.gax.longrunning.OperationFuture;
import com.google.cloud.speech.v1.LongRunningRecognizeMetadata;
import com.google.cloud.speech.v1.LongRunningRecognizeResponse;
import com.google.cloud.speech.v1.RecognitionAudio;
import com.google.cloud.speech.v1.RecognitionConfig;
import com.google.cloud.speech.v1.RecognitionConfig.AudioEncoding;
import com.google.cloud.speech.v1.SpeakerDiarizationConfig;
import com.google.cloud.speech.v1.SpeechClient;
import com.google.cloud.speech.v1.SpeechRecognitionAlternative;
import com.google.cloud.speech.v1.SpeechRecognitionResult;
import com.google.cloud.speech.v1.WordInfo;
import com.google.protobuf.ByteString;

public class SpeechSpeakerAnalyst implements SpeechAnalyst {

	private static SpeechSpeakerAnalyst speakerAnalyst = new SpeechSpeakerAnalyst();

	private SpeechSpeakerAnalyst() {
	}

	public static SpeechSpeakerAnalyst getInstance() {
		return speakerAnalyst;
	}

	/**
	 * Transcribe the given audio file using speaker diarization.
	 *
	 * @param fileName      the path to an audio file.
	 * @param maxSpeakerCnt
	 * @param minSpeakerCnt
	 */
	public String analyze(String fileName, int minSpeakerCnt, int maxSpeakerCnt) throws Exception {
		Path path = Paths.get("./WEB-INF/classes/" + fileName);
		byte[] content = Files.readAllBytes(path);

		try (SpeechClient speechClient = SpeechClient.create()) {
			// Get the contents of the local audio file
			RecognitionAudio recognitionAudio = RecognitionAudio.newBuilder().setContent(ByteString.copyFrom(content))
					.build();

			SpeakerDiarizationConfig speakerDiarizationConfig = SpeakerDiarizationConfig.newBuilder()
					.setEnableSpeakerDiarization(true).setMinSpeakerCount(minSpeakerCnt)
					.setMaxSpeakerCount(maxSpeakerCnt).build();

			// Configure request to enable Speaker diarization
			int rateHertz = 44100; // flac - 44100, default - 8000
			int channelCount = 2;

			AudioEncoding encoding = null;
			if (fileName.endsWith(".flac")) {
				encoding = AudioEncoding.FLAC;
			} else {
				encoding = AudioEncoding.LINEAR16;
			}

			RecognitionConfig config = RecognitionConfig.newBuilder().setEncoding(encoding)
					.setAudioChannelCount(channelCount).setLanguageCode("en-US").setSampleRateHertz(rateHertz)
					.setDiarizationConfig(speakerDiarizationConfig).build();

			// Perform the transcription request 잠깐 pause
//			RecognizeResponse recognizeResponse = speechClient.recognize(config, recognitionAudio);

			//////////////////////////////// 기존 변경 안.
			// Use non-blocking call for getting file transcription
			OperationFuture<LongRunningRecognizeResponse, LongRunningRecognizeMetadata> response = speechClient
					.longRunningRecognizeAsync(config, recognitionAudio);
			while (!response.isDone()) {
				System.out.println("Waiting for response...");
				Thread.sleep(10000);
			}

			List<SpeechRecognitionResult> results = response.get().getResultsList();

			StringBuilder responseBuilder = new StringBuilder();
			for (SpeechRecognitionResult result : results) {
				// There can be several alternative transcripts for a given chunk of speech.
				// Just use the
				// first (most likely) one here.
				SpeechRecognitionAlternative alternative = result.getAlternativesList().get(0);

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
						speakerWords.append(
								String.format("\nSpeaker %d: %s", wordInfo.getSpeakerTag(), wordInfo.getWord()));
						currentSpeakerTag = wordInfo.getSpeakerTag();
					}
				}

				System.out.println(speakerWords.toString());
				responseBuilder.append(speakerWords.toString());
			}
			return responseBuilder.toString();
		}
		///////////////////////////

		// Speaker Tags are only included in the last result object, which has only one
		// alternative.
//			SpeechRecognitionAlternative alternative = recognizeResponse
//					.getResults(recognizeResponse.getResultsCount() - 1).getAlternatives(0);
//
//			// The alternative is made up of WordInfo objects that contain the speaker_tag.
//			WordInfo wordInfo = alternative.getWords(0);
//			int currentSpeakerTag = wordInfo.getSpeakerTag();
//
//			// For each word, get all the words associated with one speaker, once the
//			// speaker changes,
//			// add a new line with the new speaker and their spoken words.
//			StringBuilder speakerWords = new StringBuilder(
//					String.format("Speaker %d: %s", wordInfo.getSpeakerTag(), wordInfo.getWord()));
//
//			for (int i = 1; i < alternative.getWordsCount(); i++) {
//				wordInfo = alternative.getWords(i);
//				if (currentSpeakerTag == wordInfo.getSpeakerTag()) {
//					speakerWords.append(" ");
//					speakerWords.append(wordInfo.getWord());
//				} else {
//					speakerWords
//							.append(String.format("\nSpeaker %d: %s", wordInfo.getSpeakerTag(), wordInfo.getWord()));
//					currentSpeakerTag = wordInfo.getSpeakerTag();
//				}
//			}
//
//			System.out.println(speakerWords.toString());
//			return speakerWords.toString();
	}

	/**
	 * Transcribe the given audio file using speaker diarization.
	 *
	 * @param fileName      the path to an audio file.
	 * @param maxSpeakerCnt
	 * @param minSpeakerCnt
	 */
	public List<Speaking> analyzeToUri(String gcsUri, int minSpeakerCnt, int maxSpeakerCnt) throws Exception {

		try (SpeechClient speechClient = SpeechClient.create()) {
			// Get the contents of the local audio file
			RecognitionAudio recognitionAudio = RecognitionAudio.newBuilder().setUri(gcsUri).build();

			SpeakerDiarizationConfig speakerDiarizationConfig = SpeakerDiarizationConfig.newBuilder()
					.setEnableSpeakerDiarization(true).setMinSpeakerCount(minSpeakerCnt)
					.setMaxSpeakerCount(maxSpeakerCnt).build();

			// Configure request to enable Speaker diarization
			int rateHertz = 44100; // flac - 44100, default - 8000
			int channelCount = 2;
			String language = "en-US";
			AudioEncoding encoding = AudioEncoding.FLAC;
			boolean isPunctuation = true; // 구두점(!, ?, . 등)을 자동으로 삽입한다.
			boolean isTimeOffSet = true;

			RecognitionConfig config = RecognitionConfig.newBuilder().setEncoding(encoding)
					.setAudioChannelCount(channelCount).setLanguageCode(language).setSampleRateHertz(rateHertz)
					.setEnableAutomaticPunctuation(isPunctuation).setEnableWordTimeOffsets(isTimeOffSet)
					.setDiarizationConfig(speakerDiarizationConfig).build();

			//////////////////////////////// 기존 변경 안.
			// Use non-blocking call for getting file transcription
			OperationFuture<LongRunningRecognizeResponse, LongRunningRecognizeMetadata> response = speechClient
					.longRunningRecognizeAsync(config, recognitionAudio);
			while (!response.isDone()) {
				System.out.println("Waiting for response...");
				Thread.sleep(10000);
			}

			List<SpeechRecognitionResult> results = response.get().getResultsList();

			StringBuilder responseBuilder = new StringBuilder();

			List<Speaking> speakingList = new ArrayList<>();

			for (SpeechRecognitionResult result : results) {
				// There can be several alternative transcripts for a given chunk of speech.
				// Just use the
				// first (most likely) one here.
				SpeechRecognitionAlternative alternative = result.getAlternativesList().get(0);

				// The alternative is made up of WordInfo objects that contain the speaker_tag.
				WordInfo wordInfo = alternative.getWords(0);
				int currentSpeakerTag = wordInfo.getSpeakerTag();
				
				// 초기 Speaker
				Speaking speaking = new Speaking(currentSpeakerTag);
				speaking.recordTalking(wordInfo.getWord());
				speaking.setStartTime(wordInfo.getStartTime());

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
						
						speaking.recordTalking(wordInfo.getWord());
						speaking.setEndTime(wordInfo.getEndTime());

						System.out.printf("\t%s.%s sec - %s.%s sec\n", wordInfo.getStartTime().getSeconds(),
								wordInfo.getStartTime().getNanos() / 100000000, wordInfo.getEndTime().getSeconds(),
								wordInfo.getEndTime().getNanos() / 100000000);
					} else {
						// 추가적인 저장
						speakingList.add(speaking);
						
						speakerWords.append(
								String.format("\nSpeaker %d: %s", wordInfo.getSpeakerTag(), wordInfo.getWord()));
						currentSpeakerTag = wordInfo.getSpeakerTag();
						
						speaking = new Speaking(currentSpeakerTag);
						speaking.recordTalking(wordInfo.getWord());
						speaking.setStartTime(wordInfo.getStartTime());
						
					}

					
				}

				System.out.println(speakerWords.toString());
				responseBuilder.append(speakerWords.toString());
			}
			return speakingList;
		}
	}

}
