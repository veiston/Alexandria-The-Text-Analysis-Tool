package com.alexandria.model;

import java.time.LocalDateTime;

/**
 * Represents a text uploaded or entered by a user.
 */
public class Text {
	private Integer id;
	private Integer userId;
	private String title;
	private String fileName;
	private String filePath;
	private FileType fileType; // PDF, TXT, MANUAL
	private String content;
	private LocalDateTime createdAt;

	public Text(Integer userId, String title, String fileName, FileType fileType, String content) {
		this(userId, title, fileName, null, fileType, content);
	}

	public Text(Integer userId, String title, String fileName, String filePath, FileType fileType, String content) {
		this.userId = userId;
		this.title = title;
		this.fileName = fileName;
		this.filePath = filePath;
		this.fileType = fileType;
		this.content = content;
	}

	public Text(Integer id, Integer userId, String title, String fileName, String filePath,
			FileType fileType, String content, LocalDateTime createdAt) {
		this.id = id;
		this.userId = userId;
		this.title = title;
		this.fileName = fileName;
		this.filePath = filePath;
		this.fileType = fileType;
		this.content = content;
		this.createdAt = createdAt;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	public FileType getFileType() {
		return fileType; // PDF, TXT, MANUAL
	}

	public void setFileType(FileType fileType) {
		this.fileType = fileType; // PDF, TXT, MANUAL
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}
}
