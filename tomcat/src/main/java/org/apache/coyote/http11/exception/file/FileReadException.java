package org.apache.coyote.http11.exception.file;

public class FileReadException extends RuntimeException {

    public FileReadException() {
        super("파일을 읽을 수 없습니다.");
    }
}
