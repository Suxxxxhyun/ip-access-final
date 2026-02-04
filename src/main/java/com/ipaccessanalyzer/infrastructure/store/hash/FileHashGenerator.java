package com.ipaccessanalyzer.infrastructure.store.hash;

import java.io.IOException;
import java.io.InputStream;

import org.springframework.stereotype.Component;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.web.multipart.MultipartFile;

@Component
public class FileHashGenerator {

	public String sha256(MultipartFile file) {
		try (InputStream is = file.getInputStream()) {
			return DigestUtils.sha256Hex(is);
		} catch (IOException e) {
			throw new IllegalStateException("파일 해시 생성 실패", e);
		}
	}
}
