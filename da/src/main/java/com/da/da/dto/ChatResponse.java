package com.da.da.dto;
import lombok.Data;
import java.util.List;

@Data
public class ChatResponse {
    private List<Candidate> candidates;
    @Data public static class Candidate { private Content content; }
    @Data public static class Content { private List<Part> parts; }
    @Data public static class Part { private String text; }
	public List<Candidate> getCandidates() {
		return candidates;
	}
	public void setCandidates(List<Candidate> candidates) {
		this.candidates = candidates;
	}
    
}