package com.direcord.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Main {

	public static void main(String[] args) {
		Path path = Paths.get("./src/main/resources/short_audio.flac");
		try {
			System.out.println("[path] " + path.toRealPath());
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		try {
			byte[] content = Files.readAllBytes(path);
			System.out.println(new String(content));
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

}
