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
	public List<Speaking> analyzeToUri(String gcsUri, int minSpeakerCnt, int maxSpeakerCnt) throws Exception {
//	public String analyzeToUri(String gcsUri, int minSpeakerCnt, int maxSpeakerCnt) throws Exception {

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
				speaking.setStartTime(wordInfo.getStartTime().getSeconds() + "." + wordInfo.getStartTime().getNanos() / 100000000);
				speaking.setEndTime(wordInfo.getEndTime().getSeconds() + "." + wordInfo.getEndTime().getNanos() / 100000000);
				speakingList.add(speaking);

				// For each word, get all the words associated with one speaker, once the
				// speaker changes,
				// add a new line with the new speaker and their spoken words.
				StringBuilder speakerWords = new StringBuilder(String.format("Speaker %d: %s", wordInfo.getSpeakerTag(), wordInfo.getWord()));

				for (int i = 1; i < alternative.getWordsCount(); i++) {
					wordInfo = alternative.getWords(i);
					if (currentSpeakerTag == wordInfo.getSpeakerTag()) {
						speakerWords.append(" ");
						speakerWords.append(wordInfo.getWord());

						speaking.recordTalking(wordInfo.getWord());
						speaking.setEndTime(wordInfo.getEndTime().getSeconds() + "." + wordInfo.getEndTime().getNanos() / 100000000);

					} else {
						// 추가적인 저장
						speakerWords.append(String.format("\nSpeaker %d: %s", wordInfo.getSpeakerTag(), wordInfo.getWord()));
						currentSpeakerTag = wordInfo.getSpeakerTag();

						speaking = new Speaking(currentSpeakerTag);
						speaking.recordTalking(wordInfo.getWord());
						speaking.setStartTime(wordInfo.getStartTime().getSeconds() + "." + wordInfo.getStartTime().getNanos() / 100000000);
						speaking.setEndTime(wordInfo.getEndTime().getSeconds() + "." + wordInfo.getEndTime().getNanos() / 100000000);
						speakingList.add(speaking);

					}

				}

//				System.out.println(speakerWords.toString());
				responseBuilder.append(speakerWords.toString());
			}
//			return responseBuilder.toString();
			return speakingList;
		}
	}

	@Override
	public String analyze(String fileName, int minSpeakerCnt, int maxSpeakerCnt) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

}
