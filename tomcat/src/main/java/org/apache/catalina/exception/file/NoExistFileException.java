package org.apache.catalina.exception.file;

public class NoExistFileException extends RuntimeException {

    public NoExistFileException() {
        super("파일이 존재하지 않습니다.");
    }
}
