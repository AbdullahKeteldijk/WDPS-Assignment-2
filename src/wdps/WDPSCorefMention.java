package wdps;

import java.util.ArrayList;

public class WDPSCorefMention {
	public int sentenceId;
	public int startIndex;
	public int endIndex;
	public int corefChain;
	
	
	public WDPSCorefMention(int sentenceId, int startIndex, int endIndex, int corefChain) {
		super();
		this.sentenceId = sentenceId;
		this.startIndex = startIndex;
		this.endIndex = endIndex;
		this.corefChain = corefChain;
	}
	public int getSentenceId() {
		return sentenceId;
	}
	public void setSentenceId(int sentenceId) {
		this.sentenceId = sentenceId;
	}
	public int getStartIndex() {
		return startIndex;
	}
	public void setStartIndex(int startIndex) {
		this.startIndex = startIndex;
	}
	public int getEndIndex() {
		return endIndex;
	}
	public void setEndIndex(int endIndex) {
		this.endIndex = endIndex;
	}
	public int getCorefChain() {
		return corefChain;
	}
	public void setCorefChain(int corefChain) {
		this.corefChain = corefChain;
	}
	
	
}
