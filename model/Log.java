package model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Log {
	private String message = "";

	public String getMessage() {
		return message;
	}

	public void addLog(String content) {
		String new_message = message;
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");  
		LocalDateTime now = LocalDateTime.now();  
		new_message += String.format("[%s]: %s\n", dtf.format(now), content);
		this.message = new_message;
	}
}
