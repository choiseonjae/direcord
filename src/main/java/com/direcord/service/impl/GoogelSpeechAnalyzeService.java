package com.direcord.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.direcord.model.Speaking;
import com.direcord.service.SpeechAnalyzeService;
import com.google.api.gax.longrunning.OperationFuture;
import com.google.cloud.speech.v1.LongRunningRecognizeMetadata;
import com.google.cloud.speech.v1.LongRunningRecognizeResponse;
import com.google.cloud.speech.v1.RecognitionAudio;
import com.google.cloud.speech.v1.RecognitionConfig;
import com.google.cloud.speech.v1.SpeakerDiarizationConfig;
import com.google.cloud.speech.v1.SpeechClient;
import com.google.cloud.speech.v1.SpeechRecognitionAlternative;
import com.google.cloud.speech.v1.SpeechRecognitionResult;
import com.google.cloud.speech.v1.WordInfo;

@Service
public class GoogelSpeechAnalyzeService implements SpeechAnalyzeService {

	/**
	 * Transcribe the given audio file using speaker diarization.
	 *
	 * @param Speech의 path
	 * @param 최소      화자 수
	 * @param 최대      화자 수
	 */
	@Override
	public List<Speaking> analyze(String uri, int minSpeakerCnt, int maxSpeakerCnt, String language) throws Exception {
		try (SpeechClient speechClient = SpeechClient.create()) {
			String baseUri = "gs://direcord-283711.appspot.com/";
			uri = baseUri + uri;

			// Get the contents of the local audio file
			RecognitionAudio recognitionAudio = RecognitionAudio.newBuilder().setUri(uri).build();

			SpeakerDiarizationConfig speakerDiarizationConfig = SpeakerDiarizationConfig.newBuilder()
					.setEnableSpeakerDiarization(true).setMinSpeakerCount(minSpeakerCnt)
					.setMaxSpeakerCount(maxSpeakerCnt).build();

			// Configure request to enable Speaker diarization
			int rateHertz = 44100; // flac - 44100, default - 8000
			int channelCount = 2;
//			String language = "ko-KR"; // en-US / ko-KR
//			AudioEncoding encoding = AudioEncoding.LINEAR16;
			boolean isPunctuation = true; // 구두점(!, ?, . 등)을 자동으로 삽입한다.
			boolean isTimeOffSet = true;

			RecognitionConfig config = RecognitionConfig.newBuilder()
//					.setEncoding(encoding)
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
				speaking.setStartTime(
						wordInfo.getStartTime().getSeconds() + "." + wordInfo.getStartTime().getNanos() / 100000000);
				speaking.setEndTime(
						wordInfo.getEndTime().getSeconds() + "." + wordInfo.getEndTime().getNanos() / 100000000);
				speakingList.add(speaking);

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
						speaking.setEndTime(wordInfo.getEndTime().getSeconds() + "."
								+ wordInfo.getEndTime().getNanos() / 100000000);

					} else {
						// 추가적인 저장
						speakerWords.append(
								String.format("\nSpeaker %d: %s", wordInfo.getSpeakerTag(), wordInfo.getWord()));
						currentSpeakerTag = wordInfo.getSpeakerTag();

						speaking = new Speaking(currentSpeakerTag);
						speaking.recordTalking(wordInfo.getWord());
						speaking.setStartTime(wordInfo.getStartTime().getSeconds() + "."
								+ wordInfo.getStartTime().getNanos() / 100000000);
						speaking.setEndTime(wordInfo.getEndTime().getSeconds() + "."
								+ wordInfo.getEndTime().getNanos() / 100000000);
						speakingList.add(speaking);

					}

				}

				responseBuilder.append(speakerWords.toString());
			}
			return speakingList;
		}
	}

}
